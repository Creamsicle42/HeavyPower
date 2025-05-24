package com.creamsicle42.heavypower;

import com.creamsicle42.heavypower.block.ModBlocks;
import com.creamsicle42.heavypower.blockentity.ModBlockEntities;
import com.creamsicle42.heavypower.blockentity.renderer.FissionReactorRodRenderer;
import com.creamsicle42.heavypower.computercraft.FissionReactorPeripheral;
import com.creamsicle42.heavypower.fluid.ModFluidTypes;
import com.creamsicle42.heavypower.fluid.ModFluids;
import com.creamsicle42.heavypower.item.ModCreativeModeTab;
import com.creamsicle42.heavypower.item.ModItems;
import com.creamsicle42.heavypower.menu.ModMenus;
import com.creamsicle42.heavypower.menu.custom.TurbineBladeHatchScreen;
import com.creamsicle42.heavypower.recipe.ModRecipeTypes;
import com.mojang.logging.LogUtils;
import dan200.computercraft.api.ComputerCraftAPI;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(HeavyPower.MODID)
public class HeavyPower
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "heavypower";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();




    public HeavyPower(IEventBus modEventBus, ModContainer modContainer)
    {

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(ModFluidTypes::clientExtensions);

        // Register ourselves for server and other game events we are interested in
        NeoForge.EVENT_BUS.register(this);

        // Register mod content
        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModCreativeModeTab.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModFluidTypes.register(modEventBus);
        ModFluids.register(modEventBus);
        ModRecipeTypes.register(modEventBus);
        ModMenus.register(modEventBus);

        // Data storage


        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        ComputerCraftAPI.registerGenericSource(new FissionReactorPeripheral());
    }

    public static ResourceLocation modResource(String id) {
        return ResourceLocation.fromNamespaceAndPath(MODID, id);
   }

    private void commonSetup(final FMLCommonSetupEvent event)
    {

    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void registerBER(EntityRenderersEvent.RegisterRenderers event) {
            event.registerBlockEntityRenderer(ModBlockEntities.FISSION_ROD_BE.get(), FissionReactorRodRenderer::new);
        }

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            ItemBlockRenderTypes.setRenderLayer(ModFluids.FLOWING_DENSE_STEAM.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModFluids.SOURCE_DENSE_STEAM.get(), RenderType.translucent());
        }

        @SubscribeEvent
        public static void registerScreens(RegisterMenuScreensEvent event) {
            event.register(ModMenus.TURBINE_BLADE_HATCH_MENU.get(), TurbineBladeHatchScreen::new);
        }
    }
}
