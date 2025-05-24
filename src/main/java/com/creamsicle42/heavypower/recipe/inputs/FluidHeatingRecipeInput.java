package com.creamsicle42.heavypower.recipe.inputs;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeInput;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public record FluidHeatingRecipeInput(FluidStack fluidStack) implements RecipeInput {
    @Override
    public @NotNull ItemStack getItem(int index) {
        //throw new IllegalArgumentException("Fluid heating has no item inputs");
        return new ItemStack(Items.BUCKET);
    }

    @Override
    public int size() {
        return 1;
    }
}
