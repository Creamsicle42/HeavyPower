package com.creamsicle42.heavypower.data;

import com.creamsicle42.heavypower.HeavyPower;
import com.creamsicle42.heavypower.block.ModBlocks;
import com.creamsicle42.heavypower.item.ModItems;
import com.creamsicle42.heavypower.registry.ModDataRegistries;
import com.creamsicle42.heavypower.registry.types.ReactorMaterial;
import com.creamsicle42.heavypower.registry.types.TurbineDynamo;
import com.creamsicle42.heavypower.registry.types.TurbineStage;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class ModDatapackProvider {
    static ResourceLocation modResource(String resource) {
        return ResourceLocation.fromNamespaceAndPath(HeavyPower.MODID, resource);
    }

    public static RegistrySetBuilder getBuilder() {
        return new RegistrySetBuilder()
            .add(ModDataRegistries.REACTOR_MATERIAL, bootstrap -> {
                bootstrap.register(
                    ResourceKey.create(ModDataRegistries.REACTOR_MATERIAL, modResource("uranium")),
                    new ReactorMaterial.Builder("uranium")
                        .withPassiveStats(0.5, 0.0)
                        .withAbsorptionStats(0.01, 0.8)
                        .conversionStats(0.0, 0.1)
                        .withProductionStats(2.0, 0.0, 800.5)
                        .withMeltdownStats(13000.0)
                        .build()
                );
                bootstrap.register(
                    ResourceKey.create(ModDataRegistries.REACTOR_MATERIAL, modResource("graphite")),
                    new ReactorMaterial.Builder("graphite")
                        .conversionStats(0.7, 0.2)
                        .build()
                );
                bootstrap.register(
                    ResourceKey.create(ModDataRegistries.REACTOR_MATERIAL, modResource("heat_exchanger")),
                    new ReactorMaterial.Builder("heat_exchanger")
                        .withCoolantStats(95.0)
                        .conversionStats(0.05, 0.1)
                        .build()
                );
                bootstrap.register(
                    ResourceKey.create(ModDataRegistries.REACTOR_MATERIAL, modResource("moderator")),
                    new ReactorMaterial.Builder("moderator")
                        .conversionStats(0.7, 0.9)
                        .withAbsorptionStats(0.1, 0.5)
                        .withProductionStats(0, 0, 50.0)
                        .conversionStats(0.05, 0.1)
                        .build()
                );
            }
            ).add(ModDataRegistries.TURBINE_STAGE, bootstrap -> {
                bootstrap.register(
                    ResourceKey.create(ModDataRegistries.TURBINE_STAGE, ModItems.SMALL_IRON_TURBINE_BLADE.getId()),
                        new TurbineStage(0.2, 1.0, 40.0, 0.1, 1.5)
                );
            }).add(ModDataRegistries.TURBINE_DYNAMO, bootstrap -> {
                bootstrap.register(
                    ResourceKey.create(ModDataRegistries.TURBINE_DYNAMO, ModBlocks.COPPER_DYNAMO.getId()),
                        new TurbineDynamo(0.5, 0.02, 3000.0, 6000.0, 0.8)
                );
            });
    }
}
