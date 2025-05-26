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

        pEvent.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                ModBlockEntities.FLUID_HATCH_BE.get(),
                SimpleFluidHatchBlockEntity::getFluidHandler
        );

        pEvent.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.SIMPLE_ITEM_BUS_BE.get(),
                SimpleItemBusBlockEntity::getCapability
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
