package com.creamsicle42.heavypower.blockentity.turbine;

import com.creamsicle42.heavypower.ModTags;
import com.creamsicle42.heavypower.blockentity.ModCapabilities;
import com.creamsicle42.heavypower.blockentity.misc.ISimpleMachineController;
import com.creamsicle42.heavypower.blockentity.ModBlockEntities;
import com.creamsicle42.heavypower.blockentity.misc.SimpleMachinePartBlockEntity;
import com.creamsicle42.heavypower.menu.custom.TurbineBladeHatchMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.checkerframework.checker.units.qual.C;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TurbineBladeHatchBlockEntity extends SimpleMachinePartBlockEntity implements MenuProvider {

    public static final String HATCH_LAYER_TAG = "HatchLayer";
    public static final String HELD_ITEM_TAG = "HeldItem";


    private int hatchLayer = -1;

    public TurbineBladeHatchBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.TURBINE_BLADE_HATCH_BE.get(), pos, blockState);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt(HATCH_LAYER_TAG, hatchLayer);

    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        hatchLayer = tag.contains(HATCH_LAYER_TAG) ? tag.getInt(HATCH_LAYER_TAG) : -1;

    }



    public int getHatchLayer() {
        return hatchLayer;
    }





    /**
     * Notifies the controller of breaking this block, if needed
     */
    public void breakBlock() {
        if (!getLevel().isClientSide()) {return;}
        if (controllerPosition == null) {return;}
        if (getLevel().getBlockEntity(controllerPosition) instanceof ISimpleMachineController controller) {
            controller.onComponentBreak(getBlockPos());
        }
    }

    public void setHatchLayer(int hatchLayer) {
        this.hatchLayer = hatchLayer;
    }




    @Override
    public Component getDisplayName() {
        return Component.translatable("heavypower:turbine_blade_hatch");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new TurbineBladeHatchMenu(containerId, playerInventory, this,
                player.level().getCapability(
                Capabilities.ItemHandler.BLOCK, controllerPosition, Direction.EAST),
                hatchLayer);
    }

    public static class ItemHandler implements IItemHandler, IItemHandlerModifiable {

        private final TurbineBladeHatchBlockEntity entity;

        public ItemHandler(BlockEntity pBlockEntity) {

            entity = (TurbineBladeHatchBlockEntity) pBlockEntity;
        }

        /**
         * Returns the number of slots available
         *
         * @return The number of slots available
         **/
        @Override
        public int getSlots() {
            return 1;
        }

        /**
         * Returns the ItemStack in a given slot.
         * <p>
         * The result's stack size may be greater than the itemstack's max size.
         * <p>
         * If the result is empty, then the slot is empty.
         *
         * <p>
         * <strong>IMPORTANT:</strong> This ItemStack <em>MUST NOT</em> be modified. This method is not for
         * altering an inventory's contents. Any implementers who are able to detect
         * modification through this method should throw an exception.
         * </p>
         * <p>
         * <strong><em>SERIOUSLY: DO NOT MODIFY THE RETURNED ITEMSTACK</em></strong>
         * </p>
         *
         * @param slot Slot to query
         * @return ItemStack in given slot. Empty Itemstack if the slot is empty.
         **/
        @Override
        public ItemStack getStackInSlot(int slot) {
            TurbineControllerBlockEntity controller =
                    (TurbineControllerBlockEntity) entity.getController().orElse(null);
            if (controller == null) {return ItemStack.EMPTY;}
            return controller.blades.get(entity.getHatchLayer());
        }

        /**
         * <p>
         * Inserts an ItemStack into the given slot and return the remainder.
         * The ItemStack <em>should not</em> be modified in this function!
         * </p>
         * Note: This behaviour is subtly different from {@link IFluidHandler#fill(FluidStack, IFluidHandler.FluidAction)}
         *
         * @param slot     Slot to insert into.
         * @param stack    ItemStack to insert. This must not be modified by the item handler.
         * @param simulate If true, the insertion is only simulated
         * @return The remaining ItemStack that was not inserted (if the entire stack is accepted, then return an empty ItemStack).
         * May be the same as the input ItemStack if unchanged, otherwise a new ItemStack.
         * The returned ItemStack can be safely modified after.
         **/
        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            TurbineControllerBlockEntity controller =
                    (TurbineControllerBlockEntity) entity.getController().orElse(null);
            if (controller == null) {return stack;}
            ItemStack existingStack = controller.blades.get(entity.getHatchLayer());
            if (!existingStack.isEmpty()) {
                return stack;
            }
            ItemStack newStack = stack.copy();
            newStack.shrink(1);
            if (!simulate) {
                controller.blades.set(entity.getHatchLayer(), stack.copyWithCount(1));
                controller.setChanged();
            }
            return newStack;
        }

        /**
         * Extracts an ItemStack from the given slot.
         * <p>
         * The returned value must be empty if nothing is extracted,
         * otherwise its stack size must be less than or equal to {@code amount} and {@link ItemStack#getMaxStackSize()}.
         * </p>
         *
         * @param slot     Slot to extract from.
         * @param amount   Amount to extract (may be greater than the current stack's max limit)
         * @param simulate If true, the extraction is only simulated
         * @return ItemStack extracted from the slot, must be empty if nothing can be extracted.
         * The returned ItemStack can be safely modified after, so item handlers should return a new or copied stack.
         **/
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            TurbineControllerBlockEntity controller =
                    (TurbineControllerBlockEntity) entity.getController().orElse(null);
            if (controller == null) {return ItemStack.EMPTY;}
            ItemStack existingStack = controller.blades.get(entity.getHatchLayer());
            if (!simulate) {
                controller.blades.set(entity.getHatchLayer(), ItemStack.EMPTY);
                controller.setChanged();
            }
            return existingStack.copy();
        }

        /**
         * Retrieves the maximum stack size allowed to exist in the given slot.
         *
         * @param slot Slot to query.
         * @return The maximum stack size allowed in the slot.
         */
        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return stack.is(ModTags.TURBINE_STAGES);
        }

        /**
         * Overrides the stack in the given slot. This method is used by the
         * standard Forge helper methods and classes. It is not intended for
         * general use by other mods, and the handler may throw an error if it
         * is called unexpectedly.
         *
         * @param slot  Slot to modify
         * @param stack ItemStack to set slot to (may be empty).
         * @throws RuntimeException if the handler is called in a way that the handler
         *                          was not expecting.
         **/
        @Override
        public void setStackInSlot(int slot, ItemStack stack) {
            TurbineControllerBlockEntity controller =
                    (TurbineControllerBlockEntity) entity.getController().orElse(null);
            if (controller == null) {return ;}
            controller.blades.set(entity.getHatchLayer(), stack);
            controller.setChanged();
        }
    }
}
