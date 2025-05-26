package com.creamsicle42.heavypower.blockentity.misc;

import com.creamsicle42.heavypower.blockentity.ModBlockEntities;
import com.creamsicle42.heavypower.misc.ISlotFluidHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

public class SimpleFluidHatchBlockEntity extends SimpleMachinePartBlockEntity{

    public static final String TARGET_TANK_ID = "TargetTank";
    public static final String ALLOW_INPUT_TAG = "AllowInput";
    public static final String ALLOW_OUTPUT_TAG = "AllowOutput";

    private boolean allowInput = false;
    private boolean allowOutput = false;
    private int targetTank = 0;

    public SimpleFluidHatchBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.FLUID_HATCH_BE.get(), pos, blockState);
    }

    public SimpleFluidHatchBlockEntity(BlockEntityType<?> pType, BlockPos pos, BlockState blockState) {
        super(pType, pos, blockState);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt(TARGET_TANK_ID, targetTank);
        tag.putBoolean(ALLOW_INPUT_TAG, allowInput);
        tag.putBoolean(ALLOW_OUTPUT_TAG, allowOutput);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        targetTank = tag.contains(TARGET_TANK_ID) ? tag.getInt(TARGET_TANK_ID) : 0;
        allowOutput = tag.contains(ALLOW_OUTPUT_TAG) && tag.getBoolean(ALLOW_OUTPUT_TAG);
        allowInput = tag.contains(ALLOW_INPUT_TAG) && tag.getBoolean(ALLOW_INPUT_TAG);
    }

    public void setIO(boolean in, boolean out) {
        this.allowInput = in;
        this.allowOutput = out;
    }

    public void printDiagnostics(Direction face) {
        IFluidHandler handler = level.getCapability(Capabilities.FluidHandler.BLOCK, getBlockPos(), face);
        Player player = Minecraft.getInstance().player;
        player.sendSystemMessage(Component.literal("Diagnostics for fluid hatch " + getBlockPos() + " side " + face));
        if (handler == null) {
            player.sendSystemMessage(Component.literal("+ No handler for this side."));
        } else {
            for (int i = 0; i < handler.getTanks(); i++) {
                player.sendSystemMessage(Component.literal("+ Tank " + i));
                player.sendSystemMessage(Component.literal(" - Stack: " + handler.getFluidInTank(i)));
            }
        }
    }

    public void setTargetTank(int targetTank) {
        this.targetTank = targetTank;
    }


    public IFluidHandler getFluidHandler(Direction face) {
        if (getBlockState().hasProperty(BlockStateProperties.FACING) && getBlockState().getValue(BlockStateProperties.FACING) != face) {
            return null;
        }
        return new FluidHandler(this);
    }


    public static class FluidHandler implements IFluidHandler {

        private final SimpleFluidHatchBlockEntity hatchBlockEntity;
        private final ISlotFluidHandler fluidHandler;
        private final int slot;

        public FluidHandler(SimpleFluidHatchBlockEntity inputHatchBlockEntity) {
            this.hatchBlockEntity = inputHatchBlockEntity;
            slot = hatchBlockEntity.targetTank;
            if (inputHatchBlockEntity.level instanceof ServerLevel serverLevel) {
                this.fluidHandler =  inputHatchBlockEntity.getController().map(
                        iSimpleMachineController -> ((IFluidHatchManager)iSimpleMachineController).getFluidHandler()
                ).orElse(null);
            } else {
                // This should NEVER happen
                this.fluidHandler = null;
            }
        }

        /**
         * Returns the number of fluid storage units ("tanks") available
         *
         * @return The number of tanks available
         */
        @Override
        public int getTanks() {
            if (fluidHandler == null) {return 0;}
            return 1;
        }

        /**
         * Returns the FluidStack in a given tank.
         *
         * <p>
         * <strong>IMPORTANT:</strong> This FluidStack <em>MUST NOT</em> be modified. This method is not for
         * altering internal contents. Any implementers who are able to detect modification via this method
         * should throw an exception. It is ENTIRELY reasonable and likely that the stack returned here will be a copy.
         * </p>
         *
         * <p>
         * <strong><em>SERIOUSLY: DO NOT MODIFY THE RETURNED FLUIDSTACK</em></strong>
         * </p>
         *
         * @param tank Tank to query.
         * @return FluidStack in a given tank. FluidStack.EMPTY if the tank is empty.
         */
        @Override
        public FluidStack getFluidInTank(int tank) {
            if (fluidHandler == null) {return FluidStack.EMPTY;}
            return fluidHandler.getFluidInTank(slot);
        }

        /**
         * Retrieves the maximum fluid amount for a given tank.
         *
         * @param tank Tank to query.
         * @return The maximum fluid amount held by the tank.
         */
        @Override
        public int getTankCapacity(int tank) {
            return fluidHandler.getTankCapacity(slot);
        }

        /**
         * This function is a way to determine which fluids can exist inside a given handler. General purpose tanks will
         * basically always return TRUE for this.
         *
         * @param tank  Tank to query for validity
         * @param stack Stack to test with for validity
         * @return TRUE if the tank can hold the FluidStack, not considering current state.
         * (Basically, is a given fluid EVER allowed in this tank?) Return FALSE if the answer to that question is 'no.'
         */
        @Override
        public boolean isFluidValid(int tank, FluidStack stack) {
            return fluidHandler.isFluidValid(slot, stack);
        }

        /**
         * Fills fluid into internal tanks, distribution is left entirely to the IFluidHandler.
         *
         * @param resource FluidStack representing the Fluid and maximum amount of fluid to be filled.
         * @param action   If SIMULATE, fill will only be simulated.
         * @return Amount of resource that was (or would have been, if simulated) filled.
         */
        @Override
        public int fill(FluidStack resource, FluidAction action) {
            if (fluidHandler == null) {
                return 0;}
            if (!hatchBlockEntity.allowInput) {
                return 0;}
            return fluidHandler.fill(slot, resource, action);
        }

        /**
         * Drains fluid out of internal tanks, distribution is left entirely to the IFluidHandler.
         *
         * @param resource FluidStack representing the Fluid and maximum amount of fluid to be drained.
         * @param action   If SIMULATE, drain will only be simulated.
         * @return FluidStack representing the Fluid and amount that was (or would have been, if
         * simulated) drained.
         */
        @Override
        public FluidStack drain(FluidStack resource, FluidAction action) {
            if (!hatchBlockEntity.allowOutput) {return FluidStack.EMPTY;}
            if (fluidHandler == null) {return FluidStack.EMPTY;}
            return fluidHandler.drain(slot, resource, action);
        }

        /**
         * Drains fluid out of internal tanks, distribution is left entirely to the IFluidHandler.
         * <p>
         * This method is not Fluid-sensitive.
         *
         * @param maxDrain Maximum amount of fluid to drain.
         * @param action   If SIMULATE, drain will only be simulated.
         * @return FluidStack representing the Fluid and amount that was (or would have been, if
         * simulated) drained.
         */
        @Override
        public FluidStack drain(int maxDrain, FluidAction action) {
            if (!hatchBlockEntity.allowOutput) {return FluidStack.EMPTY;}
            if (fluidHandler == null) {return FluidStack.EMPTY;}
            return fluidHandler.drain(slot, maxDrain, action);
        }
    }
}
