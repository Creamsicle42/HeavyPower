package com.creamsicle42.heavypower.blockentity.misc;

import com.creamsicle42.heavypower.blockentity.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

public class SimpleItemBusBlockEntity extends SimpleMachinePartBlockEntity{

    public static final String TARGET_SLOT_ID = "TargetSlot";
    public static final String ALLOW_INPUT_TAG = "AllowInput";
    public static final String ALLOW_OUTPUT_TAG = "AllowOutput";

    private boolean allowInput = false;
    private boolean allowOutput = false;
    private int targetSlot = 0;

    public SimpleItemBusBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.SIMPLE_ITEM_BUS_BE.get(), pos, blockState);
    }

    public SimpleItemBusBlockEntity(BlockEntityType<?> pType, BlockPos pos, BlockState blockState) {
        super(pType, pos, blockState);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt(TARGET_SLOT_ID, targetSlot);
        tag.putBoolean(ALLOW_INPUT_TAG, allowInput);
        tag.putBoolean(ALLOW_OUTPUT_TAG, allowOutput);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        targetSlot = tag.contains(TARGET_SLOT_ID) ? tag.getInt(TARGET_SLOT_ID) : 0;
        allowOutput = tag.contains(ALLOW_OUTPUT_TAG) && tag.getBoolean(ALLOW_OUTPUT_TAG);
        allowInput = tag.contains(ALLOW_INPUT_TAG) && tag.getBoolean(ALLOW_INPUT_TAG);
    }

    public void setIO(boolean in, boolean out) {
        this.allowInput = in;
        this.allowOutput = out;
    }

    public void setTargetSlot(int targetSlot) {
        this.targetSlot = targetSlot;
    }


    public static class ItemHandler implements IItemHandler {
        private final SimpleItemBusBlockEntity busBlockEntity;
        private final IItemHandler itemHandler;

        public ItemHandler(SimpleItemBusBlockEntity inputHatchBlockEntity) {
            this.busBlockEntity = inputHatchBlockEntity;
            if (inputHatchBlockEntity.level instanceof ServerLevel serverLevel) {
                this.itemHandler =  inputHatchBlockEntity.getController().map(
                        iSimpleMachineController -> ((IItemBusManager)iSimpleMachineController).getItemHandler()
                ).orElse(null);
            } else {
                // This should NEVER happen
                this.itemHandler = null;
            }
        }

        @Override
        public int getSlots() {
            return 1;
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            if (itemHandler == null) return ItemStack.EMPTY;
            return itemHandler.getStackInSlot(busBlockEntity.targetSlot);
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (itemHandler == null) return stack;
            if (!busBlockEntity.allowInput) return stack;
            return itemHandler.insertItem(busBlockEntity.targetSlot, stack, simulate);
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (itemHandler == null) return ItemStack.EMPTY;
            if (!busBlockEntity.allowInput) return ItemStack.EMPTY;
            return itemHandler.extractItem(busBlockEntity.targetSlot, amount, simulate);
        }

        @Override
        public int getSlotLimit(int slot) {
            if (itemHandler == null) return 0;
            return itemHandler.getSlotLimit(busBlockEntity.targetSlot);
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (itemHandler == null) return false;
            return itemHandler.isItemValid(slot, stack);
        }
    }
}
