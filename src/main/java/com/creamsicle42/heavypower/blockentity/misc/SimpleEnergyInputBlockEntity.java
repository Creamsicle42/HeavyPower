package com.creamsicle42.heavypower.blockentity.misc;

import com.creamsicle42.heavypower.blockentity.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class SimpleEnergyInputBlockEntity extends SimpleMachinePartBlockEntity{
    public SimpleEnergyInputBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.ENERGY_INPUT_BE.get(), pos, blockState);
    }

    public IEnergyStorage getEnergyHandler(Direction face) {
        if (face != null && getBlockState().hasProperty(BlockStateProperties.FACING) && getBlockState().getValue(BlockStateProperties.FACING) != face) {
            return null;
        }
        return new EnergyHandler(this);
    }

    public SimpleEnergyInputBlockEntity(BlockEntityType<?> pType, BlockPos pos, BlockState blockState) {
        super(pType, pos, blockState);
    }

    public static class EnergyHandler implements IEnergyStorage {
        private final SimpleEnergyInputBlockEntity hatchBlockEntity;
        private final IEnergyStorage energyHandler;

        public EnergyHandler(SimpleEnergyInputBlockEntity inputHatchBlockEntity) {
            this.hatchBlockEntity = inputHatchBlockEntity;
            if (inputHatchBlockEntity.level instanceof ServerLevel serverLevel) {
                this.energyHandler =  inputHatchBlockEntity.getController().map(
                        iSimpleMachineController -> ((IEnergyHatchManager)iSimpleMachineController).getEnergyStorage()
                ).orElse(null);
            } else {
                // This should NEVER happen
                this.energyHandler = null;
            }
        }

        /**
         * Adds energy to the storage. Returns the amount of energy that was accepted.
         *
         * @param toReceive The amount of energy being received.
         * @param simulate  If true, the insertion will only be simulated, meaning {@link #getEnergyStored()} will not change.
         * @return Amount of energy that was (or would have been, if simulated) accepted by the storage.
         */
        @Override
        public int receiveEnergy(int toReceive, boolean simulate) {
            if (energyHandler == null) return 0;
            return energyHandler.receiveEnergy(toReceive, simulate);
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
            if (energyHandler == null) return 0;
            return energyHandler.extractEnergy(toExtract, simulate);
        }

        /**
         * Returns the amount of energy currently stored.
         */
        @Override
        public int getEnergyStored() {
            if (energyHandler == null) return 0;
            return energyHandler.getEnergyStored();
        }

        /**
         * Returns the maximum amount of energy that can be stored.
         */
        @Override
        public int getMaxEnergyStored() {
            if (energyHandler == null) return 0;
            return energyHandler.getMaxEnergyStored();
        }

        /**
         * Returns if this storage can have energy extracted.
         * If this is false, then any calls to extractEnergy will return 0.
         */
        @Override
        public boolean canExtract() {
            if (energyHandler == null) return false;
            return energyHandler.canExtract();
        }

        /**
         * Used to determine if this storage can receive energy.
         * If this is false, then any calls to receiveEnergy will return 0.
         */
        @Override
        public boolean canReceive() {
            if (energyHandler == null) return false;
            return energyHandler.canReceive();
        }
    }
}
