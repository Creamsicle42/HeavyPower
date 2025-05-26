package com.creamsicle42.heavypower.blockentity.fissionreactor;

import com.creamsicle42.heavypower.ModTags;
import com.creamsicle42.heavypower.block.ModBlocks;
import com.creamsicle42.heavypower.block.custom.misc.IMachineHatchBlock;
import com.creamsicle42.heavypower.blockentity.ModBlockEntities;
import com.creamsicle42.heavypower.blockentity.misc.*;
import com.creamsicle42.heavypower.misc.ISlotFluidHandler;
import com.creamsicle42.heavypower.misc.MultiblockHelper;
import com.creamsicle42.heavypower.misc.SerializationHelper;
import com.creamsicle42.heavypower.recipe.ModRecipeTypes;
import com.creamsicle42.heavypower.recipe.inputs.FluidHeatingRecipeInput;
import com.creamsicle42.heavypower.recipe.types.FluidHeatingRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


/**
 * BE Class for the fission reactor core controller
 */
public class FissionReactorControllerBlockEntity extends BlockEntity implements ISimpleMachineController, IFluidHatchManager, IComputerHatchManager {

    public static final String ROD_INVENTORY_TAG = "RodInventory";
    public static final String MIN_EXTENTS_TAG = "MinExtents";
    public static final String MAX_EXTENTS_TAG = "MaxExtents";
    public static final String TANK_TAG = "Tank";
    public static final String HEAT_OUTPUT_TAG = "HeatPerTick";
    public static final String HEATS_TAG = "CoreHeats";
    public static final String HOT_NEUTRONS_TAG = "HotNeutrons";
    public static final String COOL_NEUTRONS_TAG = "CoolNeutrons";

    /**
     * Holder for the rod inventory. Rod inventory is laid out as a 2d grid, with each in-world block representing four contiguous item slots
     * The X world axis is the row axis and Z the collumn axis, within a four slot block segment items are laid out in a similar fashion, ie...
     *    +x>
     * +z 12 56
     *  V 34 78
     */
    private ItemStackHandler rods;
    private BlockPos minExtents;
    private BlockPos maxExtents;
    private ReactorTank tank;
    private double[][] heats;
    private double[][] hotNeutrons;
    private double[][] coolNeutrons;
    private int heatPerTick;
    private double averageTemperature;
    private double hottestCore;

    // Cached variables, not saved
    private FluidHeatingRecipe workingRecipe;
    private int tickCounter;
    private boolean isCoolingBlocked;
    private boolean cacheInvalQueue;

    public FissionReactorControllerBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.FISSION_CONTROLLER_BE.get(), pos, blockState);
    }

    public ItemStackHandler getRods() {
        return rods;
    }

    public BlockPos getMinExtents() {
        return minExtents;
    }

    public BlockPos getMaxExtents() {
        return maxExtents;
    }

    public double getAverageTemperature () {
        return averageTemperature;
    }

    public double getHottestCore() {
        return hottestCore;
    }

    public int getXSize() {
        return (maxExtents.getX() - minExtents.getX()) + 1;
    }

    public int getYSize() {
        return (maxExtents.getY() - minExtents.getY()) + 1;
    }

    public int getZSize() {
        return (maxExtents.getZ() - minExtents.getZ()) + 1;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        tag.putLong(MIN_EXTENTS_TAG, minExtents == null ? 0 : minExtents.asLong());
        tag.putLong(MAX_EXTENTS_TAG, maxExtents == null ? 0 : maxExtents.asLong());
        if (tank != null)
            tag.put(TANK_TAG, tank.toTag(new CompoundTag(), registries));
        if (rods != null)
            tag.put(ROD_INVENTORY_TAG, rods.serializeNBT(registries));
        tag.putInt(HEAT_OUTPUT_TAG, heatPerTick);
        tag.put(HEATS_TAG, SerializationHelper.gridToTag(heats, new CompoundTag()));
        tag.put(HOT_NEUTRONS_TAG, SerializationHelper.gridToTag(coolNeutrons, new CompoundTag()));
        tag.put(COOL_NEUTRONS_TAG, SerializationHelper.gridToTag(hotNeutrons, new CompoundTag()));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        maxExtents = BlockPos.of(tag.getLong(MAX_EXTENTS_TAG));
        minExtents = BlockPos.of(tag.getLong(MIN_EXTENTS_TAG));
        int xSize = 1 + (maxExtents.getX() - minExtents.getX());
        int zSize = 1 + (maxExtents.getZ() - minExtents.getZ());
        int ySize = 1 + (maxExtents.getY() - minExtents.getY());
        rods = createItemHandler(zSize * xSize * 4);
        if (tag.contains(ROD_INVENTORY_TAG)) {
            rods.deserializeNBT(registries, tag.getCompound(ROD_INVENTORY_TAG));
        }
        tank = createFluidTank(xSize * zSize * ySize);
        if (tag.contains(TANK_TAG)) {
            tank.readTag(tag.getCompound(TANK_TAG), registries);
        }
        if (tag.contains(HOT_NEUTRONS_TAG)) {
            hotNeutrons = SerializationHelper.tagToGrid(tag.getCompound(HOT_NEUTRONS_TAG));
        }
        if (tag.contains(COOL_NEUTRONS_TAG)) {
            coolNeutrons = SerializationHelper.tagToGrid(tag.getCompound(COOL_NEUTRONS_TAG));
        }
        if (tag.contains(HEATS_TAG)) {
            heats = SerializationHelper.tagToGrid(tag.getCompound(HEATS_TAG));
        }

        cacheInvalQueue = true;
    }



    /**
     * Attempt to setup block as input hatch
     *
     * @param pos The position to make an input hatch
     * @return True if the hatch has been successfully placed
     */
    @Override
    public boolean tryMakeBlockInputHatch(BlockPos pos) {
        if (pos.getY() >= maxExtents.getY()) {
            return false;
        }

        BlockState curState = getLevel().getBlockState(pos);
        if (!curState.is(ModBlocks.FISSION_REACTOR_CASING)) {return false;}

        getLevel().setBlock(pos, ModBlocks.FISSION_REACTOR_INPUT_HATCH.get().defaultBlockState(), Block.UPDATE_ALL);
        if (getLevel().getBlockEntity(pos) instanceof  SimpleFluidHatchBlockEntity fluidHatch) {
            fluidHatch.setController(getBlockPos());
            fluidHatch.setIO(true, false);
            fluidHatch.setTargetTank(0);
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
    public boolean tryMakeBlockOutputHatch(BlockPos pos) {
        if (pos.getY() >= maxExtents.getY()) {
            return false;
        }

        BlockState curState = getLevel().getBlockState(pos);
        if (!curState.is(ModBlocks.FISSION_REACTOR_CASING)) {return false;}

        getLevel().setBlock(pos, ModBlocks.FISSION_REACTOR_OUTPUT_HATCH.get().defaultBlockState(), Block.UPDATE_ALL);
        if (getLevel().getBlockEntity(pos) instanceof  SimpleFluidHatchBlockEntity fluidHatch) {
            fluidHatch.setController(getBlockPos());
            fluidHatch.setIO(false, true);
            fluidHatch.setTargetTank(1);
        }

        return true;
    }

    /**
     * Attempt to setup block as computer hatch
     *
     * @param pos The position to make an computer hatch
     * @return True if the hatch has been successfully placed
     */
    @Override
    public boolean tryMakeBlockComputerHatch(BlockPos pos) {
        if (pos.getY() >= maxExtents.getY()) {
            return false;
        }

        BlockState curState = getLevel().getBlockState(pos);
        if (!curState.is(ModBlocks.FISSION_REACTOR_CASING)) {return false;}

        getLevel().setBlock(pos, ModBlocks.FISSION_COMPUTER_HATCH.get().defaultBlockState(), Block.UPDATE_ALL);
        if (getLevel().getBlockEntity(pos) instanceof  FissionComputerHatchBlockEntity fluidHatch) {
            fluidHatch.setController(getBlockPos());
        }

        return true;
    }

    @Override
    public ISlotFluidHandler getFluidHandler() {
        return tank;
    }

    /**
     * Called when a component block is broken
     *
     * @param componentPos The position of the component block
     */
    @Override
    public void onComponentBreak(BlockPos componentPos) {
        if (level == null) {return;}
        ArrayList<ItemStack> itemsToDrop = new ArrayList<>();
        if (rods != null) {
            for (int i = 0; i < rods.getSlots(); i++) {
                if (!rods.getStackInSlot(i).isEmpty()) {
                    itemsToDrop.add(rods.getStackInSlot(i).copy());
                }
            }
        }
        for (BlockPos compPos: MultiblockHelper.getAreaIter(minExtents, maxExtents)) {
            BlockState breakState = level.getBlockState(compPos);
            if (breakState.getBlock() instanceof IMachineHatchBlock hatchBlock) {
                level.setBlock(compPos, hatchBlock.getDeconstructionBlockstate(), 3);
                itemsToDrop.add(hatchBlock.getDeconstructionItemStack());
                continue;
            }
            if (breakState.is(ModBlocks.FISSION_REACTOR_CASING)) {
                level.setBlock(compPos, ModBlocks.RADIATION_PROOF_CASING.get().defaultBlockState(), 3);
            }
            if (breakState.is(ModBlocks.FISSION_REACTOR_ROD)) {
                level.setBlock(compPos, ModBlocks.MECHANIZED_RADIATION_PROOF_CASING.get().defaultBlockState(), 3);
            }
        }

        // Handle drops
        Containers.dropContents(level, componentPos, NonNullList.copyOf(itemsToDrop));
    }

    /**
     * Attempts to form a fission reactor structure, with the specified position being the position of the desired controller
     * @param baseControllerPos The position of the ideal controller
     * @param level The current level
     * @param face The face clicked on to form the structure
     * @return True if the reactor has been successfully formed
     */
    public static boolean tryFormStructure(BlockPos baseControllerPos, Direction face, ServerLevel level) {
        // Get structure block area
        MultiblockHelper.WorldArea structureArea =
                MultiblockHelper.getEncompassingRect(baseControllerPos, ModTags.FISSION_REACTOR_BLOCKS, level)
                        .orElse(null);
        if (structureArea == null) {
            return false;
        }

        // Verify casings and toppers
        BlockPos casingSegmentMaxPos = new BlockPos(
                structureArea.maxPos().getX(),
                structureArea.maxPos().getY() - 1,
                structureArea.maxPos().getZ()
        );
        BlockPos rodSegmentMinPos = new BlockPos(
                structureArea.minPos().getX(),
                structureArea.maxPos().getY(),
                structureArea.minPos().getZ()
        );


        for (BlockPos tPos : MultiblockHelper.getAreaIter(structureArea.minPos(), casingSegmentMaxPos)) {
            if (!level.getBlockState(tPos).is(ModTags.FISSION_REACTOR_CASINGS)) {
                return false;
            }
        }

        for (BlockPos tPos : MultiblockHelper.getAreaIter(rodSegmentMinPos, structureArea.maxPos())) {
            if (!level.getBlockState(tPos).is(ModTags.FISSION_REACTOR_TOPPERS)) {
                return false;
            }
        }

        for (BlockPos tPos : MultiblockHelper.getAreaIter(structureArea.minPos(), casingSegmentMaxPos)) {
            level.setBlock(tPos, ModBlocks.FISSION_REACTOR_CASING.get().defaultBlockState(), 3);
            if (level.getBlockEntity(tPos) instanceof SimpleMachinePartBlockEntity machinePartBlockEntity) {
                machinePartBlockEntity.setController(baseControllerPos);
                machinePartBlockEntity.setChanged();
            }
        }

        for (BlockPos tPos : MultiblockHelper.getAreaIter(rodSegmentMinPos, structureArea.maxPos())) {
            level.setBlock(tPos, ModBlocks.FISSION_REACTOR_ROD.get().defaultBlockState(), 3);
            if (level.getBlockEntity(tPos) instanceof SimpleMachinePartBlockEntity machinePartBlockEntity) {
                machinePartBlockEntity.setController(baseControllerPos);
                machinePartBlockEntity.setChanged();
            }
        }

        level.setBlock(baseControllerPos, ModBlocks.FISSION_REACTOR_CONTROLLER.get().defaultBlockState(), 3);
        if (level.getBlockEntity(baseControllerPos) instanceof  FissionReactorControllerBlockEntity controllerBlockEntity) {
            int gridWidth = ((1 + structureArea.maxPos().getX() - structureArea.minPos().getX()));
            int gridHeight = ((1 + structureArea.maxPos().getZ() - structureArea.minPos().getZ()));
            int reactorHeight = ((1 + structureArea.maxPos().getY() - structureArea.minPos().getY()));
            controllerBlockEntity.rods = createItemHandler(gridWidth * gridHeight * 4);
            controllerBlockEntity.minExtents = structureArea.minPos();
            controllerBlockEntity.maxExtents = structureArea.maxPos();
            controllerBlockEntity.heats = new double[gridHeight * 2][gridWidth * 2];
            controllerBlockEntity.coolNeutrons = new double[gridHeight * 2][gridWidth * 2];
            controllerBlockEntity.hotNeutrons = new double[gridHeight * 2][gridWidth * 2];
            controllerBlockEntity.tank = createFluidTank(gridWidth * gridHeight * reactorHeight);
            controllerBlockEntity.setChanged();
        }
        return true;
    }


    public static ItemStackHandler createItemHandler(int size) {
        return new ItemStackHandler(size) {
            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return stack.is(ModTags.REACTOR_RODS);
            }

            @Override
            public @NotNull ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
                System.out.println("Inserting " + stack + " into slot " + slot);
                ItemStack hold = super.insertItem(slot,stack,simulate);
                System.out.println("Slots is now " + stacks);
                return hold;
            }
        };
    }

    public static ReactorTank createFluidTank(int size) {
        return new ReactorTank(size * 1000);
    }

    public static  void tick(Level level, BlockPos blockPos, BlockState blockState, BlockEntity blockEntity) {
        if (!(blockEntity instanceof FissionReactorControllerBlockEntity reactorBlockEntity)) return;


        if (reactorBlockEntity.minExtents == null) {return;}
        if (reactorBlockEntity.maxExtents == null) {return;}

        if (level != null && reactorBlockEntity.cacheInvalQueue) {
            reactorBlockEntity.cacheInvalQueue = false;
            for(BlockPos pos : MultiblockHelper.getAreaIter(reactorBlockEntity.minExtents, reactorBlockEntity.maxExtents)) {
                level.invalidateCapabilities(pos);
            }
        }
        // Make sure reactor is in all loaded chunks
        if (!level.isLoaded(reactorBlockEntity.minExtents)) {return;}
        if (!level.isLoaded(reactorBlockEntity.maxExtents)) {return;}
        if (!level.isLoaded(reactorBlockEntity.minExtents.mutable().setX(reactorBlockEntity.maxExtents.getX()))) {return;}
        if (!level.isLoaded(reactorBlockEntity.minExtents.mutable().setZ(reactorBlockEntity.maxExtents.getZ()))) {return;}


        // Check if fluid conversion needs to be updated
        if (reactorBlockEntity.workingRecipe == null ||
                !reactorBlockEntity.workingRecipe.matches(new FluidHeatingRecipeInput(reactorBlockEntity.getFluidHandler().getFluidInTank(0)), level)) {
            if (reactorBlockEntity.getFluidHandler() == null) {return;}
            RecipeHolder<FluidHeatingRecipe> possibleRecipe = level.getRecipeManager().getRecipeFor(
                        ModRecipeTypes.FLUID_HEATING.get(),
                        new FluidHeatingRecipeInput(reactorBlockEntity.getFluidHandler().getFluidInTank(0)),
                        level
                ).orElse(null);
            reactorBlockEntity.workingRecipe = possibleRecipe != null ? possibleRecipe.value() : null;
        }

        if (reactorBlockEntity.workingRecipe != null) {
            FluidHeatingRecipe workingRecipe = reactorBlockEntity.workingRecipe;
            IFluidHandler fluidHandler = reactorBlockEntity.getFluidHandler();

            int reactorIdealOpps = reactorBlockEntity.heatPerTick / workingRecipe.getHeatPerMillibucket();
            reactorIdealOpps *= reactorBlockEntity.getYSize();
            int maxInputOpps = fluidHandler.getFluidInTank(0).getAmount() / workingRecipe.getInputStack().getAmount();
            int outputCapacity = fluidHandler.getTankCapacity(1) - fluidHandler.getFluidInTank(1).getAmount();
            int maxOutputOpps = outputCapacity / workingRecipe.getOutput().getAmount();
            boolean outputIsValid = fluidHandler.isFluidValid(1, workingRecipe.getOutput());


            if (
                    !outputIsValid ||
                            maxOutputOpps < reactorIdealOpps ||
                            maxOutputOpps < reactorIdealOpps
            ) {
                reactorBlockEntity.isCoolingBlocked = true;
            }
            if (outputIsValid) {
                int opsToDo = Math.min(maxOutputOpps, Math.min(maxInputOpps, reactorIdealOpps));
                fluidHandler.fill(workingRecipe.getOutput().copyWithAmount(opsToDo * workingRecipe.getOutput().getAmount()), IFluidHandler.FluidAction.EXECUTE);
                fluidHandler.drain(workingRecipe.getInputStack().copyWithAmount(opsToDo * workingRecipe.getInputStack().getAmount()), IFluidHandler.FluidAction.EXECUTE);
            } else {
            }
        }

        // Increment reactor tick coutner and run reactor sim on 20'th tick
        if (++reactorBlockEntity.tickCounter > 20 && reactorBlockEntity.heats != null) {

            if (reactorBlockEntity.heats.length == 0) {return;}

            reactorBlockEntity.tickCounter = 0;
            double maxTemp = reactorBlockEntity.workingRecipe != null
                    ? reactorBlockEntity.workingRecipe.getTemperature()
                    : 9999999.0;

            int reactorWidth = reactorBlockEntity.getXSize();
            int reactorHeight = reactorBlockEntity.getZSize();

            ItemStack[][] runItems = new ItemStack[reactorHeight * 2][ reactorWidth * 2];

            for (int x = 0; x < reactorWidth; x++) {
                for (int y = 0; y < reactorHeight; y++) {
                    for (int s = 0; s < 4; s++) {
                        runItems[y * 2 + (s / 2)][x * 2 + (s % 2)] = reactorBlockEntity.rods.getStackInSlot(s + x + (y * reactorWidth));
                    }
                }
            }

            FissionCalculationHelper.SimulationResult simResult = FissionCalculationHelper.doSimStep(
                    runItems,
                    reactorBlockEntity.heats,
                    reactorBlockEntity.coolNeutrons,
                    reactorBlockEntity.hotNeutrons,
                    maxTemp,
                    1.0,
                    level.registryAccess()

            );

            reactorBlockEntity.averageTemperature = simResult.averageHeat();
            reactorBlockEntity.heatPerTick = (int)simResult.absorbedHeat();
            reactorBlockEntity.hottestCore = simResult.hottest();
        }
    }



    private static class ReactorTank implements ISlotFluidHandler {
        public static final String COOL_TAG = "CoolTank";
        public static final String HOT_TAG = "HotTank";

        private final FluidTank coolTank;
        private final FluidTank hotTank;

        private ReactorTank(int size) {
            this.coolTank = new FluidTank(size);
            this.hotTank = new FluidTank(size);
        }

        public CompoundTag toTag(CompoundTag tag ,HolderLookup.Provider registries) {
            tag.put(COOL_TAG, coolTank.writeToNBT(registries, new CompoundTag()));
            tag.put(HOT_TAG, hotTank.writeToNBT(registries, new CompoundTag()));
            return tag;
        }

        public void readTag(CompoundTag tag, HolderLookup.Provider registries) {
            if (tag.contains(COOL_TAG))
                coolTank.readFromNBT(registries, tag.getCompound(COOL_TAG));
            if (tag.contains(HOT_TAG))
                hotTank.readFromNBT(registries, tag.getCompound(HOT_TAG));
        }

        @Override
        public int getTanks() {
            return 2;
        }

        @Override
        public FluidStack getFluidInTank(int tank) {
            FluidTank toUse = tank == 0 ? coolTank : hotTank;
            return toUse.getFluidInTank(0);
        }

        @Override
        public int getTankCapacity(int tank) {
            FluidTank toUse = tank == 0 ? coolTank : hotTank;
            return toUse.getTankCapacity(0);
        }

        @Override
        public boolean isFluidValid(int tank, FluidStack stack) {
            FluidTank toUse = tank == 0 ? coolTank : hotTank;
            return toUse.isFluidValid(0, stack);
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            if (resource.is(ModTags.FISSION_REACTOR_COOLANTS)) {
                return coolTank.fill(resource, action);
            } else {
                return hotTank.fill(resource, action);
            }
        }

        @Override
        public FluidStack drain(FluidStack resource, FluidAction action) {
            return hotTank.drain(resource, action);
        }

        @Override
        public FluidStack drain(int maxDrain, FluidAction action) {
            return hotTank.drain(maxDrain, action);
        }
    }
}
