package com.creamsicle42.heavypower.block;

import com.creamsicle42.heavypower.block.codec.ModBlockCodecs;
import com.creamsicle42.heavypower.block.custom.centrifuge.*;
import com.creamsicle42.heavypower.block.custom.evaporationtower.EvaporationTowerControllerBlock;
import com.creamsicle42.heavypower.block.custom.evaporationtower.EvaporationTowerFluidInputHatchBlock;
import com.creamsicle42.heavypower.block.custom.evaporationtower.EvaporationTowerFluidOutputHatchBlock;
import com.creamsicle42.heavypower.block.custom.misc.SimpleMachinePartBlock;
import com.creamsicle42.heavypower.block.custom.reactor.*;
import com.creamsicle42.heavypower.block.custom.turbine.*;
import com.creamsicle42.heavypower.fluid.ModFluids;
import com.creamsicle42.heavypower.HeavyPower;
import com.creamsicle42.heavypower.item.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(HeavyPower.MODID);


    public static final DeferredHolder<Block, LiquidBlock> DENSE_STEAM_BLOCK = BLOCKS.register("dense_steam",
            () -> new LiquidBlock(ModFluids.SOURCE_DENSE_STEAM.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.WATER)));

    public static final DeferredHolder<Block, Block> TURBINE_CORE = registerBlock("turbine_core",
            () -> new TurbineCoreBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)));

    public static final DeferredHolder<Block, Block> TURBINE_CASING = registerBlock("turbine_casing",
            () -> new SimpleMachinePartBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)));

    public static final DeferredHolder<Block, Block> COPPER_DYNAMO = registerBlock("copper_dynamo",
            () -> new SimpleMachinePartBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)));

    public static final DeferredBlock<Block> BLANK_DYNAMO = BLOCKS.registerBlock(
            "blank_dynamo",
            SimpleMachinePartBlock::new,
                BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK)
    );

    public static final DeferredHolder<Block, Block> TURBINE_BLADE_HATCH = registerBlock("turbine_blade_hatch",
            () -> new TurbineBladeHatchBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)));

    public static final DeferredBlock<TurbineControllerBlock> TURBINE_CONTROLLER = BLOCKS.registerBlock(
            "turbine_controller",
                TurbineControllerBlock::new
    );

    public static final DeferredBlock<TurbineFluidInputHatchBlock> TURBINE_FLUID_INPUT_HATCH  = BLOCKS.registerBlock(
            "turbine_fluid_input_hatch",
            TurbineFluidInputHatchBlock::new
    );

    public static final DeferredBlock<TurbineFluidOutputHatchBlock> TURBINE_FLUID_OUTPUT_HATCH = BLOCKS.registerBlock(
            "turbine_fluid_output_hatch",
            TurbineFluidOutputHatchBlock::new
    );

    public static final DeferredBlock<DynamoOutputHatchBlock> DYNAMO_OUTPUT_HATCH = BLOCKS.registerBlock(
            "dynamo_output_hatch",
            DynamoOutputHatchBlock::new
    );

    public static final DeferredBlock<Block> RADIATION_PROOF_CASING = BLOCKS.registerBlock(
            "radiation_proof_casing",
            Block::new
    );

    public static final DeferredBlock<Block> MECHANIZED_RADIATION_PROOF_CASING = BLOCKS.registerBlock(
            "mechanized_radiation_proof_casing",
            Block::new
    );

    public static final DeferredBlock<ReactorRodBlock> FISSION_REACTOR_ROD = BLOCKS.registerBlock(
            "fission_reactor_rod",
            ReactorRodBlock::new
    );

    public static final DeferredBlock<ReactorControllerBlock> FISSION_REACTOR_CONTROLLER = BLOCKS.registerBlock(
            "fission_reactor_controller",
            ReactorControllerBlock::new
    );

    public static final DeferredBlock<SimpleMachinePartBlock> FISSION_REACTOR_CASING = BLOCKS.registerBlock(
            "fission_reactor_casing",
            SimpleMachinePartBlock::new
    );

    public static final DeferredBlock<FissionFluidInputHatchBlock> FISSION_REACTOR_INPUT_HATCH = BLOCKS.registerBlock(
            "fission_reactor_input_hatch",
            FissionFluidInputHatchBlock::new
    );

    public static final DeferredBlock<FissionFluidOutputHatchBlock> FISSION_REACTOR_OUTPUT_HATCH = BLOCKS.registerBlock(
            "fission_reactor_output_hatch",
            FissionFluidOutputHatchBlock::new
    );

    public static final DeferredBlock<FissionComputerHatchBlock> FISSION_COMPUTER_HATCH = BLOCKS.registerBlock(
            "fission_reactor_computer_hatch",
            FissionComputerHatchBlock::new
    );

    public static final DeferredBlock<Block> REINFORCED_CONCRETE_CASING = BLOCKS.registerBlock(
            "reinforced_concrete_casing",
            Block::new
    );

    public static final DeferredBlock<Block> STEEL_MESH_CASING = BLOCKS.registerBlock(
            "steel_mesh_casing",
            Block::new
    );

    public static final DeferredBlock<Block> TIER_ONE_CASING = BLOCKS.registerBlock(
            "tier_one_casing",
            Block::new
    );

    public static final DeferredBlock<Block> MECHANIZED_TIER_ONE_CASING = BLOCKS.registerBlock(
            "mechanized_tier_one_casing",
            Block::new
    );

    public static final DeferredBlock<Block> AUX_TIER_ONE_CASING = BLOCKS.registerBlock(
            "aux_tier_one_casing",
            Block::new
    );

    public static final DeferredBlock<EvaporationTowerControllerBlock> EVAPORATION_TOWER_CONTROLLER = BLOCKS.registerBlock(
            "evaporation_tower_controller",
            EvaporationTowerControllerBlock::new
    );

    public static final DeferredBlock<EvaporationTowerFluidInputHatchBlock> EVAPORATION_TOWER_INPUT_HATCH = BLOCKS.registerBlock(
            "evaporation_tower_input_hatch",
            EvaporationTowerFluidInputHatchBlock::new
    );

    public static final DeferredBlock<EvaporationTowerFluidOutputHatchBlock> EVAPORATION_TOWER_OUTPUT_HATCH = BLOCKS.registerBlock(
            "evaporation_tower_output_hatch",
            EvaporationTowerFluidOutputHatchBlock::new
    );

    public static final DeferredBlock<SimpleMachinePartBlock> EVAPORATION_TOWER_CASING = BLOCKS.registerBlock(
            "evaporation_tower_casing",
            SimpleMachinePartBlock::new
    );

    public static final DeferredBlock<SimpleMachinePartBlock> EVAPORATION_TOWER_MESH_CASING = BLOCKS.registerBlock(
            "evaporation_tower_mesh_casing",
            SimpleMachinePartBlock::new
    );

    public static final DeferredBlock<SimpleMachinePartBlock> CENTRIFUGE_CASING = BLOCKS.registerBlock(
            "centrifuge_casing",
            SimpleMachinePartBlock::new
    );

    public static final DeferredBlock<SimpleMachinePartBlock> CENTRIFUGE_MOTOR = BLOCKS.registerBlock(
            "centrifuge_motor",
            SimpleMachinePartBlock::new
    );

    public static final DeferredBlock<SimpleMachinePartBlock> CENTRIFUGE_AUX_MOTOR = BLOCKS.registerBlock(
            "centrifuge_aux_motor",
            SimpleMachinePartBlock::new
    );

    public static final DeferredBlock<CentrifugeItemInputHatchBlock> CENTRIFUGE_INPUT_BUS = BLOCKS.registerBlock(
            "centrifuge_input_bus",
            CentrifugeItemInputHatchBlock::new
    );

    public static final DeferredBlock<CentrifugeItemOutputHatchBlock> CENTRIFUGE_OUTPUT_BUS = BLOCKS.registerBlock(
            "centrifuge_output_bus",
            CentrifugeItemOutputHatchBlock::new
    );

    public static final DeferredBlock<CentrifugeEnergyInputHatchBlock> CENTRIFUGE_ENERGY_INPUT = BLOCKS.registerBlock(
            "centrifuge_energy_input",
            CentrifugeEnergyInputHatchBlock::new
    );

    public static final DeferredBlock<CentrifugeFluidInputHatchBlock> CENTRIFUGE_INPUT_HATCH = BLOCKS.registerBlock(
            "centrifuge_input_hatch",
            CentrifugeFluidInputHatchBlock::new
    );

    public static final DeferredBlock<CentrifugeFluidOutputHatchBlock> CENTRIFUGE_OUTPUT_HATCH = BLOCKS.registerBlock(
            "centrifuge_output_hatch",
            CentrifugeFluidOutputHatchBlock::new
    );

    public static final DeferredBlock<CentrifugeControllerBlock> CENTRIFUGE_CONTROLLER = BLOCKS.registerBlock(
            "centrifuge_controller",
            CentrifugeControllerBlock::new
    );

    private static <T extends Block> DeferredHolder<Block, T> registerBlock(String name, Supplier<T> block) {
        DeferredHolder<Block, T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredHolder<Block, T> block) {
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        ModBlockCodecs.REGISTRAR.register(eventBus);
    }
}
