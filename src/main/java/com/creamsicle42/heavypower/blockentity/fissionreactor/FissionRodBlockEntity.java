package com.creamsicle42.heavypower.blockentity.fissionreactor;

import com.creamsicle42.heavypower.block.ModBlocks;
import com.creamsicle42.heavypower.blockentity.ModBlockEntities;
import com.creamsicle42.heavypower.blockentity.misc.SimpleMachinePartBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

public class FissionRodBlockEntity extends SimpleMachinePartBlockEntity {

    public static final String HELD_ITEMS_TAG = "HeldItems";

    public NonNullList<ItemStack> heldItems = NonNullList.withSize(4, ItemStack.EMPTY);

    public FissionRodBlockEntity(BlockEntityType<?> pType, BlockPos pos, BlockState blockState) {
        super(pType, pos, blockState);
    }

    public FissionRodBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.FISSION_ROD_BE.get(), pos, blockState);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put(HELD_ITEMS_TAG, ContainerHelper.saveAllItems(new CompoundTag(), heldItems, registries));
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains(HELD_ITEMS_TAG)) {
            ContainerHelper.loadAllItems(tag.getCompound(HELD_ITEMS_TAG), heldItems, registries);
            System.out.println("Rod held Items = " + heldItems);
        }
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection connection, ClientboundBlockEntityDataPacket packet, HolderLookup.Provider registries) {
        super.onDataPacket(connection, packet, registries);
    }

    public static class ItemHandler implements IItemHandler {

        private final int invOffset;
        private final FissionReactorControllerBlockEntity controllerBlockEntity;
        private final FissionRodBlockEntity selfEntity;

        /**
         * Sets up item handler to manage it's relevant rods
         * @param baseBlockEntity The base block entity
         */
        public ItemHandler(FissionRodBlockEntity baseBlockEntity) {
            controllerBlockEntity = (FissionReactorControllerBlockEntity) baseBlockEntity.getController().orElse(null);
            selfEntity = baseBlockEntity;
            if (controllerBlockEntity == null || controllerBlockEntity.getMinExtents() == null) {
                invOffset = 0;
                return;
            }
            BlockPos selfPos = baseBlockEntity.getBlockPos();
            int offsetX = selfPos.getX() - controllerBlockEntity.getMinExtents().getX();
            int offsetZ = selfPos.getZ() - controllerBlockEntity.getMinExtents().getZ();
            invOffset = (offsetX + offsetZ * controllerBlockEntity.getXSize()) * 4;
        }

        /**
         * Returns the number of slots available
         *
         * @return The number of slots available
         **/
        @Override
        public int getSlots() {
            return 4;
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
        public @NotNull ItemStack getStackInSlot(int slot) {
            return controllerBlockEntity.getRods().getStackInSlot(slot + invOffset);
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
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            ItemStack out = controllerBlockEntity.getRods().insertItem(slot + invOffset, stack, simulate);
            selfEntity.heldItems.set(slot, controllerBlockEntity.getRods().getStackInSlot(slot + invOffset));
            selfEntity.setChanged();
            controllerBlockEntity.setChanged();
            return out;
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
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            ItemStack out = controllerBlockEntity.getRods().extractItem(slot + invOffset, amount, simulate);
            selfEntity.heldItems.set(slot, ItemStack.EMPTY);
            selfEntity.setChanged();
            controllerBlockEntity.setChanged();
            return out;
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


        /**
         * <p>
         * This function re-implements the vanilla function {@link Container#canPlaceItem(int, ItemStack)}.
         * It should be used instead of simulated insertions in cases where the contents and state of the inventory are
         * irrelevant, mainly for the purpose of automation and logic (for instance, testing if a minecart can wait
         * to deposit its items into a full inventory, or if the items in the minecart can never be placed into the
         * inventory and should move on).
         * </p>
         * <ul>
         * <li>isItemValid is false when insertion of the item is never valid.</li>
         * <li>When isItemValid is true, no assumptions can be made and insertion must be simulated case-by-case.</li>
         * <li>The actual items in the inventory, its fullness, or any other state are <strong>not</strong> considered by isItemValid.</li>
         * </ul>
         *
         * @param slot  Slot to query for validity
         * @param stack Stack to test with for validity
         * @return true if the slot can insert the ItemStack, not considering the current state of the inventory.
         * false if the slot can never insert the ItemStack in any situation.
         */
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return controllerBlockEntity.getRods().isItemValid(slot, stack);
        }
    }

}
