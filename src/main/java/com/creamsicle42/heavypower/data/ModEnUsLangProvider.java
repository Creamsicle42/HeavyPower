package com.creamsicle42.heavypower.data;

import com.creamsicle42.heavypower.block.ModBlocks;
import com.creamsicle42.heavypower.fluid.ModFluidTypes;
import com.creamsicle42.heavypower.HeavyPower;
import com.creamsicle42.heavypower.item.ModItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModEnUsLangProvider extends LanguageProvider {
    public ModEnUsLangProvider(PackOutput output) {
        super(output, HeavyPower.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        add("creativetab.heavypower.heavy_power_tab", "Misc Heavy Power");
        add("creativetab.heavypower.casings_tab", "Heavy Power Casings");

        addItem(ModItems.URANIUM_ROD, "Uranium Fuel Rod");
        addItem(ModItems.GRAPHITE_ROD, "Graphite Moderator Rod");
        addItem(ModItems.DENSE_STEAM_BUCKET, "Bucket of Dense Steam");
        addBlock(ModBlocks.DENSE_STEAM_BLOCK, "Dense Steam");
        addItem(ModItems.HEAT_EXCHANGER_ROD, "Heat Exchanger Rod");
        addFluid(ModFluidTypes.DENSE_STEAM, "Dense Steam");
        addItem(ModItems.MODERATOR_ROD, "Moderator Rod");
        addItem(ModItems.BLADE_HATCH_COMPONENT, "Turbine Blade Hatch Component");

        addItem(ModItems.FLUID_INPUT_HATCH_COMPONENTS, "Fluid Input Hatch Components");
        addItem(ModItems.FLUID_OUTPUT_HATCH_COMPONENTS, "Fluid Output Hatch Components");
        addItem(ModItems.ITEM_INPUT_HATCH_COMPONENTS, "Item Input Hatch Components");
        addItem(ModItems.ITEM_OUTPUT_HATCH_COMPONENTS, "Item Output Hatch Components");
        addItem(ModItems.ENERGY_INPUT_HATCH_COMPONENTS, "Energy Input Hatch Components");

        addItem(ModItems.SMALL_IRON_TURBINE_BLADE, "Small Iron Turbine Blade");
        addItem(ModItems.BLANK_DYNAMO, "Blank Dynamo");
        addItem(ModItems.DYNAMO_OUTPUT_HATCH_COMPONENTS, "Dynamo Output Hatch Components");
        addItem(ModItems.COMPUTER_HATCH_COMPONENTS, "Computer Hatch Components");
        addItem(ModItems.CENTRIFUGE_CONTROL_COMPONENT, "Centrifuge Control Components");

        // Generic Casings
        addItem(ModItems.STEEL_MESH_CASING, "Steel Mesh Casing");
        addItem(ModItems.REINFORCED_CONCRETE_CASING, "Reinforced Concrete Casing");
        addItem(ModItems.RADIATION_PROOF_CASING, "Radiation Proof Casing");
        addItem(ModItems.MECHANIZED_RADIATION_PROOF_CASING, "Mechanized Radiation Proof Casing");
        addItem(ModItems.TIER_ONE_CASING, "Tier One Casing");
        addItem(ModItems.MECHANIZED_TIER_ONE_CASING, "Mechanized Tier One Casing");
        addItem(ModItems.AUX_TIER_ONE_CASING, "Tier One Auxiliary Motor");

        // Evap Tower
        addBlock(ModBlocks.EVAPORATION_TOWER_CONTROLLER, "Evaporation Tower Controller");
        addBlock(ModBlocks.EVAPORATION_TOWER_CASING, "Evaporation Tower Casing");
        addBlock(ModBlocks.EVAPORATION_TOWER_MESH_CASING, "Evaporation Tower Condenser");
        addBlock(ModBlocks.EVAPORATION_TOWER_INPUT_HATCH, "Evaporation Tower Fluid Input Hatch");
        addBlock(ModBlocks.EVAPORATION_TOWER_OUTPUT_HATCH, "Evaporation Tower Fluid Output Hatch");
        addItem(ModItems.EVAPORATION_TOWER_CONTROL_COMPONENT, "Evaporation Tower Components");

        // Turbine Blocks
        addBlock(ModBlocks.TURBINE_CORE, "Turbine Core");
        addBlock(ModBlocks.TURBINE_CASING, "Turbine Casing");
        addBlock(ModBlocks.COPPER_DYNAMO, "Copper Dynamo");
        addBlock(ModBlocks.TURBINE_FLUID_INPUT_HATCH, "Turbine Fluid Input Hatch");
        addBlock(ModBlocks.TURBINE_FLUID_OUTPUT_HATCH, "Turbine Fluid Output Hatch");
        addBlock(ModBlocks.TURBINE_BLADE_HATCH, "Turbine Blade Access Hatch");

        // Fission reactor blocks
        addBlock(ModBlocks.FISSION_REACTOR_CASING, "Fission Reactor Casing");
        addBlock(ModBlocks.FISSION_REACTOR_CONTROLLER, "Fission Reactor Controller");
        addBlock(ModBlocks.FISSION_REACTOR_INPUT_HATCH, "Fission Reactor Fluid Input Hatch");
        addBlock(ModBlocks.FISSION_REACTOR_OUTPUT_HATCH, "Fission Reactor Fluid Output Hatch");
        addBlock(ModBlocks.FISSION_REACTOR_ROD, "Fission Reactor Rod Assembly");
        addBlock(ModBlocks.FISSION_COMPUTER_HATCH, "Fission Reactor Computer Hatch");
        addItem(ModItems.FISSION_REACTOR_CONTROL_COMPONENT, "Fission Reactor Components");

        // Centrifuge blocks
        addBlock(ModBlocks.CENTRIFUGE_CASING, "Centrifuge Casing");
        addBlock(ModBlocks.CENTRIFUGE_INPUT_BUS, "Centrifuge Item Input Bus");
        addBlock(ModBlocks.CENTRIFUGE_OUTPUT_BUS, "Centrifuge Item Output Bus");
        addBlock(ModBlocks.CENTRIFUGE_CONTROLLER, "Centrifuge Controller");
        addBlock(ModBlocks.CENTRIFUGE_MOTOR, "Centrifuge Motor");
        addBlock(ModBlocks.CENTRIFUGE_AUX_MOTOR, "Centrifuge Auxiliary Motor");
        addBlock(ModBlocks.CENTRIFUGE_ENERGY_INPUT, "Centrifuge Energy Input");
        addBlock(ModBlocks.CENTRIFUGE_INPUT_HATCH, "Centrifuge Fluid Input Hatch");
        addBlock(ModBlocks.CENTRIFUGE_OUTPUT_HATCH, "Centrifuge Fluid Output Hatch");
    }

    private void addFluid(DeferredHolder<FluidType, FluidType> fluid, String translation) {
        add(fluid.get().getDescriptionId(), translation);
    }
}
