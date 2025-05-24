package com.creamsicle42.heavypower.blockentity.misc;

import com.creamsicle42.heavypower.blockentity.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class SimpleEnergyOutputBlockEntity extends SimpleMachinePartBlockEntity{
    public SimpleEnergyOutputBlockEntity( BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.ENERGY_OUTPUT_BE.get(), pos, blockState);
    }

    public SimpleEnergyOutputBlockEntity(BlockEntityType<?> pType, BlockPos pos, BlockState blockState) {
        super(pType, pos, blockState);
    }

    public int distributeEnergy(int toDistribute) {
        if (getLevel() == null || getLevel().isClientSide()) {return toDistribute;}
        for (Direction direction : Direction.values()) {
            if (toDistribute == 0)
                break;

            IEnergyStorage targetStorage = getLevel().getCapability(
                    Capabilities.EnergyStorage.BLOCK, getBlockPos().relative(direction), direction);

            if (targetStorage == null || !targetStorage.canReceive())
                continue;

            toDistribute -= targetStorage.receiveEnergy(toDistribute, false);
        }

        return toDistribute;
    }

    public static class EnergyHandler implements IEnergyStorage {

        /**
         * Adds energy to the storage. Returns the amount of energy that was accepted.
         *
         * @param toReceive The amount of energy being received.
         * @param simulate  If true, the insertion will only be simulated, meaning {@link #getEnergyStored()} will not change.
         * @return Amount of energy that was (or would have been, if simulated) accepted by the storage.
         */
        @Override
        public int receiveEnergy(int toReceive, boolean simulate) {
            return 0;
        }

        /**
         * Removes energy from the storage. Returns the amount of energy that was removed.
         *
         * @param toExtract The amount of energy being extracted.
         * @param simulate  If true, the extraction will only be simulated, meaning {@link #getEnergyStored()} will not change.
         * @return Amount of energy that was (or would have been, if simulated) extracted from the storage.
         */
        @Override
        public int extractEnergy(int toExtract, boolean simulate) {
            return 0;
        }

        /**
         * Returns the amount of energy currently stored.
         */
        @Override
        public int getEnergyStored() {
            return 0;
        }

        /**
         * Returns the maximum amount of energy that can be stored.
         */
        @Override
        public int getMaxEnergyStored() {
            return 0;
        }

        /**
         * Returns if this storage can have energy extracted.
         * If this is false, then any calls to extractEnergy will return 0.
         */
        @Override
        public boolean canExtract() {
            return true;
        }

        /**
         * Used to determine if this storage can receive energy.
         * If this is false, then any calls to receiveEnergy will return 0.
         */
        @Override
        public boolean canReceive() {
            return false;
        }
    }
}
