package com.creamsicle42.heavypower.data;

import com.creamsicle42.heavypower.block.ModBlocks;
import com.creamsicle42.heavypower.HeavyPower;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.data.models.ModelProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.generators.*;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, HeavyPower.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {

        simpleBlockWithItem(ModBlocks.TURBINE_CORE.get(), models().cubeAll("turbine_core", ResourceLocation.fromNamespaceAndPath(HeavyPower.MODID, "block/turbine_core")));
        simpleBlockWithItem(ModBlocks.TURBINE_CASING.get(), models().cubeAll("turbine_casing", ResourceLocation.fromNamespaceAndPath(HeavyPower.MODID, "block/turbine_casing")));
        simpleBlockWithItem(ModBlocks.COPPER_DYNAMO.get(), models().cubeAll("copper_dynamo", ResourceLocation.fromNamespaceAndPath(HeavyPower.MODID, "block/copper_dynamo")));
        simpleBlockWithItem(ModBlocks.BLANK_DYNAMO.get(), models().cubeAll("blank_dynamo", ResourceLocation.fromNamespaceAndPath(HeavyPower.MODID, "block/blank_dynamo")));
        simpleBlock(ModBlocks.TURBINE_BLADE_HATCH.get());
        simpleBlock(ModBlocks.TURBINE_CONTROLLER.get());
        simpleBlock(ModBlocks.TURBINE_FLUID_INPUT_HATCH.get());
        simpleBlock(ModBlocks.TURBINE_FLUID_OUTPUT_HATCH.get());
        simpleBlock(ModBlocks.DYNAMO_OUTPUT_HATCH.get());

        simpleBlock(ModBlocks.FISSION_REACTOR_CONTROLLER.get());
        simpleBlock(ModBlocks.FISSION_REACTOR_CASING.get());
        simpleBlock(ModBlocks.FISSION_REACTOR_ROD.get());
        simpleBlock(ModBlocks.FISSION_REACTOR_INPUT_HATCH.get());
        simpleBlock(ModBlocks.FISSION_REACTOR_OUTPUT_HATCH.get());
        simpleBlock(ModBlocks.FISSION_COMPUTER_HATCH.get());

        simpleBlockWithItem(ModBlocks.RADIATION_PROOF_CASING.get(), models().cubeAll("radiation_proof_casing", HeavyPower.modResource("block/radiation_proof_casing")));
        simpleBlockWithItem(ModBlocks.MECHANIZED_RADIATION_PROOF_CASING.get(), models().cubeAll("mechanized_radiation_proof_casing", HeavyPower.modResource("block/mechanized_radiation_proof_casing")));
        simpleBlockWithItem(ModBlocks.REINFORCED_CONCRETE_CASING.get(), models().cubeAll("reinforced_concrete_casing", HeavyPower.modResource("block/reinforced_concrete_casing")));
        simpleBlockWithItem(ModBlocks.STEEL_MESH_CASING.get(), models().cubeAll("steel_mesh_casing", HeavyPower.modResource("block/steel_mesh_casing")));
        simpleBlockWithItem(ModBlocks.TIER_ONE_CASING.get(), models().cubeAll("tier_one_casing", HeavyPower.modResource("block/tier_one_casing")));
        simpleBlockWithItem(ModBlocks.MECHANIZED_TIER_ONE_CASING.get(), models().cubeAll("mechanized_tier_one_casing", HeavyPower.modResource("block/mechanized_tier_one_casing")));
        simpleBlockWithItem(ModBlocks.AUX_TIER_ONE_CASING.get(), models().cubeAll("aux_tier_one_casing", HeavyPower.modResource("block/aux_tier_one_casing")));

        simpleBlock(ModBlocks.EVAPORATION_TOWER_INPUT_HATCH.get());
        simpleBlock(ModBlocks.EVAPORATION_TOWER_OUTPUT_HATCH.get());
        simpleBlock(ModBlocks.EVAPORATION_TOWER_CASING.get());
        simpleBlock(ModBlocks.EVAPORATION_TOWER_MESH_CASING.get());
        simpleBlock(ModBlocks.EVAPORATION_TOWER_CONTROLLER.get());

        simpleBlock(ModBlocks.CENTRIFUGE_MOTOR.get());
        simpleBlock(ModBlocks.CENTRIFUGE_AUX_MOTOR.get());
        simpleBlock(ModBlocks.CENTRIFUGE_CASING.get());
        simpleBlock(ModBlocks.CENTRIFUGE_CONTROLLER.get());
        simpleBlock(ModBlocks.CENTRIFUGE_ENERGY_INPUT.get());

        ResourceLocation centrifugeBaseTexture = HeavyPower.modResource("block/centrifuge_casing");
        ResourceLocation centrifugeFluidInputTexture = HeavyPower.modResource("block/centrifuge_input_hatch");
        ResourceLocation centrifugeFluidOutputTexture = HeavyPower.modResource("block/centrifuge_output_hatch");
        ResourceLocation centrifugeItemInputTexture = HeavyPower.modResource("block/centrifuge_input_bus");
        ResourceLocation centrifugeItemOutputTexture = HeavyPower.modResource("block/centrifuge_output_bus");

        createDirectionalHatch("centrifuge_input_hatch", ModBlocks.CENTRIFUGE_INPUT_HATCH.get(), centrifugeFluidInputTexture, centrifugeBaseTexture);
        createDirectionalHatch("centrifuge_output_hatch", ModBlocks.CENTRIFUGE_OUTPUT_HATCH.get(), centrifugeFluidOutputTexture, centrifugeBaseTexture);
        createDirectionalHatch("centrifuge_input_bus", ModBlocks.CENTRIFUGE_INPUT_BUS.get(), centrifugeItemInputTexture, centrifugeBaseTexture);
        createDirectionalHatch("centrifuge_output_bus", ModBlocks.CENTRIFUGE_OUTPUT_BUS.get(), centrifugeItemOutputTexture, centrifugeBaseTexture);

    }

    void createDirectionalHatch(String name, Block block, ResourceLocation face, ResourceLocation side) {
        MultiPartBlockStateBuilder centBuilder = getMultipartBuilder(block);
        BlockModelBuilder northModel = models().cube("block/" + name + "/north", side, side, face, side, side, side);
        centBuilder.part().modelFile(
                models().cube("block/" + name + "/down", face, side, side, side, side, side)
        ).addModel().condition(BlockStateProperties.FACING, Direction.DOWN).end().part().modelFile(
                models().cube("block/" + name + "/up", side, face, side, side, side, side)
        ).addModel().condition(BlockStateProperties.FACING, Direction.UP).end().part().modelFile(
                northModel
        ).addModel().condition(BlockStateProperties.FACING, Direction.NORTH).end().part().modelFile(
                northModel
        ).rotationY(180).addModel().condition(BlockStateProperties.FACING, Direction.SOUTH).end().part().modelFile(
                northModel
        ).rotationY(90).addModel().condition(BlockStateProperties.FACING, Direction.EAST).end().part().modelFile(
                northModel
        ).rotationY(270).addModel().condition(BlockStateProperties.FACING, Direction.WEST).end();
    }
}
