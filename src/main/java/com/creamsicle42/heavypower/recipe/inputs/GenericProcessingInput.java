package com.creamsicle42.heavypower.recipe.inputs;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.neoforged.neoforge.fluids.FluidStack;

public record GenericProcessingInput(NonNullList<ItemStack> inputItems, NonNullList<FluidStack> inputFluids) implements RecipeInput {
    @Override
    public ItemStack getItem(int index) {
        return inputItems.get(index);
    }

    @Override
    public int size() {
        return inputItems.size();
    }
}
