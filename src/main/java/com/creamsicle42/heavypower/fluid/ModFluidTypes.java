package com.creamsicle42.heavypower.fluid;

import com.creamsicle42.heavypower.fluid.custom.DenseSteamFluidType;
import com.creamsicle42.heavypower.HeavyPower;
import com.creamsicle42.heavypower.fluid.custom.LowDensitySteamFluidType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class ModFluidTypes {
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.FLUID_TYPES, HeavyPower.MODID);


    public static final DeferredHolder<FluidType, FluidType> DENSE_STEAM = DenseSteamFluidType.getRegistry();
    public static final DeferredHolder<FluidType, FluidType> LOW_DENSITY_STEAM = LowDensitySteamFluidType.getRegistry();


    public static void register(IEventBus eventBus) {
        FLUID_TYPES.register(eventBus);
    }

    public static void clientExtensions(RegisterClientExtensionsEvent clientExtensionsEvent) {
        clientExtensionsEvent.registerFluidType(DenseSteamFluidType.liquidExt, DENSE_STEAM);
        clientExtensionsEvent.registerFluidType(LowDensitySteamFluidType.liquidExt, LOW_DENSITY_STEAM);
    }
}
