package com.creamsicle42.heavypower.item;

import com.creamsicle42.heavypower.block.ModBlocks;
import com.creamsicle42.heavypower.blockentity.centrifuge.CentrifugeControllerBlockEntity;
import com.creamsicle42.heavypower.blockentity.evaporationtower.EvaporationTowerControllerBlockEntity;
import com.creamsicle42.heavypower.blockentity.fissionreactor.FissionReactorControllerBlockEntity;
import com.creamsicle42.heavypower.fluid.ModFluids;
import com.creamsicle42.heavypower.HeavyPower;
import com.creamsicle42.heavypower.item.custom.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(HeavyPower.MODID);

    public static final DeferredHolder<Item, ReactorRodItem> URANIUM_ROD = ITEMS.register("uranium_rod",
            () -> new ReactorRodItem(new Item.Properties(), ResourceLocation.fromNamespaceAndPath(HeavyPower.MODID, "uranium")));

    public static final DeferredHolder<Item, ReactorRodItem> GRAPHITE_ROD = ITEMS.register("graphite_rod",
            () -> new ReactorRodItem(new Item.Properties(), ResourceLocation.fromNamespaceAndPath(HeavyPower.MODID, "graphite")));

    public static final DeferredHolder<Item, ReactorRodItem> HEAT_EXCHANGER_ROD = ITEMS.register("heat_exchanger_rod",
            () -> new ReactorRodItem(new Item.Properties(), ResourceLocation.fromNamespaceAndPath(HeavyPower.MODID, "heat_exchanger")));

    public static final DeferredHolder<Item, ReactorRodItem> MODERATOR_ROD = ITEMS.register("moderator_rod",
            () -> new ReactorRodItem(new Item.Properties(), ResourceLocation.fromNamespaceAndPath(HeavyPower.MODID, "moderator")));

    public static final DeferredHolder<Item, TurbineBladeHatchItem> BLADE_HATCH_COMPONENT = ITEMS.register("turbine_blade_hatch_component",
            () -> new TurbineBladeHatchItem(new Item.Properties()));

    public static final DeferredHolder<Item, BucketItem> DENSE_STEAM_BUCKET = ITEMS.register("dense_steam_bucket",
            () -> new BucketItem(ModFluids.SOURCE_DENSE_STEAM.get(), new Item.Properties().stacksTo(1)));

    public static final DeferredItem<FluidInputHatchItem> FLUID_INPUT_HATCH_COMPONENTS = ITEMS.registerItem(
            "fluid_input_hatch_components",
            FluidInputHatchItem::new
    );

    public static final DeferredItem<FluidOutputHatchItem> FLUID_OUTPUT_HATCH_COMPONENTS = ITEMS.registerItem(
            "fluid_output_hatch_components",
            FluidOutputHatchItem::new
    );

    public static final DeferredItem<Item> SMALL_IRON_TURBINE_BLADE = ITEMS.registerItem(
            "small_iron_turbine_blade",
            Item::new,
            new Item.Properties().durability(15000)
    );

    public static final DeferredItem<DynamoOutputHatchItem> DYNAMO_OUTPUT_HATCH_COMPONENTS = ITEMS.registerItem(
            "dynamo_output_hatch_component",
            DynamoOutputHatchItem::new
    );

    public static final DeferredItem<MachineControllerHatchItem> FISSION_REACTOR_CONTROL_COMPONENT = ITEMS.registerItem(
        "fission_reactor_control_component",
            properties ->
                    new MachineControllerHatchItem(properties, FissionReactorControllerBlockEntity::tryFormStructure)
    );

    public static final DeferredItem<ComputerHatchItem> COMPUTER_HATCH_COMPONENTS = ITEMS.registerItem(
            "computer_hatch_component",
            ComputerHatchItem::new
    );

    public static final DeferredItem<MachineControllerHatchItem> EVAPORATION_TOWER_CONTROL_COMPONENT = ITEMS.registerItem(
            "evaporation_tower_control_component",
            properties -> new MachineControllerHatchItem(properties, EvaporationTowerControllerBlockEntity::tryFormStructure)
    );

    public static final DeferredItem<MachineControllerHatchItem> CENTRIFUGE_CONTROL_COMPONENT = ITEMS.registerItem(
            "centrifuge_control_component",
            properties -> new MachineControllerHatchItem(properties, CentrifugeControllerBlockEntity::tryFormStructure)
    );

    public static final DeferredItem<BlockItem> BLANK_DYNAMO = ITEMS.registerSimpleBlockItem(ModBlocks.BLANK_DYNAMO.getDelegate());
    public static final DeferredItem<BlockItem> RADIATION_PROOF_CASING = ITEMS.registerSimpleBlockItem(ModBlocks.RADIATION_PROOF_CASING.getDelegate());
    public static final DeferredItem<BlockItem> MECHANIZED_RADIATION_PROOF_CASING = ITEMS.registerSimpleBlockItem(ModBlocks.MECHANIZED_RADIATION_PROOF_CASING.getDelegate());
    public static final DeferredItem<BlockItem> REINFORCED_CONCRETE_CASING = ITEMS.registerSimpleBlockItem(ModBlocks.REINFORCED_CONCRETE_CASING);
    public static final DeferredItem<BlockItem> STEEL_MESH_CASING = ITEMS.registerSimpleBlockItem(ModBlocks.STEEL_MESH_CASING);
    public static final DeferredItem<BlockItem> TIER_ONE_CASING = ITEMS.registerSimpleBlockItem(ModBlocks.TIER_ONE_CASING);
    public static final DeferredItem<BlockItem> MECHANIZED_TIER_ONE_CASING = ITEMS.registerSimpleBlockItem(ModBlocks.MECHANIZED_TIER_ONE_CASING);
    public static final DeferredItem<BlockItem> AUX_TIER_ONE_CASING = ITEMS.registerSimpleBlockItem(ModBlocks.AUX_TIER_ONE_CASING);


    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
