package com.creamsicle42.heavypower.blockentity.misc;

import com.creamsicle42.heavypower.ModTags;
import com.creamsicle42.heavypower.block.custom.misc.IMachineHatchBlock;
import com.creamsicle42.heavypower.misc.ISlotFluidHandler;
import com.creamsicle42.heavypower.misc.MultiblockHelper;
import com.creamsicle42.heavypower.recipe.inputs.GenericProcessingInput;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

import java.util.ArrayList;
import java.util.Map;

public class GenericProcessingMachineBlockEntity extends BlockEntity implements IFluidHatchManager, IEnergyHatchManager, IItemBusManager, ISimpleMachineController{

    public static final String MIN_EXTENTS_TAG = "MinExtents";
    public static final String MAX_EXTENTS_TAG = "MaxExtents";
    static final String STATS_TAG = "Stats";
    static final String FLUID_HANDLER_TAG = "FluidHandler";
    static final String ITEM_HANDLER_TAG = "ItemHandler";

    private BlockPos minExtents;
    private BlockPos maxExtents;
    private MultiblockStats stats;
    private FluidHandler fluidHandler;
    private ItemStackHandler itemHandler;
    private boolean queueCapInvalidate;

    public GenericProcessingMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (minExtents != null) tag.putLong(MIN_EXTENTS_TAG, minExtents.asLong()) ;
        if (maxExtents != null) tag.putLong(MAX_EXTENTS_TAG, maxExtents.asLong()) ;
        if (stats != null) tag.put(STATS_TAG, stats.toTag());
        if (fluidHandler != null) tag.put(FLUID_HANDLER_TAG, fluidHandler.toTag(new CompoundTag(), registries));
        if (itemHandler != null) tag.put(ITEM_HANDLER_TAG, itemHandler.serializeNBT(registries));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        minExtents = BlockPos.of(tag.getLong(MIN_EXTENTS_TAG));
        maxExtents = BlockPos.of(tag.getLong(MAX_EXTENTS_TAG));
        if (tag.contains(STATS_TAG)) {
            stats = MultiblockStats.fromTag(tag.getCompound(STATS_TAG));
        } else {
            stats = new MultiblockStats(0, 0);
        }
        if (tag.contains(FLUID_HANDLER_TAG)) {
            fluidHandler = new FluidHandler(tag.getCompound(FLUID_HANDLER_TAG), registries);
        } else {
            fluidHandler = new FluidHandler(
                    getFluidInputSlotCount() + getFluidOutputSlotCount(),
                    getFluidInputStackLimit() * stats.numParallels,
                    getFluidInputSlotCount()
            );
        }
        itemHandler = createItemHandler(
                getItemInputSlotCount() + getItemOutputSlotCount(),
                getItemInputStackLimit() * stats.numParallels);
        if (tag.contains(ITEM_HANDLER_TAG)) {
            itemHandler.deserializeNBT(registries, tag.getCompound(ITEM_HANDLER_TAG));
        }
        queueCapInvalidate = true;

    }

    /**
     * Attempt to setup block as input hatch
     *
     * @param pos The position to make an input hatch
     * @return True if the hatch has been successfully placed
     */
    @Override
    public boolean tryMakeBlockEnergyInputHatch(BlockPos pos, Direction face) {
        if (level == null) return false;
        if (!isBlockHatchReplacable(pos)) return false;
        BlockState hatchState = getEnergyInputBlockState(pos);
        if (hatchState == null) return false;

        level.setBlockAndUpdate(pos, hatchState.setValue(BlockStateProperties.FACING, face));

        if (level.getBlockEntity(pos) instanceof SimpleEnergyInputBlockEntity hatch) {
            hatch.setController(getBlockPos());
            setupHatch(pos);
            hatch.setChanged();
        }

        return true;
    }

    /**
     * Attempt to setup block as output hatch
     *
     * @param pos The position to make an output hatch
     * @return True if the hatch has been successfully placed
     */
    @Override
    public boolean tryMakeBlockEnergyOutputHatch(BlockPos pos) {
        return false;
    }

    @Override
    public IEnergyStorage getEnergyStorage() {
        return null;
    }

    @Override
    public ISlotFluidHandler getFluidHandler() {
        return fluidHandler;
    }

    /**
     * Attempt to setup block as input hatch
     *
     * @param pos The position to make an input hatch
     * @return True if the hatch has been successfully placed
     */
    @Override
    public boolean tryMakeBlockInputHatch(BlockPos pos, Direction face) {
        if (level == null) return false;
        if (!isBlockHatchReplacable(pos)) return false;
        BlockState hatchState = getFluidInputBlockState(pos);
        if (hatchState == null) return false;

        level.setBlockAndUpdate(pos, hatchState.setValue(BlockStateProperties.FACING, face));

        if (level.getBlockEntity(pos) instanceof SimpleFluidHatchBlockEntity hatch) {
            hatch.setIO(true, false);
            hatch.setController(getBlockPos());
            setupHatch(pos);
            hatch.setChanged();
        }

        return true;
    }

    /**
     * Attempt to setup block as output hatch
     *
     * @param pos The position to make an output hatch
     * @return True if the hatch has ben successfully placed
     */
    @Override
    public boolean tryMakeBlockOutputHatch(BlockPos pos, Direction face) {
        if (level == null) return false;
        if (!isBlockHatchReplacable(pos)) return false;
        BlockState hatchState = getFluidOutputBlockState(pos);
        if (hatchState == null) return false;

        level.setBlockAndUpdate(pos, hatchState.setValue(BlockStateProperties.FACING, face));


        if (level.getBlockEntity(pos) instanceof SimpleFluidHatchBlockEntity hatch) {
            hatch.setIO(false, true);
            hatch.setController(getBlockPos());
            setupHatch(pos);
            hatch.setChanged();
        }

        return true;
    }

    @Override
    public IItemHandler getItemHandler() {
        return itemHandler;
    }

    /**
     * Attempt to setup block as input hatch
     *
     * @param pos The position to make an input hatch
     * @return True if the hatch has been successfully placed
     */
    @Override
    public boolean tryMakeBlockInputBus(BlockPos pos, Direction face) {
        if (level == null) return false;
        if (!isBlockHatchReplacable(pos)) return false;
        BlockState hatchState = getItemInputBlockState(pos);
        if (hatchState == null) return false;

        level.setBlockAndUpdate(pos, hatchState.setValue(BlockStateProperties.FACING, face));

        if (level.getBlockEntity(pos) instanceof SimpleItemBusBlockEntity hatch) {
            hatch.setIO(true, false);
            hatch.setController(getBlockPos());
            setupHatch(pos);
            hatch.setChanged();
        }

        return true;
    }

    /**
     * Attempt to setup block as output hatch
     *
     * @param pos The position to make an output hatch
     * @return True if the hatch has been successfully placed
     */
    @Override
    public boolean tryMakeBlockOutputBus(BlockPos pos, Direction face) {
        if (level == null) return false;
        if (!isBlockHatchReplacable(pos)) return false;
        BlockState hatchState = getItemOutputBlockState(pos);
        if (hatchState == null) return false;

        level.setBlockAndUpdate(pos, hatchState.setValue(BlockStateProperties.FACING, face));

        if (level.getBlockEntity(pos) instanceof SimpleItemBusBlockEntity hatch) {
            hatch.setIO(false, true);
            hatch.setController(getBlockPos());
            setupHatch(pos);
            hatch.setChanged();
        }

        return true;
    }

    /**
     * Called when a component block is broken
     *
     * @param componentPos The position of the component block
     */
    @Override
    public void onComponentBreak(BlockPos componentPos) {
        if (level == null) {return;}
        ArrayList<ItemStack> itemsToDrop = getBreakItems();

        for (int i = 0; i < itemHandler.getSlots(); i++) {
            if (!itemHandler.getStackInSlot(i).isEmpty()) {
                itemsToDrop.add(itemHandler.getStackInSlot(i).copy());
            }
        }

        for (BlockPos compPos: MultiblockHelper.getAreaIter(minExtents, maxExtents)) {
            BlockState breakState = level.getBlockState(compPos);
            if (breakState.getBlock() instanceof IMachineHatchBlock hatchBlock) {
                level.setBlock(compPos, hatchBlock.getDeconstructionBlockstate(), 3);
                itemsToDrop.add(hatchBlock.getDeconstructionItemStack());
                continue;
            }
            if (getUnformMap().containsKey(breakState)) {
                level.setBlock(compPos, getUnformMap().get(breakState), 3);
            }
        }
        // Handle drops
        Containers.dropContents(level, componentPos, NonNullList.copyOf(itemsToDrop));
    }

    static ItemStackHandler createItemHandler(int stacks, int slotLimit) {
        return new ItemStackHandler(stacks) {
            @Override
            protected int getStackLimit(int slot, ItemStack stack) {
                return slotLimit;
            }
        };
    }

    public static boolean tryFormStructure(BlockPos baseControllerPos, Direction face, ServerLevel level, TagKey<Block> formationTag, Map<BlockState, BlockState> formationMap, TagKey<Block> controllerReplace, BlockState controllerState) {
        MultiblockHelper.WorldArea formationArea = MultiblockHelper.getEncompassingRect(baseControllerPos, formationTag, level).orElse(null);
        if (formationArea == null) return false;

        if (formationArea.getXSize() > getMaxSize() || formationArea.getXSize() < getMinSize()) return false;
        if (formationArea.getYSize() > getMaxSize() || formationArea.getYSize() < getMinSize()) return false;
        if (formationArea.getZSize() > getMaxSize() || formationArea.getZSize() < getMinSize()) return false;

        if (!level.getBlockState(baseControllerPos).is(controllerReplace)) return false;

        for (BlockPos pos : formationArea.getIterator()) {
            BlockState replaceState = level.getBlockState(pos);
            if (!formationMap.containsKey(replaceState)) continue;
            level.setBlockAndUpdate(pos, formationMap.get(replaceState));
            if (level.getBlockEntity(pos) instanceof SimpleMachinePartBlockEntity partBlockEntity) {
                partBlockEntity.setController(baseControllerPos);
            }
        }

        level.setBlockAndUpdate(baseControllerPos, controllerState);
        if (level.getBlockEntity(baseControllerPos) instanceof GenericProcessingMachineBlockEntity controllerBlockEntity) {
            controllerBlockEntity.stats = controllerBlockEntity.initializeMachineStats(formationArea);
            controllerBlockEntity.fluidHandler = new FluidHandler(
                    controllerBlockEntity.getFluidInputSlotCount() + controllerBlockEntity.getFluidOutputSlotCount(),
                    controllerBlockEntity.getFluidInputStackLimit() * controllerBlockEntity.stats.numParallels,
                    controllerBlockEntity.getFluidInputSlotCount()
            );
            controllerBlockEntity.itemHandler = createItemHandler(
                    controllerBlockEntity.getItemInputSlotCount() + controllerBlockEntity.getItemOutputSlotCount(),
                    controllerBlockEntity.getItemInputStackLimit() * controllerBlockEntity.stats.numParallels);
            controllerBlockEntity.minExtents = formationArea.minPos();
            controllerBlockEntity.maxExtents = formationArea.maxPos();
            controllerBlockEntity.queueCapInvalidate = true;
            controllerBlockEntity.setChanged();
        }


        return true;
    }

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, BlockEntity blockEntity) {
        if (!(blockEntity instanceof GenericProcessingMachineBlockEntity controller)) return;
        if (controller.queueCapInvalidate) {
            System.out.println("Invalidating Capabilities");
            for (BlockPos pos : MultiblockHelper.getAreaIter(controller.minExtents, controller.maxExtents)) {
                level.invalidateCapabilities(pos);
            }
            controller.queueCapInvalidate = false;
        }
    }

    public static int getMinSize() {return 2;}

    public static int getMaxSize() {return 16;}

    public static RecipeType<?> getRecipeType() {
        return null;
    };

    public ArrayList<ItemStack> getBreakItems() {
        return new ArrayList<>();
    }

    public Map<BlockState, BlockState> getUnformMap() {
        return null;
    }

    protected MultiblockStats initializeMachineStats(MultiblockHelper.WorldArea formationArea) {
        return new MultiblockStats(0, 0.0);
    }


    public GenericProcessingInput getRecipeInput() {
        return null;
    }

    protected boolean isBlockHatchReplacable(BlockPos pos) {
        return getLevel().getBlockState(pos).is(ModTags.TIER_ONE_HATCH_BLOCKS);
    }

    protected BlockState getFluidInputBlockState(BlockPos pos) {
        return null;
    }

    protected BlockState getFluidOutputBlockState(BlockPos pos) {
        return null;
    }

    protected BlockState getItemInputBlockState(BlockPos pos) {
        return null;
    }

    protected BlockState getItemOutputBlockState(BlockPos pos) {
        return null;
    }

    protected BlockState getEnergyInputBlockState(BlockPos pos) {
        return null;
    }

    protected void setupHatch(BlockPos pos) {}


    protected int getItemInputSlotCount() {
        return 1;
    }

    protected int getItemOutputSlotCount() {
        return 1;
    }

    protected int getFluidInputSlotCount() {
        return 1;
    }

    protected int getFluidOutputSlotCount() {
        return 1;
    }

    protected int getFluidInputStackLimit() {
        return 1000;
    }

    protected int getItemInputStackLimit() {
        return 64;
    }

    public record MultiblockStats(int numParallels, double boostFactor) {
        private static final String NUM_PARALLELS_TAG = "NumParallels";
        private static final String BOOST_FACTOR_TAG = "BoostFactor";

        static public MultiblockStats fromTag(CompoundTag tag) {
            int par = tag.getInt(NUM_PARALLELS_TAG);
            double boost = tag.getDouble(BOOST_FACTOR_TAG);
            return new MultiblockStats(par, boost);
        }

        public CompoundTag toTag() {
            CompoundTag tag = new CompoundTag();
            tag.putInt(NUM_PARALLELS_TAG, numParallels);
            tag.putDouble(BOOST_FACTOR_TAG, boostFactor);
            return tag;
        }
    }

    static class FluidHandler implements ISlotFluidHandler {

        private static final String SLOT_LIMIT_TAG = "SlotLimit";
        private static final String INPUT_SLOT_COUNT_TAG = "InputSlots";
        private static final String OUTPUT_SLOT_COUNT_TAG = "OutputSlots";
        private static final String FLUIDS_TAG = "Fluids";

        private final NonNullList<FluidStack> fluids;
        private final int slotLimit;
        private final int inputSlots;

        public FluidHandler(CompoundTag tag, HolderLookup.Provider provider) {
            slotLimit = tag.getInt(SLOT_LIMIT_TAG);
            inputSlots = tag.getInt(INPUT_SLOT_COUNT_TAG);
            int outputSlots = tag.getInt(OUTPUT_SLOT_COUNT_TAG);

            fluids = NonNullList.withSize(inputSlots + outputSlots, FluidStack.EMPTY);

            ListTag fluidTags = tag.getList(FLUIDS_TAG, CompoundTag.TAG_COMPOUND);
            for (int i = 0; i < fluidTags.size(); i++) {
                fluids.set(i, FluidStack.parseOptional(provider, fluidTags.getCompound(i)));
            }

        }

        public FluidHandler(int fluidCount, int slotLimit, int inputSlots) {
            this.slotLimit = slotLimit;
            fluids = NonNullList.withSize(fluidCount, FluidStack.EMPTY);
            this.inputSlots = inputSlots;
        }

        public CompoundTag toTag(CompoundTag tag, HolderLookup.Provider provider) {
            tag.putInt(SLOT_LIMIT_TAG, slotLimit);
            tag.putInt(INPUT_SLOT_COUNT_TAG, inputSlots);
            tag.putInt(OUTPUT_SLOT_COUNT_TAG, fluids.size() - inputSlots);

            ListTag fluidTag = new ListTag();

            for (int i = 0; i < fluids.size(); i++) {
                if (fluids.get(i).isEmpty()) {
                    fluidTag.addTag(i, new CompoundTag());
                    continue;
                }
                fluidTag.addTag(i, fluids.get(i).save(provider));
            }

            tag.put(FLUIDS_TAG, fluidTag);

            return tag;
        }

        /**
         * Returns the number of fluid storage units ("tanks") available
         *
         * @return The number of tanks available
         */
        @Override
        public int getTanks() {
            return fluids.size();
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
            if (fluids.size() <= tank) return FluidStack.EMPTY;
            return fluids.get(tank);
        }

        /**
         * Retrieves the maximum fluid amount for a given tank.
         *
         * @param tank Tank to query.
         * @return The maximum fluid amount held by the tank.
         */
        @Override
        public int getTankCapacity(int tank) {
            return slotLimit;
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
            return fluids.get(tank).isEmpty() || fluids.get(tank).is(stack.getFluid());
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
            FluidStack stackToUse = null;
            FluidStack firstEmptyStack = null;

            // Find the first empty stack, and the first stack of this fluid type
            for (int i = 0; i < inputSlots; i++) {
                FluidStack testStack = fluids.get(i);
                if (firstEmptyStack == null && testStack.isEmpty())
                    firstEmptyStack = testStack;
                if (!testStack.isEmpty() && testStack.is(resource.getFluidType())) {
                    stackToUse = testStack;
                    break;
                }
            }

            // Use first empty stack if no stack of fluid type is found
            if (stackToUse == null) {
                stackToUse = firstEmptyStack;
            }

            // If no stack can be used, then mb is full
            if (stackToUse == null) {
                return 0;
            }

            int emptySpace = slotLimit - stackToUse.getAmount();
            int fillAmmount = Math.min(resource.getAmount(), emptySpace);

            if (action.execute()) {
                stackToUse.grow(fillAmmount);
            }

            return fillAmmount;
        }

        @Override
        public int fill(int targetSlot, FluidStack resource, FluidAction action) {
            FluidStack fluid = fluids.get(targetSlot);

            if (resource.isEmpty() || !isFluidValid(targetSlot, resource)) {
                return 0;
            }
            if (action.simulate()) {
                if (fluid.isEmpty()) {
                    return Math.min(slotLimit, resource.getAmount());
                }
                if (!FluidStack.isSameFluidSameComponents(fluid, resource)) {
                    return 0;
                }
                return Math.min(slotLimit - fluid.getAmount(), resource.getAmount());
            }
            if (fluid.isEmpty()) {
                fluids.set(targetSlot, resource.copyWithAmount(Math.min(slotLimit, resource.getAmount())));
                return fluids.get(targetSlot).getAmount();
            }
            if (!FluidStack.isSameFluidSameComponents(fluid, resource)) {
                return 0;
            }
            int filled = slotLimit - fluid.getAmount();

            if (resource.getAmount() < filled) {
                fluid.grow(resource.getAmount());
                filled = resource.getAmount();
            } else {
                fluid.setAmount(slotLimit);
            }

           return filled;
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
            FluidStack stackToUse = null;
            for(int i = inputSlots; i < fluids.size(); i++) {
                if (fluids.get(i).is(resource.getFluidType())) {
                    stackToUse = fluids.get(i);
                    break;
                }
            }
            if (stackToUse == null) {return FluidStack.EMPTY;}
            int drainAmmount = Math.min(resource.getAmount(), stackToUse.getAmount());
            if (action.execute()) stackToUse.shrink(drainAmmount);
            return stackToUse.copyWithAmount(stackToUse.getAmount() - drainAmmount);
        }

        @Override
        public FluidStack drain(int targetSlot, FluidStack resource, FluidAction action) {
            FluidStack fluid = fluids.get(targetSlot);
            if (resource.isEmpty() || !FluidStack.isSameFluidSameComponents(resource, fluid)) {
                return FluidStack.EMPTY;
            }
            return drain(targetSlot, resource.getAmount(), action);
        }

        @Override
        public FluidStack drain(int targetSlot, int maxDrain, FluidAction action) {
            FluidStack fluid = fluids.get(targetSlot);
            int drained = maxDrain;
            if (fluid.getAmount() < drained) {
                drained = fluid.getAmount();
            }
            FluidStack stack = fluid.copyWithAmount(drained);
            if (action.execute() && drained > 0) {
                fluid.shrink(drained);
            }
            return stack;
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
            FluidStack stackToUse = null;
            for(int i = inputSlots; i < fluids.size(); i++) {
                if (!fluids.get(i).isEmpty()) {
                    stackToUse = fluids.get(i);
                    break;
                }
            }
            if (stackToUse == null) {return FluidStack.EMPTY;}
            int drainAmmount = Math.min(maxDrain, stackToUse.getAmount());
            if (action.execute()) stackToUse.shrink(drainAmmount);
            return stackToUse.copyWithAmount(stackToUse.getAmount() - drainAmmount);
        }
    }

}
