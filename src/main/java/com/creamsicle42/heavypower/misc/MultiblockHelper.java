package com.creamsicle42.heavypower.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MultiblockHelper {
    public record WorldArea(BlockPos minPos, BlockPos maxPos) {
        public List<BlockPos> getIterator() {
            return getAreaIter(minPos, maxPos);
        }

        @Override
        public String toString() {
            return "WorldArea(" + minPos + " to " + maxPos + ")";
        }

        public int getXSize() {
            return (maxPos().getX() - minPos.getX()) + 1;
        }

        public int getYSize() {
            return (maxPos().getY() - minPos.getY()) + 1;
        }

        public int getZSize() {
            return (maxPos().getZ() - minPos.getZ()) + 1;
        }
    }


    public static List<BlockPos> getAreaIter(BlockPos minPos, BlockPos maxPos) {
        ArrayList<BlockPos> out = new ArrayList<>();
        if (minPos == null) {return out;}
        if (maxPos == null) {return out;}
        for (int x = minPos.getX(); x <= maxPos.getX(); x++) {
            for (int y = minPos.getY(); y <= maxPos.getY(); y++) {
                for (int z = minPos.getZ(); z <= maxPos.getZ(); z++) {
                    out.add(new BlockPos(x, y, z));
                }
            }
        }
        return out;
    }

    public static List<BlockPos> getRingIter(BlockPos cornerPos, int iLen, int jLen, Direction iBasis, Direction jBasis) {
        ArrayList<BlockPos> out = new ArrayList<>();

        for (int i = 0; i < iLen; i++) {
            out.add(cornerPos.relative(iBasis, i).relative(jBasis, 0));
            out.add(cornerPos.relative(iBasis, i).relative(jBasis, jLen - 1));
        }
        for (int j = 1; j < jLen - 1; j++) {
            out.add(cornerPos.relative(jBasis, j).relative(iBasis, 0));
            out.add(cornerPos.relative(jBasis, j).relative(iBasis, iLen - 1));
        }

        return out;
    }

    public static Optional<WorldArea> getEncompassingRect(BlockPos basePos, TagKey<Block> blockTagKey, Level level) {
        return getEncompassingRect(basePos, blockTagKey, level, 2, 16);
    }


    public static Optional<WorldArea> getEncompassingRect(BlockPos basePos, TagKey<Block> blockTagKey, Level level, int minSize, int maxSize) {
        // Get the farthest block in each direction that is the same block type
        int posX = basePos.getX() + getFurthestBlockInDir(level, blockTagKey, basePos, Direction.EAST);
        int negX = basePos.getX() - getFurthestBlockInDir(level, blockTagKey, basePos, Direction.WEST);
        int posY = basePos.getY() + getFurthestBlockInDir(level, blockTagKey, basePos, Direction.UP);
        int negY = basePos.getY() - getFurthestBlockInDir(level, blockTagKey, basePos, Direction.DOWN);
        int posZ = basePos.getZ() + getFurthestBlockInDir(level, blockTagKey, basePos, Direction.SOUTH);
        int negZ = basePos.getZ() - getFurthestBlockInDir(level, blockTagKey, basePos, Direction.NORTH);


        // Ensure that what has been found is a solid block
        for (int x = negX; x <= posX; x++) {
            for (int y = negY; y <= posY; y++) {
                for (int z = negZ; z <= posZ; z++) {
                    if (!level.getBlockState(new BlockPos(x,y,z)).is(blockTagKey)) {
                        return Optional.empty();
                    }
                }
            }
        }
        return Optional.of(new WorldArea(new BlockPos(negX, negY, negZ), new BlockPos(posX, posY, posZ)));
    }

    private static int getFurthestBlockInDir(Level level, TagKey<Block> type, BlockPos basePos, Direction direction) {
        int distance = 0;
        boolean running = true;
        while (running) {
            BlockPos newBlockPos = basePos.relative(direction, distance + 1);

            BlockState blockType = level.getBlockState(newBlockPos);

            if (!blockType.is(type)) {
                running = false;
            } else {
                distance++;
            }


        }
        return distance;
    }
}
