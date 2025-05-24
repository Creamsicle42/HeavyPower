package com.creamsicle42.heavypower.blockentity.evaporationtower;

import com.creamsicle42.heavypower.Config;
import com.creamsicle42.heavypower.ModTags;
import com.creamsicle42.heavypower.block.ModBlocks;
import com.creamsicle42.heavypower.block.custom.misc.IMachineHatchBlock;
import com.creamsicle42.heavypower.blockentity.ModBlockEntities;
import com.creamsicle42.heavypower.blockentity.fissionreactor.FissionReactorControllerBlockEntity;
import com.creamsicle42.heavypower.blockentity.misc.IFluidHatchManager;
import com.creamsicle42.heavypower.blockentity.misc.ISimpleMachineController;
import com.creamsicle42.heavypower.blockentity.misc.SimpleFluidHatchBlockEntity;
import com.creamsicle42.heavypower.blockentity.misc.SimpleMachinePartBlockEntity;
import com.creamsicle42.heavypower.misc.MultiblockHelper;
import com.creamsicle42.heavypower.recipe.ModRecipeTypes;
import com.creamsicle42.heavypower.recipe.inputs.EvaporationRecipeInput;
import com.creamsicle42.heavypower.recipe.types.EvaporationRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

import java.util.ArrayList;
import java.util.List;

public class EvaporationTowerControllerBlockEntity extends BlockEntity implements ISimpleMachineController, IFluidHatchManager {

    public static final String EVAP_MIN_TAG = "EvapMin";
    public static final String EVAP_MAX_TAG = "EvapMax";
    public static final String HEIGHT_TAG = "Height";
    public static final String FLUID_HANDLER_TAG = "FluidHandler";

    private BlockPos evapMinPos;
    private BlockPos evapMaxPos;
    private int height;
    private EvapTowerFluidHandler fluidHandler;
    private boolean cacheInvalQueue;
    private int skyCheckTimer;
    private boolean canSeeSky;

    public EvaporationTowerControllerBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.EVAPORATION_TOWER_CONTROLLER_BE.get(), pos, blockState);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        evapMaxPos = BlockPos.of(tag.getLong(EVAP_MAX_TAG));
        evapMinPos = BlockPos.of(tag.getLong(EVAP_MIN_TAG));
        height = tag.getInt(HEIGHT_TAG);
        int length = (evapMaxPos.getX() - evapMinPos.getX()) + 1;
        int width = (evapMaxPos.getZ() - evapMinPos.getZ()) + 1;
        fluidHandler = new EvapTowerFluidHandler(length * width * 1000);
        if (tag.contains(FLUID_HANDLER_TAG)) {
            fluidHandler.readTag(tag.getCompound(FLUID_HANDLER_TAG), registries);
        }
        cacheInvalQueue = true;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (evapMaxPos == null) {return;}
        tag.putLong(EVAP_MAX_TAG, evapMaxPos.asLong());
        tag.putLong(EVAP_MIN_TAG, evapMinPos.asLong());
        tag.putInt(HEIGHT_TAG, height);
        tag.put(FLUID_HANDLER_TAG, fluidHandler.toTag(new CompoundTag(), registries));
    }

    int getXWidth() {
        return (evapMaxPos.getX() - evapMinPos.getX()) + 1;
    }

    int getZWidth() {
        return (evapMaxPos.getZ() - evapMinPos.getZ()) + 1;
    }

    @Override
    public IFluidHandler getFluidHandler() {
        return fluidHandler;
    }

    /**
     * Attempt to setup block as input hatch
     *
     * @param pos The position to make an input hatch
     * @return True if the hatch has been successfully placed
     */
    @Override
    public boolean tryMakeBlockInputHatch(BlockPos pos) {
        BlockState curState = getLevel().getBlockState(pos);
        if (!curState.is(ModBlocks.EVAPORATION_TOWER_MESH_CASING)) {return false;}

        getLevel().setBlock(pos, ModBlocks.EVAPORATION_TOWER_INPUT_HATCH.get().defaultBlockState(), Block.UPDATE_ALL);
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
        BlockState curState = getLevel().getBlockState(pos);
        if (!curState.is(ModBlocks.EVAPORATION_TOWER_MESH_CASING)) {return false;}

        getLevel().setBlock(pos, ModBlocks.EVAPORATION_TOWER_OUTPUT_HATCH.get().defaultBlockState(), Block.UPDATE_ALL);
        if (getLevel().getBlockEntity(pos) instanceof  SimpleFluidHatchBlockEntity fluidHatch) {
            fluidHatch.setController(getBlockPos());
            fluidHatch.setIO(false, true);
            fluidHatch.setTargetTank(1);
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
        ArrayList<ItemStack> itemsToDrop = new ArrayList<>();

        for (BlockPos compPos: MultiblockHelper.getAreaIter(evapMinPos, evapMaxPos)) {
            BlockState breakState = level.getBlockState(compPos);
            if (breakState.getBlock() instanceof IMachineHatchBlock hatchBlock) {
                level.setBlock(compPos, hatchBlock.getDeconstructionBlockstate(), 3);
                itemsToDrop.add(hatchBlock.getDeconstructionItemStack());
                continue;
            }
            if (breakState.is(ModBlocks.EVAPORATION_TOWER_MESH_CASING)) {
                level.setBlock(compPos, ModBlocks.STEEL_MESH_CASING.get().defaultBlockState(), 3);
            }
        }

        MultiblockHelper.WorldArea evaporatorArea = new MultiblockHelper.WorldArea(evapMinPos, evapMaxPos);
        for (int i = 1; i <= height; i++) {
            List<BlockPos> ringPos = MultiblockHelper.getRingIter(
                    evaporatorArea.minPos().offset(0, i, 0),
                    evaporatorArea.getXSize(),
                    evaporatorArea.getZSize(),
                    Direction.EAST,
                    Direction.SOUTH);

            for (BlockPos pos : ringPos) {
                BlockState breakState = level.getBlockState(pos);
                if (breakState.is(ModBlocks.EVAPORATION_TOWER_CASING)) {
                    level.setBlock(pos, ModBlocks.REINFORCED_CONCRETE_CASING.get().defaultBlockState(), 3);
                }
            }
        }


        Containers.dropContents(level, componentPos, NonNullList.copyOf(itemsToDrop));
    }

    public static boolean tryFormStructure(BlockPos baseControllerPos, Direction face, ServerLevel level) {

        MultiblockHelper.WorldArea evaporatorArea =
                MultiblockHelper.getEncompassingRect(baseControllerPos, ModTags.EVAPORATION_TOWER_CONDENSERS, level)
                        .orElse(null);
        if (evaporatorArea == null) {
            return false;
        }

        if (evaporatorArea.getXSize() < 3 || evaporatorArea.getXSize() > Config.evaporationTowerMaxSize) return false;
        if (evaporatorArea.getZSize() < 3 || evaporatorArea.getZSize() > Config.evaporationTowerMaxSize) return false;
        if (evaporatorArea.getYSize() != 1) return false;

        int towerHeight = 0;
        for (int i = 1; i <= Config.evaporationTowerMaxHeight; i++) {
            List<BlockPos> ringPos = MultiblockHelper.getRingIter(
                    evaporatorArea.minPos().offset(0, i, 0),
                    evaporatorArea.getXSize(),
                    evaporatorArea.getZSize(),
                    Direction.EAST,
                    Direction.SOUTH);

            boolean valid = true;
            for (BlockPos pos : ringPos) {
                if (!level.getBlockState(pos).is(ModBlocks.REINFORCED_CONCRETE_CASING)) {
                    valid = false;
                    break;
                }
            }

            if (!valid) break;

            towerHeight++;
        }

        if (towerHeight < 1) {
            return false;
        }


        for (BlockPos tPos : evaporatorArea.getIterator()) {
            level.setBlock(tPos, ModBlocks.EVAPORATION_TOWER_MESH_CASING.get().defaultBlockState(), 3);
            if (level.getBlockEntity(tPos) instanceof SimpleMachinePartBlockEntity machinePartBlockEntity) {
                machinePartBlockEntity.setController(baseControllerPos);
                machinePartBlockEntity.setChanged();
            }
        }


        for (int i = 1; i <= towerHeight; i++) {
            List<BlockPos> ringPos = MultiblockHelper.getRingIter(
                    evaporatorArea.minPos().offset(0, i, 0),
                    evaporatorArea.getXSize(),
                    evaporatorArea.getZSize(),
                    Direction.EAST,
                    Direction.SOUTH);

            for (BlockPos pos : ringPos) {
                level.setBlock(pos, ModBlocks.EVAPORATION_TOWER_CASING.get().defaultBlockState(), 3);
                if (level.getBlockEntity(pos) instanceof SimpleMachinePartBlockEntity machinePartBlockEntity) {
                    machinePartBlockEntity.setController(baseControllerPos);
                    machinePartBlockEntity.setChanged();
                }
            }
        }


        level.setBlock(baseControllerPos, ModBlocks.EVAPORATION_TOWER_CONTROLLER.get().defaultBlockState(), 3);
        if (level.getBlockEntity(baseControllerPos) instanceof EvaporationTowerControllerBlockEntity controller) {
            controller.evapMinPos = evaporatorArea.minPos();
            controller.evapMaxPos = evaporatorArea.maxPos();
            controller.height = towerHeight;
            controller.fluidHandler = new EvapTowerFluidHandler(evaporatorArea.getZSize() * evaporatorArea.getXSize() * 1000);
            controller.setChanged();
        }

        return true;
    }

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, BlockEntity blockEntity) {
        if (!(blockEntity instanceof EvaporationTowerControllerBlockEntity controllerBlockEntity)) return;
        if (controllerBlockEntity.fluidHandler == null) return;

        if (level != null && controllerBlockEntity.cacheInvalQueue) {
            controllerBlockEntity.cacheInvalQueue = false;
            for(BlockPos pos : MultiblockHelper.getAreaIter(controllerBlockEntity.evapMinPos, controllerBlockEntity.evapMaxPos)) {
                level.invalidateCapabilities(pos);
            }
        }

        controllerBlockEntity.skyCheckTimer++;
        if (controllerBlockEntity.skyCheckTimer > 60) {
            controllerBlockEntity.skyCheckTimer = 0;
            controllerBlockEntity.canSeeSky = true;
            for (BlockPos pos : MultiblockHelper.getAreaIter(
                    controllerBlockEntity.evapMinPos.offset(1, 0, 1),
                    controllerBlockEntity.evapMaxPos.offset(-1, 0, -1))) {
                for (int i = 1; i <= controllerBlockEntity.height * 2; i++) {
                    if (!level.getBlockState(pos.offset(0, i, 0)).isEmpty()) {
                        controllerBlockEntity.canSeeSky = false;
                        break;
                    }
                }
                if (!controllerBlockEntity.canSeeSky) {
                    break;
                }
            }
        }

        if (!controllerBlockEntity.canSeeSky) {
            return;
        }

        RecipeHolder<EvaporationRecipe> recipeHolder = level.getRecipeManager().getRecipeFor(
                ModRecipeTypes.EVAPORATION.get(),
                new EvaporationRecipeInput(controllerBlockEntity.fluidHandler.getFluidInTank(0)),
                level
        ).orElse(null);

        if (recipeHolder == null) {return;}

        EvaporationRecipe recipe = recipeHolder.value();

        int maxParallels = (controllerBlockEntity.getXWidth() - 2) * (controllerBlockEntity.getZWidth() - 2);
        int inputParallels = (controllerBlockEntity.fluidHandler.inputTank.getFluidAmount() / recipe.getInputStack().getAmount());
        int outputParallels = (controllerBlockEntity.fluidHandler.outputTank.getCapacity() / recipe.getOutputStack().getAmount());

        int parallelsToRun = Math.min(maxParallels, Math.min(inputParallels, outputParallels));

        double radius = (((double)controllerBlockEntity.getXWidth() - 2.0) + ((double)controllerBlockEntity.getZWidth() - 2.0)) / 2.0;

        double outputEfficiency = 1.0 - Math.pow(2.0, -(double)controllerBlockEntity.height / radius);

        controllerBlockEntity.fluidHandler.inputTank.drain(
                recipe.getInputStack().getAmount() * parallelsToRun,
                IFluidHandler.FluidAction.EXECUTE
        );
        controllerBlockEntity.fluidHandler.outputTank.fill(
                recipe.getOutputStack().copyWithAmount(
                        (int)(recipe.getOutputStack().getAmount() * parallelsToRun * outputEfficiency)
                ),
                IFluidHandler.FluidAction.EXECUTE
        );
    }

    public void printDiagnostics() {
        if (fluidHandler == null) return;
        RecipeHolder<EvaporationRecipe> recipeHolder = level.getRecipeManager().getRecipeFor(
                ModRecipeTypes.EVAPORATION.get(),
                new EvaporationRecipeInput(fluidHandler.getFluidInTank(0)),
                level
        ).orElse(null);


        EvaporationRecipe recipe = recipeHolder != null ? recipeHolder.value() : null;
        Minecraft.getInstance().player.sendSystemMessage(Component.literal("Evap Tower Diagnostics"));
        double radius = (((double)getXWidth() - 2.0) + ((double)getZWidth() - 2.0)) / 2.0;
        double outputEfficiency = 1.0 - Math.pow(2.0, -(double)height / radius);

        Minecraft.getInstance().player.sendSystemMessage(Component.literal("- Dimensions: " + getXWidth() + "x" + getZWidth()));
        Minecraft.getInstance().player.sendSystemMessage(Component.literal("- Radius: " + radius));
        Minecraft.getInstance().player.sendSystemMessage(Component.literal("- Efficiency: " + outputEfficiency));
        Minecraft.getInstance().player.sendSystemMessage(Component.literal("- Recipe: " + recipe));
        Minecraft.getInstance().player.sendSystemMessage(Component.literal("- Can See Sky: " + canSeeSky));


    }

    private static class EvapTowerFluidHandler implements IFluidHandler {

        public static final String INPUT_TAG = "InputTank";
        public static final String OUTOUT_TAG = "OutputTank";

        protected final FluidTank inputTank;
        protected final FluidTank outputTank;

        public EvapTowerFluidHandler(int size) {
            inputTank = new FluidTank(size);
            outputTank = new FluidTank(size);
        }

        public CompoundTag toTag(CompoundTag tag , HolderLookup.Provider registries) {
            tag.put(INPUT_TAG, inputTank.writeToNBT(registries, new CompoundTag()));
            tag.put(OUTOUT_TAG, outputTank.writeToNBT(registries, new CompoundTag()));
            return tag;
        }

        public void readTag(CompoundTag tag, HolderLookup.Provider registries) {
            if (tag.contains(INPUT_TAG))
                inputTank.readFromNBT(registries, tag.getCompound(INPUT_TAG));
            if (tag.contains(OUTOUT_TAG))
                outputTank.readFromNBT(registries, tag.getCompound(OUTOUT_TAG));
        }

        /**
         * Returns the number of fluid storage units ("tanks") available
         *
         * @return The number of tanks available
         */
        @Override
        public int getTanks() {
            return 2;
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
            return (tank == 0 ? inputTank : outputTank).getFluidInTank(0);
        }

        /**
         * Retrieves the maximum fluid amount for a given tank.
         *
         * @param tank Tank to query.
         * @return The maximum fluid amount held by the tank.
         */
        @Override
        public int getTankCapacity(int tank) {
            return (tank == 0 ? inputTank : outputTank).getTankCapacity(0);
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
            return (tank == 0 ? inputTank : outputTank).isFluidValid(0, stack);
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
            return inputTank.fill(resource, action);
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
            return outputTank.drain(resource, action);
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
            return outputTank.drain(maxDrain, action);
        }
    }
}
