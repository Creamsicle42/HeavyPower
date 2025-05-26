package com.creamsicle42.heavypower.blockentity.turbine;

import com.creamsicle42.heavypower.Config;
import com.creamsicle42.heavypower.HeavyPower;
import com.creamsicle42.heavypower.ModTags;
import com.creamsicle42.heavypower.block.ModBlocks;
import com.creamsicle42.heavypower.block.custom.misc.IMachineHatchBlock;
import com.creamsicle42.heavypower.blockentity.misc.*;
import com.creamsicle42.heavypower.blockentity.ModBlockEntities;
import com.creamsicle42.heavypower.misc.ISlotFluidHandler;
import com.creamsicle42.heavypower.recipe.ModRecipeTypes;
import com.creamsicle42.heavypower.recipe.inputs.TurbineRecipeInput;
import com.creamsicle42.heavypower.recipe.types.TurbineRecipe;
import com.creamsicle42.heavypower.registry.ModDataRegistries;
import com.creamsicle42.heavypower.registry.types.TurbineDynamo;
import com.creamsicle42.heavypower.registry.types.TurbineStage;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.joml.Vector2i;

import java.util.*;

public class TurbineControllerBlockEntity extends BlockEntity implements ISimpleMachineController, IFluidHatchManager, IEnergyHatchManager {

    public static final String BLADES_TAG = "Blades";
    public static final String BLADE_COUNT_TAG = "BladeCount";
    public static final String ENERGY_TAG = "Energy";
    public static final String CASING_RADIUS_TAG = "CasingRadius";
    public static final String DIRECTION_TAG = "Direction";
    public static final String INPUT_FLUID_TAG = "InputFluid";
    public static final String OUTPUT_FLUID_TAG = "OutputFluid";
    public static final String DYNAMO_CLAMP_TAG = "DynamoClampForce";
    public static final String DYNAMO_BONUS_TAG = "DynamoBonus";
    public static final String DYNAMO_FALLOFF_TAG = "DynamoFalloff";
    public static final String DYNAMO_IDEAL_SPEED_TAG = "DynamoIdealSpeed";
    public static final String DYNAMO_OUTPUT_TAG = "DynamoOutputs";
    public static final String DYNAMO_BASE_EFFICIENCY_TAG = "BaseEfficiency";

    public NonNullList<ItemStack> blades;
    private double energy;
    private int[] casingRadius;
    private Direction direction;
    private FluidTank inputFluidTank;
    private FluidTank outputFluidTank;
    private double dynamoClampForce;
    private double dynamoIdealSpeed;
    private double dynamoFalloff;
    private double dynamoBonus;
    private double baseEfficiency;
    public List<TurbineStage> cachedStageData;
    private ArrayList<BlockPos> energyOutputBlocks;
    private boolean cachedStageDataDirty;


    public TurbineControllerBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.TURBINE_CONTROLLER_BE.get(), pos, blockState);
        blades = NonNullList.withSize(1, ItemStack.EMPTY);
        cachedStageData = List.of();
        cachedStageDataDirty = false;
        energyOutputBlocks = new ArrayList<>();
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        int bladeCount = tag.contains(BLADE_COUNT_TAG) ? tag.getInt(BLADE_COUNT_TAG) : 1;
        blades = NonNullList.withSize(bladeCount, ItemStack.EMPTY);
        if (tag.contains(BLADES_TAG))
            ContainerHelper.loadAllItems(tag.getCompound(BLADES_TAG), blades, registries);
        if (tag.contains(CASING_RADIUS_TAG)) {
            casingRadius = tag.getIntArray(CASING_RADIUS_TAG);
        } else {
            casingRadius = new int[]{1};
        }
        direction = tag.contains(DIRECTION_TAG) ? Direction.byName(tag.getString(DIRECTION_TAG)) : Direction.EAST;
        energy = tag.contains(ENERGY_TAG) ? tag.getDouble(ENERGY_TAG) : 0.0;

        int faceBlocks = (casingRadius[0] * 2 + 1) * (casingRadius[0] * 2 + 1);
        inputFluidTank = new FluidTank(faceBlocks * 1000);
        outputFluidTank = new FluidTank(faceBlocks * 1000);
        if (tag.contains(INPUT_FLUID_TAG) && !tag.getCompound(INPUT_FLUID_TAG).isEmpty())
            inputFluidTank.readFromNBT(registries, tag.getCompound(INPUT_FLUID_TAG));
        if (tag.contains(OUTPUT_FLUID_TAG) && !tag.getCompound(OUTPUT_FLUID_TAG).isEmpty())
            outputFluidTank.readFromNBT(registries, tag.getCompound(OUTPUT_FLUID_TAG));

        dynamoBonus = tag.getDouble(DYNAMO_BONUS_TAG);
        dynamoFalloff = tag.getDouble(DYNAMO_FALLOFF_TAG);
        dynamoClampForce = tag.getDouble(DYNAMO_CLAMP_TAG);
        dynamoIdealSpeed = tag.getDouble(DYNAMO_IDEAL_SPEED_TAG);
        baseEfficiency = tag.getDouble(DYNAMO_BASE_EFFICIENCY_TAG);

        if (tag.contains(DYNAMO_OUTPUT_TAG)) {
            energyOutputBlocks = new ArrayList<>(
                    Arrays.stream(tag.getLongArray(DYNAMO_OUTPUT_TAG)).mapToObj(BlockPos::of).toList());
        }

        cachedStageDataDirty = true;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        CompoundTag bladesTag = new CompoundTag();
        ContainerHelper.saveAllItems(bladesTag, blades, registries);
        tag.put(BLADES_TAG, bladesTag);
        tag.putInt(BLADE_COUNT_TAG, blades.size());
        tag.putDouble(ENERGY_TAG, energy);
        tag.putIntArray(CASING_RADIUS_TAG, casingRadius);
        tag.putString(DIRECTION_TAG, direction != null ? direction.getName() : Direction.EAST.getName());
        tag.putDouble(DYNAMO_BONUS_TAG, dynamoBonus);
        tag.putDouble(DYNAMO_FALLOFF_TAG, dynamoFalloff);
        tag.putDouble(DYNAMO_CLAMP_TAG, dynamoClampForce);
        tag.putDouble(DYNAMO_IDEAL_SPEED_TAG, dynamoIdealSpeed);
        tag.putDouble(DYNAMO_BASE_EFFICIENCY_TAG, baseEfficiency);
        if (inputFluidTank != null && outputFluidTank != null) {
            tag.put(INPUT_FLUID_TAG, inputFluidTank.writeToNBT(registries, new CompoundTag()));
            tag.put(OUTPUT_FLUID_TAG, outputFluidTank.writeToNBT(registries, new CompoundTag()));
        } else {
            tag.put(INPUT_FLUID_TAG, new CompoundTag());
            tag.put(OUTPUT_FLUID_TAG, new CompoundTag());
        }
        tag.putLongArray(DYNAMO_OUTPUT_TAG, energyOutputBlocks.stream().map(BlockPos::asLong).toList());


    }

    public void updateStageDataCache() {
        if (getLevel() == null) {return;}
        HolderLookup.Provider lookupProvider = getLevel().registryAccess();
        ArrayList<TurbineStage> newList = new ArrayList<>();
        for (ItemStack item : blades) {
            lookupProvider.asGetterLookup().get(
                    ModDataRegistries.TURBINE_STAGE,
                    ResourceKey.create(ModDataRegistries.TURBINE_STAGE, BuiltInRegistries.ITEM.getKey(item.getItem()))
            ).map(Holder.Reference::value).ifPresent(newList::add);
        }
        cachedStageDataDirty = false;
        cachedStageData = List.copyOf(newList);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, TurbineControllerBlockEntity blockEntity) {
        RecipeManager recipes = level.getRecipeManager();
        if (blockEntity.inputFluidTank == null) {return;}
        RecipeHolder<TurbineRecipe> conversionRecipe = recipes.getRecipeFor(
                ModRecipeTypes.TURBINE.get(),
                new TurbineRecipeInput(blockEntity.inputFluidTank.getFluid()),
                level
        ).orElse(null);


        double totalMass = 0.01;
        double totalFriction = 0.0;

        if (blockEntity.cachedStageDataDirty) {

            blockEntity.updateStageDataCache();
        }


        // Calculate total friction and mass
        for (TurbineStage stage : blockEntity.cachedStageData) {
            totalMass += stage.getMass();
            totalFriction += stage.getFriction();
        }

        // Calc speed
        double speed = blockEntity.energy / totalMass;

        if (conversionRecipe != null) {
            TurbineRecipe recipe = conversionRecipe.value();
            int maxInConv = (blockEntity.inputFluidTank.getFluidAmount() / 2) / recipe.getInputStack().getAmount();
            int maxOutConv = blockEntity.inputFluidTank.getFluidAmount() / recipe.getInputStack().getAmount();
            int conversions = Math.min(maxOutConv, maxInConv);

            blockEntity.inputFluidTank.drain(
                    conversions * recipe.getInputStack().getAmount(),
                    IFluidHandler.FluidAction.EXECUTE);
            blockEntity.outputFluidTank.fill(
                    recipe.getOutputStack().copyWithAmount(recipe.getOutputStack().getAmount() * conversions),
                    IFluidHandler.FluidAction.EXECUTE);

            int inputMB = conversions * recipe.getInputStack().getAmount();
            double packetEnergy = recipe.getEnergyPerMB() * inputMB;
            double packetFlow = (recipe.getFlowSpeedPerMB() * inputMB) + 1;
            double expansion = 1.0;
            double bonusExpansion = recipe.getExpPerHundred() * ((double)inputMB / 100.0);

            // Constant speed factor
            double speedFactor = (packetFlow - speed) / packetFlow;
            speedFactor = Math.clamp(speedFactor, 0.0, 1.0);

            // Do stage simulation
            for (TurbineStage stage : blockEntity.cachedStageData) {
                double sizeFactor = Math.pow(Math.E, -0.5 * Math.abs(stage.getSize() - expansion));
                double totalAbs = sizeFactor * speedFactor * stage.getEfficiency();
                double energyAbsorbed = packetEnergy * totalAbs;
                packetEnergy -= energyAbsorbed;
                blockEntity.energy += energyAbsorbed;
                expansion *= stage.getExpansion() + bonusExpansion;
            }


        }

        // Friction loss
        blockEntity.energy -= totalFriction * speed;

        // Loss due to clamp power
        double absorbedClampEnergy = speed * blockEntity.dynamoClampForce;
        blockEntity.energy -= absorbedClampEnergy;

        double generatorEfficiency = blockEntity.dynamoBonus
                + blockEntity.baseEfficiency * Math.pow(Math.E,
                -(1.0/ blockEntity.dynamoFalloff) * Math.abs(speed - blockEntity.dynamoIdealSpeed));
        double outputEnergy = absorbedClampEnergy * generatorEfficiency;

        for (BlockPos outPos : blockEntity.energyOutputBlocks) {
            if (level.getBlockEntity(outPos) instanceof SimpleEnergyOutputBlockEntity outputHatchBlock) {
                outputHatchBlock.distributeEnergy((int)(outputEnergy / blockEntity.energyOutputBlocks.size()));
            }
        }

        blockEntity.setChanged();
    }

    @Override
    public boolean tryMakeBlockInputHatch(BlockPos pos) {
        if (getLevel() == null || getLevel().isClientSide()) {return false;}

        BlockPos selfPos = getBlockPos();
        BlockPos offsetPos = new BlockPos(pos.getX() - selfPos.getX(), pos.getY() - selfPos.getY(), pos.getZ() - selfPos.getZ());

        Direction turbineDirection = direction;

        int layerNumber = offsetPos.getX() * turbineDirection.getStepX()
                + offsetPos.getY() * turbineDirection.getStepY()
                + offsetPos.getZ() * turbineDirection.getStepZ();

        if (layerNumber != 0) {return false;}

        getLevel().setBlock(pos, ModBlocks.TURBINE_FLUID_INPUT_HATCH.get().defaultBlockState(), 3);
        SimpleFluidHatchBlockEntity hatchBlockEntity =
                (SimpleFluidHatchBlockEntity) getLevel().getBlockEntity(pos);

        if (hatchBlockEntity == null) {return false;}

        hatchBlockEntity.setController(getBlockPos());
        hatchBlockEntity.setTargetTank(0);
        hatchBlockEntity.setIO(true, false);
        hatchBlockEntity.setChanged();

        return true;
    }

    @Override
    public boolean tryMakeBlockOutputHatch(BlockPos pos) {

        if (getLevel() == null || getLevel().isClientSide()) {return false;}

        BlockPos selfPos = getBlockPos();
        BlockPos offsetPos = new BlockPos(pos.getX() - selfPos.getX(), pos.getY() - selfPos.getY(), pos.getZ() - selfPos.getZ());

        Direction turbineDirection = direction;

        int layerNumber = offsetPos.getX() * turbineDirection.getStepX()
                + offsetPos.getY() * turbineDirection.getStepY()
                + offsetPos.getZ() * turbineDirection.getStepZ();

        if (layerNumber != blades.size() + 1) {return false;}

        getLevel().setBlock(pos, ModBlocks.TURBINE_FLUID_OUTPUT_HATCH.get().defaultBlockState(), 3);
        SimpleFluidHatchBlockEntity hatchBlockEntity =
                (SimpleFluidHatchBlockEntity) getLevel().getBlockEntity(pos);

        if (hatchBlockEntity == null) {return false;}

        hatchBlockEntity.setController(getBlockPos());
        hatchBlockEntity.setTargetTank(1);
        hatchBlockEntity.setIO(false, true);
        hatchBlockEntity.setChanged();

        return true;
    }

    /**
     * Check if a hatch block position is valid
     * @param pPos The position to check
     * @return -1 if the position is invalid, otherwise the hatch layer
     */
    public int isValidHatchPos(BlockPos pPos) {
        if (getLevel() == null || getLevel().isClientSide()) {return -1;}

        BlockPos selfPos = getBlockPos();
        BlockPos offsetPos = new BlockPos(pPos.getX() - selfPos.getX(), pPos.getY() - selfPos.getY(), pPos.getZ() - selfPos.getZ());

        Direction turbineDirection = direction;

        int layerNumber = offsetPos.getX() * turbineDirection.getStepX()
                + offsetPos.getY() * turbineDirection.getStepY()
                + offsetPos.getZ() * turbineDirection.getStepZ();

        return layerNumber > 0 && layerNumber <= blades.size() ? layerNumber - 1 : -1;
    }

    /**
     * Handles unforming of the multiblock
     * Notifies all block components that they no longer have a controller
     */
    public void controllerBreakMultiblock(BlockPos brokenPos) {
        if (getLevel() == null) {
            return;
        }
        if (getLevel().isClientSide()) {
            return;
        }



        Direction iBasis;
        Direction jBasis;

        if (direction == null) {return;}

        if (direction.getAxis() == Direction.Axis.X) {
            iBasis = Direction.UP;
            jBasis = Direction.SOUTH;
        } else if (direction.getAxis() == Direction.Axis.Z) {
            iBasis = Direction.UP;
            jBasis = Direction.EAST;
        } else {
            iBasis = Direction.SOUTH;
            jBasis = Direction.EAST;
        }

        ArrayList<ItemStack> itemsToDrop = new ArrayList<>();
        if (blades != null) {
            itemsToDrop.addAll(blades);
        } else {
            System.out.println("Blades are null!!");
        }

        for (int i = 0; i < casingRadius.length; i++) {
            BlockPos centerPos = getBlockPos().relative(direction, i);
            for (int x = -casingRadius[i]; x <= casingRadius[i]; x++) {
                for (int y = -casingRadius[i]; y <= casingRadius[i]; y++) {
                    BlockPos componentPos = centerPos.relative(iBasis, x).relative(jBasis, y);
                    BlockEntity component = getLevel().getBlockEntity(componentPos);

                    if (component instanceof SimpleMachinePartBlockEntity machinePartBlockEntity) {
                        machinePartBlockEntity.setController(null);
                        machinePartBlockEntity.setChanged();
                    }
                    if (getLevel().getBlockState(componentPos).getBlock() instanceof IMachineHatchBlock hatch) {
                        itemsToDrop.add(hatch.getDeconstructionItemStack());
                        BlockState newState = hatch.getDeconstructionBlockstate();
                        getLevel().setBlock(componentPos, newState, 3);
                    }

                    //getLevel().setBlock(componentPos, Blocks.AIR.defaultBlockState(), 3);
                }
            }
        }
        Containers.dropContents(getLevel(), brokenPos, NonNullList.copyOf(itemsToDrop));
    }

    /**
     * Called when a component block is broken
     *
     * @param componentPos The position of the component block
     */
    @Override
    public void onComponentBreak(BlockPos componentPos) {
        controllerBreakMultiblock(componentPos);
    }


    // Static Functions

    /**
     * Attempts to form a turbine structure
     * If is able to form the structure, then updates all structural blocks
     *
     * @param pPos Block position of the turbine core that will from the face of the turbine
     * @param pCoreDir Direction of the turbine core, extending from the face core
     * @param pLevel Current level
     * @return True if the structure was successfully formed
     */
    public static boolean tryFormStructure(BlockPos pPos, Direction pCoreDir, Level pLevel) {
        // Step 1, determine the length of the core

        // 1.1 Ensure base block is a turbine core
        if (!pLevel.getBlockState(pPos).is(ModBlocks.TURBINE_CORE)) {
            HeavyPower.LOGGER.error("Attempted to form turbine with improper core block.");
            return false;
        }

        // 1.2 March in core direction until invalid block is found
        int coreLength = 1;
        while (pLevel.getBlockState(pPos.relative(pCoreDir, coreLength)).is(ModBlocks.TURBINE_CORE)) {
            coreLength++;
        }

        // Step 2, identify the width and nature of core layers
        // 2.1 Get Basis vector directions for the directions of the layer plane
        Direction iBasis;
        Direction jBasis;

        if (pCoreDir.getAxis() == Direction.Axis.X) {
            iBasis = Direction.UP;
            jBasis = Direction.SOUTH;
        } else if (pCoreDir.getAxis() == Direction.Axis.Z) {
            iBasis = Direction.UP;
            jBasis = Direction.EAST;
        } else {
            iBasis = Direction.SOUTH;
            jBasis = Direction.EAST;
        }

        // Step 3, verify that the layers are valid
        int[] layerRadius = new int[coreLength];
        assert coreLength >= 1;
        ArrayList<TagKey<Block>> layerBlocks = new ArrayList<>();

        for (int i = 0; i < coreLength; i++) {
            BlockPos layerCenter = pPos.relative(pCoreDir, i);
            if (!pLevel.getBlockState(layerCenter.relative(iBasis)).is(ModTags.TURBINE_BLOCKS)) {
                System.out.println("Block " + layerCenter.relative(iBasis) + " Is not a valid turbine block");
                return false;
            }
            layerBlocks.add(pLevel.getBlockState(layerCenter.relative(iBasis)).is(ModTags.TURBINE_CASINGS) ? ModTags.TURBINE_CASINGS : ModTags.TURBINE_COILS);
            System.out.println("Layer " + i + " Will be " + layerBlocks.get(i));
            layerRadius[i] = 0;
            for (int r = 1; r <= Config.maxTurbineRadius; r++) {
                boolean ringValid = true;
                for (Vector2i offset : getRingPositions(r)) {
                    if (!pLevel.getBlockState(
                            layerCenter.relative(iBasis, offset.x).relative(jBasis, offset.y)
                    ).is(layerBlocks.get(i))) {
                        System.out.println("Stopping layer " + i + " at pos " + layerCenter.relative(iBasis, offset.x).relative(jBasis, offset.y));
                        ringValid = false;
                        break;
                    }
                    if (pLevel.getBlockEntity(
                            layerCenter.relative(iBasis, offset.x).relative(jBasis, offset.y)
                    ) instanceof SimpleMachinePartBlockEntity casingBlock) {
                        if (casingBlock.getControllerPosition() != null) {
                            ringValid = false;
                            break;
                        }
                    }
                }
                if (ringValid) {
                    layerRadius[i] = r;
                } else {
                    break;
                }
            }
        }

        // 3.2 Ensure that...
        // There are no 0 radius layers
        // There are at least three casing layers
        // There is at least one dynamo layer
        // No casing layers come after the first dynamo layer
        boolean isCheckingCasings = true;
        int casingCount = 0;
        int dynamoCount = 0;
        for (int i = 0; i < coreLength; i++) {
            if (layerRadius[i] == 0) {
                System.out.println("Layer " + i + " is zero radius");
                return false;
            }
            if (layerBlocks.get(i) == ModTags.TURBINE_CASINGS) {
                if (!isCheckingCasings) {
                    System.out.println("Casing layer detected after beginning of dynamo");
                    return false;
                }
                casingCount++;
            } else {
                isCheckingCasings = false;
                if (casingCount < 3) {
                    System.out.println("Fewer than three casing layers");
                    return false;
                }
                dynamoCount++;
            }
        }
        if (dynamoCount < 1) {
            System.out.println("No dynamo layer");
            return false;
        }

        HolderLookup.Provider provider = pLevel.registryAccess();

        // Calculate dynamo stats
        int dynamoBlockCount = 0;
        double dynamoClamp = 0.0;
        double dynamoBonus = 0.0;
        double dynamoIdealSpeed = 0.0;
        double dynamoFalloff = 0.0;
        double dynamoBaseEfficiency = 0.0;

        for (int i = casingCount; i < coreLength; i++) {
            BlockPos layerCenter = pPos.relative(pCoreDir, i);
            for (int r = 1; r <= layerRadius[i]; r++) {
                for (Vector2i offset : getRingPositions(r)) {
                    BlockPos targetPos = layerCenter
                            .relative(iBasis, offset.x)
                            .relative(jBasis, offset.y);
                    Block dynamoBlock = pLevel.getBlockState(targetPos).getBlock();
                    ResourceLocation blockID = BuiltInRegistries.BLOCK.getKey(dynamoBlock);
                    Optional<Holder.Reference<TurbineDynamo>> dynamoRef = provider.asGetterLookup().get(
                            ModDataRegistries.TURBINE_DYNAMO,
                            ResourceKey.create(ModDataRegistries.TURBINE_DYNAMO, blockID)
                    );
                    if (dynamoRef.isEmpty()) continue;
                    TurbineDynamo dynamoData = dynamoRef.get().value();
                    dynamoBlockCount++;
                    dynamoClamp += dynamoData.getClampForce();
                    dynamoBonus += dynamoData.getEfficiencyBonus();
                    dynamoIdealSpeed += dynamoData.getIdealSpeed();
                    dynamoFalloff += dynamoData.getEfficiencyFalloff();
                    dynamoBaseEfficiency += dynamoData.getBaseEfficiency();
                }
            }
        }

        // Step 4, Update all machine blocks
        for (int i = 0; i < coreLength; i++) {
            BlockPos layerCenter = pPos.relative(pCoreDir, i);
            if (pLevel.getBlockEntity(layerCenter) instanceof SimpleMachinePartBlockEntity coreBlock) {
                coreBlock.setController(pPos);
                coreBlock.setChanged();
            }

            for (int r = 1; r <= layerRadius[i]; r++) {
                for (Vector2i offset : getRingPositions(r)) {
                    if (pLevel.getBlockEntity(
                            layerCenter.relative(iBasis, offset.x).relative(jBasis, offset.y)
                    ) instanceof SimpleMachinePartBlockEntity casingBlock) {
                        casingBlock.setController(pPos);
                        casingBlock.setChanged();
                    }
                }
            }
        }

        pLevel.setBlock(pPos, ModBlocks.TURBINE_CONTROLLER.get().defaultBlockState(), 3);
        if (pLevel.getBlockEntity(pPos) instanceof TurbineControllerBlockEntity controller) {
            controller.blades = NonNullList.withSize(casingCount - 2, ItemStack.EMPTY);
            controller.direction = pCoreDir;
            controller.casingRadius = layerRadius;
            controller.dynamoIdealSpeed = dynamoIdealSpeed / dynamoBlockCount;
            controller.dynamoBonus = dynamoBonus;
            controller.dynamoClampForce = dynamoClamp;
            controller.dynamoFalloff = dynamoFalloff / dynamoBlockCount;
            controller.baseEfficiency = dynamoBaseEfficiency / dynamoBlockCount;
            int faceBlocks = (layerRadius[0] * 2 + 1) * (layerRadius[0] * 2 + 1);
            controller.inputFluidTank = new FluidTank(faceBlocks * 1000);
            controller.outputFluidTank = new FluidTank(faceBlocks * 1000);
            controller.setChanged();
        }


        return true;
    }



    /**
     * Creates a list of positions in a square ring given a radius
     *
     * @param radius Radius of the ring
     * @return List of positions on the ring
     */
    private static List<Vector2i> getRingPositions(int radius) {
        Set<Vector2i> out = new HashSet<>();

        for (int i = -radius; i <= radius; i++) {
            out.add(new Vector2i(i, radius));
            out.add(new Vector2i(i, -radius));
            out.add(new Vector2i(radius, i));
            out.add(new Vector2i(-radius, i));
        }

        return out.stream().toList();
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
        if (getLevel() == null) {return false;}
        if (!getLevel().getBlockState(pos).is(ModBlocks.BLANK_DYNAMO)) {return false;}

        getLevel().setBlock(pos, ModBlocks.DYNAMO_OUTPUT_HATCH.get().defaultBlockState(), 3);
        energyOutputBlocks.add(pos);
        setChanged();

        return true;
    }

    @Override
    public IEnergyStorage getEnergyStorage() {
        return null;
    }

    @Override
    public ISlotFluidHandler getFluidHandler() {
        return new FluidHandler(this);
    }

    public static class ItemHandler implements IItemHandlerModifiable {
        private final TurbineControllerBlockEntity controller;

        public ItemHandler(TurbineControllerBlockEntity controller) {
            this.controller = controller;
        }

        /**
         * Returns the number of slots available
         *
         * @return The number of slots available
         **/
        @Override
        public int getSlots() {
            return controller.blades.size();
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
            if (slot < 0 || slot >= controller.blades.size()) {return new ItemStack(Items.COAL);}
            return controller.blades.get(slot);
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
            if (!stack.is(ModTags.TURBINE_STAGES)) {return stack.copy();}
            if (!controller.blades.get(slot).isEmpty()) {return stack.copy();}
            if (!simulate) {
                controller.blades.set(slot, stack.copyWithCount(1));
                controller.updateStageDataCache();
            }
            return stack.copyWithCount(stack.getCount() - 1);
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
            ItemStack temp = controller.blades.get(slot).copy();
            if (!simulate) {
                controller.blades.set(slot, ItemStack.EMPTY);
                controller.updateStageDataCache();
            }
            return temp;
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
            if (slot < 0 || slot > controller.blades.size()) {return;}
            controller.blades.set(slot, stack);
            controller.updateStageDataCache();
        }
    }

    public static class FluidHandler implements ISlotFluidHandler {

        private final TurbineControllerBlockEntity controller;

        public FluidHandler(TurbineControllerBlockEntity controller) {
            this.controller = controller;
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
            FluidTank tankToUse = tank == 0 ? controller.inputFluidTank : controller.outputFluidTank;
            return tankToUse.getFluidInTank(0);
        }

        /**
         * Retrieves the maximum fluid amount for a given tank.
         *
         * @param tank Tank to query.
         * @return The maximum fluid amount held by the tank.
         */
        @Override
        public int getTankCapacity(int tank) {
            FluidTank tankToUse = tank == 0 ? controller.inputFluidTank : controller.outputFluidTank;
            return tankToUse.getTankCapacity(0);
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
            FluidTank tankToUse = tank == 0 ? controller.inputFluidTank : controller.outputFluidTank;
            return tankToUse.isFluidValid(stack);
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
            if (this.controller.inputFluidTank == null) {return 0;}
            return controller.inputFluidTank.fill(resource, action);
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
           return controller.outputFluidTank.drain(resource, action);
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
            if (controller.outputFluidTank == null) {return FluidStack.EMPTY;}
            return controller.outputFluidTank.drain(maxDrain, action);
        }
    }
}
