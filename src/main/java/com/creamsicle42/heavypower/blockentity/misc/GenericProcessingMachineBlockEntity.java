package com.creamsicle42.heavypower.blockentity.misc;

import com.creamsicle42.heavypower.block.ModBlocks;
import com.creamsicle42.heavypower.block.custom.misc.IMachineHatchBlock;
import com.creamsicle42.heavypower.misc.MultiblockHelper;
import com.creamsicle42.heavypower.recipe.inputs.GenericProcessingInput;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.Map;

public class GenericProcessingMachineBlockEntity extends BlockEntity implements IFluidHatchManager, IEnergyHatchManager, IItemBusManager, ISimpleMachineController{

    public static final String MIN_EXTENTS_TAG = "MinExtents";
    public static final String MAX_EXTENTS_TAG = "MaxExtents";

    private BlockPos minExtents;
    private BlockPos maxExtents;

    public GenericProcessingMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (minExtents != null) tag.putLong(MIN_EXTENTS_TAG, minExtents.asLong()) ;
        if (maxExtents != null) tag.putLong(MAX_EXTENTS_TAG, maxExtents.asLong()) ;
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        minExtents = BlockPos.of(tag.getLong(MIN_EXTENTS_TAG));
        maxExtents = BlockPos.of(tag.getLong(MAX_EXTENTS_TAG));
    }

    /**
     * Attempt to setup block as input hatch
     *
     * @param pos The position to make an input hatch
     * @return True if the hatch has been successfully placed
     */
    @Override
    public boolean tryMakeBlockEnergyInputHatch(BlockPos pos) {
        return false;
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
    public IFluidHandler getFluidHandler() {
        return null;
    }

    /**
     * Attempt to setup block as input hatch
     *
     * @param pos The position to make an input hatch
     * @return True if the hatch has been successfully placed
     */
    @Override
    public boolean tryMakeBlockInputHatch(BlockPos pos) {
        return false;
    }

    /**
     * Attempt to setup block as output hatch
     *
     * @param pos The position to make an output hatch
     * @return True if the hatch has ben successfully placed
     */
    @Override
    public boolean tryMakeBlockOutputHatch(BlockPos pos) {
        return false;
    }

    @Override
    public IItemHandler getItemHandler() {
        return null;
    }

    /**
     * Attempt to setup block as input hatch
     *
     * @param pos The position to make an input hatch
     * @return True if the hatch has been successfully placed
     */
    @Override
    public boolean tryMakeBlockInputBus(BlockPos pos) {
        return false;
    }

    /**
     * Attempt to setup block as output hatch
     *
     * @param pos The position to make an output hatch
     * @return True if the hatch has been successfully placed
     */
    @Override
    public boolean tryMakeBlockOutputBus(BlockPos pos) {
        return false;
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
            controllerBlockEntity.initializeMachineStats(formationArea);
            controllerBlockEntity.minExtents = formationArea.minPos();
            controllerBlockEntity.maxExtents = formationArea.maxPos();
            controllerBlockEntity.setChanged();
        }

        return true;
    }

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, BlockEntity blockEntity) {}

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

    public void initializeMachineStats(MultiblockHelper.WorldArea formationArea) {}


    public GenericProcessingInput getRecipeInput() {
        return null;
    }

}
