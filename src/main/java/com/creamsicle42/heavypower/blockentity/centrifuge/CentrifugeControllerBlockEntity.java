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
    public void initializeMachineStats(MultiblockHelper.WorldArea formationArea) {
        super.initializeMachineStats(formationArea);
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
    public boolean tryMakeBlockInputHatch(BlockPos pos, Direction face) {
        if (level == null) return false;
        if (!level.getBlockState(pos).is(ModBlocks.CENTRIFUGE_CASING)) return false;

        level.setBlockAndUpdate(pos, ModBlocks.CENTRIFUGE_INPUT_HATCH.get().defaultBlockState().setValue(BlockStateProperties.FACING, face));

        if (level.getBlockEntity(pos) instanceof SimpleFluidHatchBlockEntity hatch) {
            hatch.setTargetTank(0);
            hatch.setIO(true, false);
            hatch.setController(getBlockPos());
            hatch.setChanged();
        }

        return true;
    }
}

