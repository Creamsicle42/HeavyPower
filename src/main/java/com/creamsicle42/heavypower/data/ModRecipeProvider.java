package com.creamsicle42.heavypower.data;

import com.creamsicle42.heavypower.data.custom.EvaporationRecipeBuilder;
import com.creamsicle42.heavypower.data.custom.FluidHeatingRecipeBuilder;
import com.creamsicle42.heavypower.data.custom.TurbineRecipeBuilder;
import com.creamsicle42.heavypower.fluid.ModFluids;
import com.creamsicle42.heavypower.HeavyPower;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(@NotNull RecipeOutput recipeOutput) {
        new FluidHeatingRecipeBuilder(
                new FluidStack(Fluids.WATER, 1),
                new FluidStack(ModFluids.SOURCE_DENSE_STEAM.get(), 2),
                100,
                100.0
        ).unlockedBy("always", has(Tags.Items.BUCKETS_WATER)).save(recipeOutput, ResourceLocation.fromNamespaceAndPath(HeavyPower.MODID, "water_boiling"));

        new TurbineRecipeBuilder(
                new FluidStack(ModFluids.SOURCE_DENSE_STEAM.get(), 1),
                new FluidStack(ModFluids.LOW_DENSITY_STEAM, 5),
                100.0,
                10.0,
                0.005
        ).unlockedBy("always", has(Tags.Items.BUCKETS_WATER)).save(recipeOutput, ResourceLocation.fromNamespaceAndPath(HeavyPower.MODID, "dense_steam_turbine"));

        new EvaporationRecipeBuilder(
                new FluidStack(ModFluids.LOW_DENSITY_STEAM.get(), 100),
                new FluidStack(Fluids.WATER, 10)
        ).unlockedBy("always", has(Tags.Items.BUCKETS_WATER)).save(recipeOutput, HeavyPower.modResource("low_density_steam_evaporation"));
    }
}
