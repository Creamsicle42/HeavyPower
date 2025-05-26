package com.creamsicle42.heavypower.blockentity.centrifuge;

import com.creamsicle42.heavypower.ModTags;
import com.creamsicle42.heavypower.block.ModBlocks;
import com.creamsicle42.heavypower.blockentity.ModBlockEntities;
import com.creamsicle42.heavypower.blockentity.misc.GenericProcessingMachineBlockEntity;
import com.creamsicle42.heavypower.blockentity.misc.SimpleFluidHatchBlockEntity;
import com.creamsicle42.heavypower.misc.MultiblockHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.Map;

public class CentrifugeControllerBlockEntity extends GenericProcessingMachineBlockEntity {
    public CentrifugeControllerBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.CENTRIFUGE_CONTROLLER_BE.get(), pos, blockState);
    }

    public static boolean tryFormStructure(BlockPos baseControllerPos, Direction face, ServerLevel level) {
        return tryFormStructure(baseControllerPos, face, level,
                ModTags.TIER_ONE_GENERIC_MACHINE_BLOCKS,
                Map.of(
                        ModBlocks.TIER_ONE_CASING.get().defaultBlockState(), ModBlocks.CENTRIFUGE_CASING.get().defaultBlockState(),
                        ModBlocks.AUX_TIER_ONE_CASING.get().defaultBlockState(), ModBlocks.CENTRIFUGE_AUX_MOTOR.get().defaultBlockState(),
                        ModBlocks.MECHANIZED_TIER_ONE_CASING.get().defaultBlockState(), ModBlocks.CENTRIFUGE_MOTOR.get().defaultBlockState()
                ),
                ModTags.TIER_ONE_HATCH_BLOCKS,
                ModBlocks.CENTRIFUGE_CONTROLLER.get().defaultBlockState()
                );
    }

    @Override
    protected MultiblockStats initializeMachineStats(MultiblockHelper.WorldArea formationArea) {
        int motorCount = 0;
        double boosterLevel = 0.0;

        if (level == null) {
            return new MultiblockStats(1, 0.0);
        }

        for (BlockState state : formationArea.getIterator().stream().map(p -> level.getBlockState(p)).toList()) {
            if (state.is(ModBlocks.CENTRIFUGE_MOTOR)) {
                motorCount++;
                continue;
            }
            if (state.is(ModBlocks.CENTRIFUGE_AUX_MOTOR)) {
                boosterLevel += 0.1;
                continue;
            }
        }

        return new MultiblockStats(motorCount, boosterLevel);
    }

    @Override
    public Map<BlockState, BlockState> getUnformMap() {
        return Map.of(
                ModBlocks.CENTRIFUGE_CASING.get().defaultBlockState(), ModBlocks.TIER_ONE_CASING.get().defaultBlockState(),
                ModBlocks.CENTRIFUGE_AUX_MOTOR.get().defaultBlockState(), ModBlocks.AUX_TIER_ONE_CASING.get().defaultBlockState(),
                ModBlocks.CENTRIFUGE_MOTOR.get().defaultBlockState(), ModBlocks.MECHANIZED_TIER_ONE_CASING.get().defaultBlockState()
        );
    }

    @Override
    protected int getFluidInputSlotCount() {
        return 1;
    }

    @Override
    protected int getFluidOutputSlotCount() {
        return 2;
    }

    @Override
    protected int getItemInputSlotCount() {
        return 1;
    }

    @Override
    protected int getItemOutputSlotCount() {
        return 6;
    }

    @Override
    protected BlockState getFluidInputBlockState(BlockPos pos) {
        return ModBlocks.CENTRIFUGE_INPUT_HATCH.get().defaultBlockState();
    }

    @Override
    protected BlockState getFluidOutputBlockState(BlockPos pos) {
        return ModBlocks.CENTRIFUGE_OUTPUT_HATCH.get().defaultBlockState();
    }

    @Override
    protected BlockState getItemInputBlockState(BlockPos pos) {
        return ModBlocks.CENTRIFUGE_INPUT_BUS.get().defaultBlockState();
    }

    @Override
    protected BlockState getItemOutputBlockState(BlockPos pos) {
        return ModBlocks.CENTRIFUGE_OUTPUT_BUS.get().defaultBlockState();
    }

    @Override
    protected BlockState getEnergyInputBlockState(BlockPos pos) {
        return ModBlocks.CENTRIFUGE_ENERGY_INPUT.get().defaultBlockState();
    }
}

