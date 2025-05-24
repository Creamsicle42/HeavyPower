package com.creamsicle42.heavypower.recipe.types;

import com.creamsicle42.heavypower.recipe.inputs.FluidHeatingRecipeInput;
import com.creamsicle42.heavypower.recipe.ModRecipeTypes;
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

public class FluidHeatingRecipe implements Recipe<FluidHeatingRecipeInput> {

    private final FluidStack inputType;
    private final FluidStack output;
    private final int heatPerMillibucket;
    private final double temperature;

    public FluidHeatingRecipe(FluidStack inputType, FluidStack output, int heatPerMillibucket, double temperature) {
        this.inputType = inputType;
        this.output = output;
        this.heatPerMillibucket = heatPerMillibucket;
        this.temperature = temperature;
    }

    public FluidStack getInputStack() {
        return inputType;
    }

    public FluidStack getOutput() {
        return output;
    }

    public int getHeatPerMillibucket() {
        return heatPerMillibucket;
    }

    public double getTemperature() {
        return temperature;
    }

    @Override
    public boolean matches(FluidHeatingRecipeInput input, @NotNull Level level) {
        return input.fluidStack().getFluidType() == inputType.getFluidType();
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull FluidHeatingRecipeInput input, HolderLookup.@NotNull Provider registries) {
        return ItemStack.EMPTY;
    }

    /// Custom assemble function that converts as much heat as possible
    public FluidStack assembleFluid(@NotNull FluidHeatingRecipeInput input, int heatUnits, HolderLookup.@NotNull Provider registries) {
        int mbToConvert = Math.min((int)(heatUnits / heatPerMillibucket), input.fluidStack().getAmount());
        return output.copyWithAmount(output.getAmount() * mbToConvert);
    }

    public FluidStack getRemainingInput(@NotNull FluidHeatingRecipeInput input, int heatUnits, HolderLookup.@NotNull Provider registries) {
        int mbToConvert = Math.min((int)(heatUnits / heatPerMillibucket), input.fluidStack().getAmount());
        return input.fluidStack().copyWithAmount(input.fluidStack().getAmount() - mbToConvert);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public @NotNull ItemStack getResultItem(HolderLookup.@NotNull Provider registries) {
        return new ItemStack(Items.BUCKET);
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.FLUID_HEATING_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return ModRecipeTypes.FLUID_HEATING.get();
    }

    public static class Serializer implements RecipeSerializer<FluidHeatingRecipe> {

        public static final MapCodec<FluidHeatingRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                FluidStack.CODEC.fieldOf("inputType").forGetter(FluidHeatingRecipe::getInputStack),
                FluidStack.CODEC.fieldOf("output").forGetter(FluidHeatingRecipe::getOutput),
                Codec.INT.fieldOf("heatPerMillibucket").forGetter(FluidHeatingRecipe::getHeatPerMillibucket),
                Codec.DOUBLE.fieldOf("temperature").forGetter(FluidHeatingRecipe::getTemperature)
        ).apply(inst, FluidHeatingRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, FluidHeatingRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        FluidStack.STREAM_CODEC, FluidHeatingRecipe::getInputStack,
                        FluidStack.STREAM_CODEC, FluidHeatingRecipe::getOutput,
                        ByteBufCodecs.INT, FluidHeatingRecipe::getHeatPerMillibucket,
                        ByteBufCodecs.DOUBLE, FluidHeatingRecipe::getTemperature,
                        FluidHeatingRecipe::new
                );

        @Override
        public @NotNull MapCodec<FluidHeatingRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, FluidHeatingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
