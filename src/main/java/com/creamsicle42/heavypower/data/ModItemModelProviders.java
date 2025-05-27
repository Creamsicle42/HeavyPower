package com.creamsicle42.heavypower.data;

import com.creamsicle42.heavypower.HeavyPower;
import com.creamsicle42.heavypower.item.ModItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModItemModelProviders extends ItemModelProvider {
    public ModItemModelProviders(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, HeavyPower.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(ModItems.URANIUM_ROD.get());
        basicItem(ModItems.GRAPHITE_ROD.get());
        basicItem(ModItems.HEAT_EXCHANGER_ROD.get());
        basicItem(ModItems.DENSE_STEAM_BUCKET.get());
        basicItem(ModItems.MODERATOR_ROD.get());
        basicItem(ModItems.BLADE_HATCH_COMPONENT.get());
        basicItem(ModItems.FLUID_INPUT_HATCH_COMPONENTS.get());
        basicItem(ModItems.FLUID_OUTPUT_HATCH_COMPONENTS.get());
        basicItem(ModItems.SMALL_IRON_TURBINE_BLADE.get());
        basicItem(ModItems.DYNAMO_OUTPUT_HATCH_COMPONENTS.get());
        basicItem(ModItems.FISSION_REACTOR_CONTROL_COMPONENT.get());
        basicItem(ModItems.COMPUTER_HATCH_COMPONENTS.get());
        basicItem(ModItems.EVAPORATION_TOWER_CONTROL_COMPONENT.get());
        basicItem(ModItems.CENTRIFUGE_CONTROL_COMPONENT.get());
        basicItem(ModItems.ITEM_OUTPUT_HATCH_COMPONENTS.get());
        basicItem(ModItems.ITEM_INPUT_HATCH_COMPONENTS.get());
        basicItem(ModItems.ENERGY_INPUT_HATCH_COMPONENTS.get());
    }
}
