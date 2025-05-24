package com.creamsicle42.heavypower.recipe.types;

import com.creamsicle42.heavypower.recipe.ModRecipeTypes;
import com.creamsicle42.heavypower.recipe.inputs.EvaporationRecipeInput;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public class EvaporationRecipe implements Recipe<EvaporationRecipeInput> {

    private final FluidStack inputStack;
    private final FluidStack outputStack;

    public EvaporationRecipe(FluidStack inputStack, FluidStack outputStack) {
        this.inputStack = inputStack;
        this.outputStack = outputStack;
    }

    @Override
    public boolean matches(EvaporationRecipeInput input, Level level) {
        return input.input().is(inputStack.getFluidType());
    }

    @Override
    public ItemStack assemble(EvaporationRecipeInput input, HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    /**
     * Used to determine if this recipe can fit in a grid of the given width/height
     *
     * @param width
     * @param height
     */
    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return new ItemStack(Items.BUCKET);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.EVAPORATION_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.EVAPORATION.get();
    }

    public FluidStack getInputStack() {
        return inputStack;
    }

    public FluidStack getOutputStack() {
        return outputStack;
    }

    @Override
    public String toString() {
        return "Evaporating(" + inputStack + " -> " + outputStack + ")";
    }

    public static class Serializer implements RecipeSerializer<EvaporationRecipe> {

        public static final MapCodec<EvaporationRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                FluidStack.CODEC.fieldOf("input").forGetter(EvaporationRecipe::getInputStack),
                FluidStack.CODEC.fieldOf("output").forGetter(EvaporationRecipe::getOutputStack)
        ).apply(inst, EvaporationRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, EvaporationRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        FluidStack.STREAM_CODEC, EvaporationRecipe::getInputStack,
                        FluidStack.STREAM_CODEC, EvaporationRecipe::getOutputStack,
                        EvaporationRecipe::new
                );

        @Override
        public @NotNull MapCodec<EvaporationRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, EvaporationRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
