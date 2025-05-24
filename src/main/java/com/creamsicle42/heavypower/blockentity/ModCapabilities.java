package com.creamsicle42.heavypower.blockentity;

import com.creamsicle42.heavypower.block.ModBlocks;
import com.creamsicle42.heavypower.blockentity.fissionreactor.FissionRodBlockEntity;
import com.creamsicle42.heavypower.blockentity.misc.SimpleEnergyOutputBlockEntity;
import com.creamsicle42.heavypower.blockentity.misc.SimpleFluidHatchBlockEntity;
import com.creamsicle42.heavypower.HeavyPower;
import com.creamsicle42.heavypower.blockentity.misc.SimpleItemBusBlockEntity;
import com.creamsicle42.heavypower.blockentity.turbine.TurbineBladeHatchBlockEntity;
import com.creamsicle42.heavypower.blockentity.turbine.TurbineControllerBlockEntity;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@EventBusSubscriber(modid = HeavyPower.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModCapabilities {

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent pEvent) {
        pEvent.registerBlock(
                Capabilities.ItemHandler.BLOCK,
                (pLevel, pPos, pState, pBlockEntity, pSide) -> new TurbineBladeHatchBlockEntity.ItemHandler(pBlockEntity),
                ModBlocks.TURBINE_BLADE_HATCH.get()
        );
        pEvent.registerBlock(
                Capabilities.FluidHandler.BLOCK,
                (pLevel, pPos, pState, pBlockEntity, pSide) ->
                        new TurbineControllerBlockEntity.FluidHandler((TurbineControllerBlockEntity) pBlockEntity),
                ModBlocks.TURBINE_CONTROLLER.get()
        );
        pEvent.registerBlock(
                Capabilities.ItemHandler.BLOCK,
                (pLevel, pPos, pState, pBlockEntity, pSide) ->
                        new TurbineControllerBlockEntity.ItemHandler((TurbineControllerBlockEntity) pBlockEntity),
                ModBlocks.TURBINE_CONTROLLER.get()
        );
        pEvent.registerBlock(
                Capabilities.FluidHandler.BLOCK,
                (pLevel, pPos, pState, pBlockEntity, pSide) -> {
                    if(pState.hasProperty(BlockStateProperties.FACING) && pState.getValue(BlockStateProperties.FACING) != pSide) return null;
                    assert pBlockEntity != null;
                    return new SimpleFluidHatchBlockEntity.FluidHandler((SimpleFluidHatchBlockEntity) pBlockEntity);
                },
                ModBlocks.TURBINE_FLUID_INPUT_HATCH.get(),
                ModBlocks.TURBINE_FLUID_OUTPUT_HATCH.get(),
                ModBlocks.FISSION_REACTOR_OUTPUT_HATCH.get(),
                ModBlocks.FISSION_REACTOR_INPUT_HATCH.get(),
                ModBlocks.EVAPORATION_TOWER_OUTPUT_HATCH.get(),
                ModBlocks.EVAPORATION_TOWER_INPUT_HATCH.get(),
                ModBlocks.CENTRIFUGE_INPUT_HATCH.get(),
                ModBlocks.CENTRIFUGE_OUTPUT_HATCH.get()
        );

        pEvent.registerBlock(
                Capabilities.ItemHandler.BLOCK,
                ((level, pos, state, blockEntity, context) -> {
                    if(state.hasProperty(BlockStateProperties.FACING) && state.getValue(BlockStateProperties.FACING) != context) return null;
                    return new SimpleItemBusBlockEntity.ItemHandler((SimpleItemBusBlockEntity) blockEntity);
                }),
                ModBlocks.CENTRIFUGE_OUTPUT_BUS.get(),
                ModBlocks.CENTRIFUGE_INPUT_BUS.get()
        );

        pEvent.registerBlock(
                Capabilities.EnergyStorage.BLOCK,
                (pLevel, pPos, pState, pBlockEntity, pSide) -> {
                        if(pState.hasProperty(BlockStateProperties.FACING) && pState.getValue(BlockStateProperties.FACING) != pSide) return null;
                        return new SimpleEnergyOutputBlockEntity.EnergyHandler();
                    },
                ModBlocks.DYNAMO_OUTPUT_HATCH.get()
        );

        pEvent.registerBlock(
                Capabilities.ItemHandler.BLOCK,
                ((level, pos, state, blockEntity, context) ->
                        new FissionRodBlockEntity.ItemHandler((FissionRodBlockEntity) blockEntity)),
                ModBlocks.FISSION_REACTOR_ROD.get()
        );

    }


}
