package com.creamsicle42.heavypower.registry;

import com.creamsicle42.heavypower.HeavyPower;
import com.creamsicle42.heavypower.registry.types.ReactorMaterial;
import com.creamsicle42.heavypower.registry.types.TurbineDynamo;
import com.creamsicle42.heavypower.registry.types.TurbineStage;
import com.mojang.logging.LogUtils;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import org.slf4j.Logger;

@EventBusSubscriber(modid = HeavyPower.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModDataRegistries {

    private static final Logger LOGGER = LogUtils.getLogger();

    public static ResourceLocation REACTOR_MATERIAL_LOCATION =
            ResourceLocation.fromNamespaceAndPath(HeavyPower.MODID, "reactor_materials");
    public static final ResourceKey<Registry<ReactorMaterial>> REACTOR_MATERIAL = ResourceKey.createRegistryKey(REACTOR_MATERIAL_LOCATION);

    public static ResourceLocation TURBINE_STAGE_LOCATION =
            ResourceLocation.fromNamespaceAndPath(HeavyPower.MODID, "turbine_stage");
    public static final ResourceKey<Registry<TurbineStage>> TURBINE_STAGE = ResourceKey.createRegistryKey(TURBINE_STAGE_LOCATION);

    public static ResourceLocation TURBINE_DYNAMO_LOCATION =
            ResourceLocation.fromNamespaceAndPath(HeavyPower.MODID, "turbine_dynamo");
    public static final ResourceKey<Registry<TurbineDynamo>> TURBINE_DYNAMO = ResourceKey.createRegistryKey(TURBINE_DYNAMO_LOCATION);



    @SubscribeEvent
    public static void setupModDataRegistries(DataPackRegistryEvent.NewRegistry packRegistryEvent) {
        LOGGER.info("HeavyPower: Creating data registries");
        packRegistryEvent.dataPackRegistry(REACTOR_MATERIAL, ReactorMaterial.CODEC);
        packRegistryEvent.dataPackRegistry(TURBINE_STAGE, TurbineStage.CODEC);
        packRegistryEvent.dataPackRegistry(TURBINE_DYNAMO, TurbineDynamo.CODEC);
    }
}
