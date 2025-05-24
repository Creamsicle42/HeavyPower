package com.creamsicle42.heavypower.blockentity;

import com.creamsicle42.heavypower.block.ModBlocks;
import com.creamsicle42.heavypower.block.custom.reactor.FissionComputerHatchBlock;
import com.creamsicle42.heavypower.blockentity.centrifuge.CentrifugeControllerBlockEntity;
import com.creamsicle42.heavypower.blockentity.evaporationtower.EvaporationTowerControllerBlockEntity;
import com.creamsicle42.heavypower.blockentity.fissionreactor.FissionComputerHatchBlockEntity;
import com.creamsicle42.heavypower.blockentity.fissionreactor.FissionReactorControllerBlockEntity;
import com.creamsicle42.heavypower.blockentity.fissionreactor.FissionRodBlockEntity;
import com.creamsicle42.heavypower.blockentity.misc.*;
import com.creamsicle42.heavypower.blockentity.turbine.TurbineBladeHatchBlockEntity;
import com.creamsicle42.heavypower.blockentity.turbine.TurbineControllerBlockEntity;
import com.creamsicle42.heavypower.HeavyPower;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, HeavyPower.MODID);




    public static final DeferredHolder<BlockEntityType<?> ,BlockEntityType<SimpleMachinePartBlockEntity>> SIMPLE_MACHINE_PART_BE =
            BLOCK_ENTITIES.register("simple_machine_part_be", () -> BlockEntityType.Builder.of(
                    SimpleMachinePartBlockEntity::new,
                    ModBlocks.TURBINE_CASING.get(),
                    ModBlocks.COPPER_DYNAMO.get(),
                    ModBlocks.BLANK_DYNAMO.get(),
                    ModBlocks.TURBINE_CORE.get(),
                    ModBlocks.FISSION_REACTOR_CASING.get(),
                    ModBlocks.EVAPORATION_TOWER_CASING.get(),
                    ModBlocks.EVAPORATION_TOWER_MESH_CASING.get(),
                    ModBlocks.CENTRIFUGE_CASING.get(),
                    ModBlocks.CENTRIFUGE_AUX_MOTOR.get(),
                    ModBlocks.CENTRIFUGE_MOTOR.get()
            ).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SimpleItemBusBlockEntity>> SIMPLE_ITEM_BUS_BE =
            BLOCK_ENTITIES.register("simple_item_bus", () -> BlockEntityType.Builder.of(
                    SimpleItemBusBlockEntity::new,
                    ModBlocks.CENTRIFUGE_OUTPUT_BUS.get(),
                    ModBlocks.CENTRIFUGE_INPUT_BUS.get()
            ).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TurbineBladeHatchBlockEntity>> TURBINE_BLADE_HATCH_BE =
            BLOCK_ENTITIES.register("turbine_blade_hatch_be", () -> BlockEntityType.Builder.of(
                    TurbineBladeHatchBlockEntity::new,
                    ModBlocks.TURBINE_BLADE_HATCH.get()
            ).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TurbineControllerBlockEntity>> TURBINE_CONTROLLER_BE =
            BLOCK_ENTITIES.register("turbine_controller_be", () -> BlockEntityType.Builder.of(
                    TurbineControllerBlockEntity::new,
                    ModBlocks.TURBINE_CONTROLLER.get()
            ).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SimpleFluidHatchBlockEntity>> FLUID_HATCH_BE =
            BLOCK_ENTITIES.register("fluid_input_hatch_be", () -> BlockEntityType.Builder.of(
                    SimpleFluidHatchBlockEntity::new,
                    ModBlocks.TURBINE_FLUID_INPUT_HATCH.get(),
                    ModBlocks.TURBINE_FLUID_OUTPUT_HATCH.get(),
                    ModBlocks.FISSION_REACTOR_INPUT_HATCH.get(),
                    ModBlocks.FISSION_REACTOR_OUTPUT_HATCH.get(),
                    ModBlocks.EVAPORATION_TOWER_INPUT_HATCH.get(),
                    ModBlocks.EVAPORATION_TOWER_OUTPUT_HATCH.get(),
                    ModBlocks.CENTRIFUGE_INPUT_HATCH.get(),
                    ModBlocks.CENTRIFUGE_OUTPUT_HATCH.get()
            ).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SimpleEnergyOutputBlockEntity>> ENERGY_OUTPUT_BE =
            BLOCK_ENTITIES.register("energy_output_hatch_be", () -> BlockEntityType.Builder.of(
                    SimpleEnergyOutputBlockEntity::new,
                    ModBlocks.DYNAMO_OUTPUT_HATCH.get()
            ).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SimpleEnergyInputBlockEntity>> ENERGY_INPUT_BE =
            BLOCK_ENTITIES.register("energy_input_hatch_be", () -> BlockEntityType.Builder.of(
                    SimpleEnergyInputBlockEntity::new,
                    ModBlocks.CENTRIFUGE_ENERGY_INPUT.get()
            ).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FissionRodBlockEntity>> FISSION_ROD_BE =
            BLOCK_ENTITIES.register(
                    "fission_rod_be",
                    () -> BlockEntityType.Builder.of(
                            FissionRodBlockEntity::new,
                            ModBlocks.FISSION_REACTOR_ROD.get()
                    ).build(null)
            );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FissionReactorControllerBlockEntity>> FISSION_CONTROLLER_BE =
            BLOCK_ENTITIES.register(
                    "fission_controller_be",
                    () -> BlockEntityType.Builder.of(
                            FissionReactorControllerBlockEntity::new,
                            ModBlocks.FISSION_REACTOR_CONTROLLER.get()
                    ).build(null)
            );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FissionComputerHatchBlockEntity>> FISSION_COMPUTER_HATCH_BE =
            BLOCK_ENTITIES.register(
                    "fission_reactor_computer_hatch",
                    () -> BlockEntityType.Builder.of(
                            FissionComputerHatchBlockEntity::new,
                            ModBlocks.FISSION_COMPUTER_HATCH.get()
                    ).build(null)
            );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EvaporationTowerControllerBlockEntity>> EVAPORATION_TOWER_CONTROLLER_BE =
            BLOCK_ENTITIES.register(
                    "evaporation_tower_controller",
                    () -> BlockEntityType.Builder.of(
                            EvaporationTowerControllerBlockEntity::new,
                            ModBlocks.EVAPORATION_TOWER_CONTROLLER.get()
                    ).build(null)
            );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CentrifugeControllerBlockEntity>> CENTRIFUGE_CONTROLLER_BE =
            BLOCK_ENTITIES.register(
                    "centrifuge_controller",
                    () -> BlockEntityType.Builder.of(
                            CentrifugeControllerBlockEntity::new,
                            ModBlocks.CENTRIFUGE_CONTROLLER.get()
                    ).build(null)
            );

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
