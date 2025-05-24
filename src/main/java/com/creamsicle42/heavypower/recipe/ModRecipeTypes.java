package com.creamsicle42.heavypower.recipe;

import com.creamsicle42.heavypower.HeavyPower;
import com.creamsicle42.heavypower.recipe.types.EvaporationRecipe;
import com.creamsicle42.heavypower.recipe.types.FluidHeatingRecipe;
import com.creamsicle42.heavypower.recipe.types.TurbineRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModRecipeTypes {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, HeavyPower.MODID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, HeavyPower.MODID);

    public static final Supplier<RecipeType<FluidHeatingRecipe>> FLUID_HEATING = RECIPE_TYPES.register("fluid_heating",
            () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath(HeavyPower.MODID, "fluid_heating")));
    public static final Supplier<RecipeSerializer<FluidHeatingRecipe>> FLUID_HEATING_SERIALIZER = RECIPE_SERIALIZERS.register("fluid_heating",
            FluidHeatingRecipe.Serializer::new);

    public static final Supplier<RecipeType<TurbineRecipe>> TURBINE = RECIPE_TYPES.register("turbine",
            () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath(HeavyPower.MODID, "turbine")));
    public static final Supplier<RecipeSerializer<TurbineRecipe>> TURBINE_SERIALIZER = RECIPE_SERIALIZERS.register("turbine",
            TurbineRecipe.Serializer::new);

    public static final Supplier<RecipeType<EvaporationRecipe>> EVAPORATION = RECIPE_TYPES.register("evaporation",
            () -> RecipeType.simple(HeavyPower.modResource("evaporation")));
    public static final Supplier<RecipeSerializer<EvaporationRecipe>> EVAPORATION_SERIALIZER = RECIPE_SERIALIZERS.register("evaporation",
            EvaporationRecipe.Serializer::new);

    public static void register(IEventBus eventBus) {
        RECIPE_TYPES.register(eventBus);
        RECIPE_SERIALIZERS.register(eventBus);
    }
}
