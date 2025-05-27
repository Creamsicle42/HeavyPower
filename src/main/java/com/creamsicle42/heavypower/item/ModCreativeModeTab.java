package com.creamsicle42.heavypower.item;

import com.creamsicle42.heavypower.block.ModBlocks;
import com.creamsicle42.heavypower.HeavyPower;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCreativeModeTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, HeavyPower.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> HEAVY_POWER_TAB = CREATIVE_MODE_TABS.register("heavy_power_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.FISSION_REACTOR_CONTROL_COMPONENT.get()))
                    .title(Component.translatable("creativetab.heavypower.heavy_power_tab"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(ModItems.FLUID_INPUT_HATCH_COMPONENTS.get());
                        output.accept(ModItems.FLUID_OUTPUT_HATCH_COMPONENTS.get());
                        output.accept(ModItems.ITEM_INPUT_HATCH_COMPONENTS.get());
                        output.accept(ModItems.ITEM_OUTPUT_HATCH_COMPONENTS.get());
                        output.accept(ModItems.ENERGY_INPUT_HATCH_COMPONENTS.get());
                        output.accept(ModItems.COMPUTER_HATCH_COMPONENTS.get());
                        output.accept(ModItems.FISSION_REACTOR_CONTROL_COMPONENT.get());
                        output.accept(ModItems.URANIUM_ROD.get());
                        output.accept(ModItems.GRAPHITE_ROD.get());
                        output.accept(ModItems.HEAT_EXCHANGER_ROD.get());
                        output.accept(ModItems.MODERATOR_ROD.get());
                        output.accept(ModItems.DENSE_STEAM_BUCKET.get());
                        output.accept(ModBlocks.TURBINE_CASING.get());
                        output.accept(ModBlocks.TURBINE_CORE.get());
                        output.accept(ModBlocks.COPPER_DYNAMO.get());
                        output.accept(ModBlocks.BLANK_DYNAMO.get());
                        output.accept(ModItems.SMALL_IRON_TURBINE_BLADE.get());
                        output.accept(ModItems.BLADE_HATCH_COMPONENT.get());
                        output.accept(ModItems.DYNAMO_OUTPUT_HATCH_COMPONENTS.get());
                        output.accept(ModItems.EVAPORATION_TOWER_CONTROL_COMPONENT);
                        output.accept(ModItems.CENTRIFUGE_CONTROL_COMPONENT);
                    }).build());

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> HEAVY_POWER_CASINGS_TAB = CREATIVE_MODE_TABS.register("heavy_power_casings_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.TIER_ONE_CASING.get()))
                    .title(Component.translatable("creativetab.heavypower.casings_tab"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(ModItems.TIER_ONE_CASING);
                        output.accept(ModItems.MECHANIZED_TIER_ONE_CASING);
                        output.accept(ModItems.AUX_TIER_ONE_CASING);
                        output.accept(ModItems.REINFORCED_CONCRETE_CASING);
                        output.accept(ModItems.STEEL_MESH_CASING);
                        output.accept(ModItems.RADIATION_PROOF_CASING.get());
                        output.accept(ModItems.MECHANIZED_RADIATION_PROOF_CASING.get());
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
