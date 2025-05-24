package com.creamsicle42.heavypower.blockentity.fissionreactor;

import com.creamsicle42.heavypower.blockentity.ModBlockEntities;
import com.creamsicle42.heavypower.blockentity.misc.SimpleMachinePartBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class FissionComputerHatchBlockEntity extends SimpleMachinePartBlockEntity {
    public FissionComputerHatchBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.FISSION_COMPUTER_HATCH_BE.get(), pos, blockState);
    }

    public FissionComputerHatchBlockEntity(BlockEntityType<?> pType, BlockPos pos, BlockState blockState) {
        super(pType, pos, blockState);
    }
}
