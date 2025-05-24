package com.creamsicle42.heavypower.data.custom;

import com.creamsicle42.heavypower.recipe.types.FluidHeatingRecipe;
import com.creamsicle42.heavypower.recipe.types.TurbineRecipe;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

public class TurbineRecipeBuilder implements RecipeBuilder {

    private final FluidStack input;
    private final FluidStack output;
    private final double energyPerMB;
    private final double flowPerMB;
    private final double expPerHundred;

    protected final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
    @Nullable
    protected String group;

    public TurbineRecipeBuilder(FluidStack input, FluidStack output, double energyPerMB, double flowPerMB, double expPerHundred) {
        this.input = input;
        this.output = output;
        this.energyPerMB = energyPerMB;
        this.flowPerMB = flowPerMB;
        this.expPerHundred = expPerHundred;
    }

    @Override
    public @NotNull RecipeBuilder unlockedBy(@NotNull String name, @NotNull Criterion<?> criterion) {
        this.criteria.put(name, criterion);
        return this;
    }

    @Override
    public @NotNull RecipeBuilder group(@Nullable String groupName) {
        this.group = groupName;
        return this;
    }

    @Override
    public @NotNull Item getResult() {
        return Items.BUCKET;
    }

    @Override
    public void save(RecipeOutput recipeOutput, @NotNull ResourceLocation id) {
        // Build the advancement.
        Advancement.Builder advancement = recipeOutput.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                .rewards(AdvancementRewards.Builder.recipe(id))
                .requirements(AdvancementRequirements.Strategy.OR);
        this.criteria.forEach(advancement::addCriterion);
        // Our factory parameters are the result, the block state, and the ingredient.
        TurbineRecipe recipe = new TurbineRecipe(input, output, energyPerMB, flowPerMB, expPerHundred);
        // Pass the id, the recipe, and the recipe advancement into the RecipeOutput.
        recipeOutput.accept(id, recipe, advancement.build(id.withPrefix("recipes/")));
    }
}
