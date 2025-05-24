package com.creamsicle42.heavypower.recipe.inputs;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeInput;
import net.neoforged.neoforge.fluids.FluidStack;

public record EvaporationRecipeInput(FluidStack input) implements RecipeInput {
    @Override
    public ItemStack getItem(int index) {
        return new ItemStack(Items.BUCKET);
    }

    @Override
    public int size() {
        return 1;
    }
}
