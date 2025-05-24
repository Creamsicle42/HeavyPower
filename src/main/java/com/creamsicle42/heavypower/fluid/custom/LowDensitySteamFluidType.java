package com.creamsicle42.heavypower.fluid.custom;

import com.creamsicle42.heavypower.HeavyPower;
import com.creamsicle42.heavypower.fluid.ModFluidTypes;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;

public class LowDensitySteamFluidType extends FluidType {


    /**
     * Default constructor.
     *
     * @param properties the general properties of the fluid type
     */
    public LowDensitySteamFluidType(Properties properties) {
        super(properties);
    }

    public static DeferredHolder<FluidType, FluidType> getRegistry() {
        return ModFluidTypes.FLUID_TYPES.register("low_density_steam", () -> new LowDensitySteamFluidType(
                Properties.create().density(1).canSwim(false).viscosity(1).temperature(10)));
    }

    public static final IClientFluidTypeExtensions liquidExt = new IClientFluidTypeExtensions() {
        @Override
        public @NotNull ResourceLocation getStillTexture() {
            return ResourceLocation.fromNamespaceAndPath(HeavyPower.MODID, "block/dense_steam");
        }

        @Override
        public @NotNull ResourceLocation getFlowingTexture() {
            return ResourceLocation.fromNamespaceAndPath(HeavyPower.MODID, "block/dense_steam");
        }
    };

    @Override
    public int getDensity() {
        return -1;
    }
}
