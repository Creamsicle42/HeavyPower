package com.creamsicle42.heavypower.fluid;

import com.creamsicle42.heavypower.block.ModBlocks;
import com.creamsicle42.heavypower.HeavyPower;
import com.creamsicle42.heavypower.item.ModItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModFluids {
    public static final DeferredRegister<Fluid> FLUIDS =DeferredRegister.create(BuiltInRegistries.FLUID, HeavyPower.MODID);

    public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> SOURCE_DENSE_STEAM = FLUIDS.register("dense_steam",
            () -> new BaseFlowingFluid.Source(ModFluids.DENSE_STEAM_PROPERTIES));

    public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> FLOWING_DENSE_STEAM = FLUIDS.register("dense_steam_flowing",
            () -> new BaseFlowingFluid.Flowing(ModFluids.DENSE_STEAM_PROPERTIES));

    public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> LOW_DENSITY_STEAM = FLUIDS.register("low_density_steam",
            () -> new BaseFlowingFluid.Source(ModFluids.LOW_DENSITY_STEAM_PROPERTIES));

    public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> FLOWING_LOW_DENSITY_STEAM = FLUIDS.register("low_density_steam_flowing",
            () -> new BaseFlowingFluid.Flowing(ModFluids.LOW_DENSITY_STEAM_PROPERTIES));

    public static final BaseFlowingFluid.Properties DENSE_STEAM_PROPERTIES = new BaseFlowingFluid.Properties(
            ModFluidTypes.DENSE_STEAM, SOURCE_DENSE_STEAM, FLOWING_DENSE_STEAM
    ).levelDecreasePerBlock(1).slopeFindDistance(4).block(ModBlocks.DENSE_STEAM_BLOCK).bucket(ModItems.DENSE_STEAM_BUCKET);

    public static final BaseFlowingFluid.Properties LOW_DENSITY_STEAM_PROPERTIES = new BaseFlowingFluid.Properties(
            ModFluidTypes.LOW_DENSITY_STEAM, LOW_DENSITY_STEAM, FLOWING_LOW_DENSITY_STEAM
    );

    public static void register(IEventBus eventBus) {
        FLUIDS.register(eventBus);
    }


}
