package com.creamsicle42.heavypower.recipe.types;

import com.creamsicle42.heavypower.recipe.ModRecipeTypes;
import com.creamsicle42.heavypower.recipe.inputs.TurbineRecipeInput;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public class TurbineRecipe implements Recipe<TurbineRecipeInput> {

    private final FluidStack inputStack;
    private final FluidStack outputStack;
    private final double energyPerMB;
    private final double flowSpeedPerMB;
    private final double expPerHundred;

    public TurbineRecipe(FluidStack inputStack, FluidStack outputStack, double energyPerMB, double flowSpeedPerMB, double expPerHundred) {
        this.inputStack = inputStack;
        this.outputStack = outputStack;
        this.energyPerMB = energyPerMB;
        this.flowSpeedPerMB = flowSpeedPerMB;
        this.expPerHundred = expPerHundred;
    }

    @Override
    public boolean matches(TurbineRecipeInput input, Level level) {
        return input.inputStack().is(inputStack.getFluidType());
    }

    @Override
    public ItemStack assemble(TurbineRecipeInput input, HolderLookup.Provider registries) {
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
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.TURBINE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.TURBINE.get();
    }

    public FluidStack getInputStack() {
        return inputStack;
    }

    public FluidStack getOutputStack() {
        return outputStack;
    }

    public double getEnergyPerMB() {
        return energyPerMB;
    }

    public double getFlowSpeedPerMB() {
        return flowSpeedPerMB;
    }

    public double getExpPerHundred() {
        return expPerHundred;
    }

    public static class Serializer implements RecipeSerializer<TurbineRecipe> {

        public static final MapCodec<TurbineRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                FluidStack.CODEC.fieldOf("inputStack").forGetter(TurbineRecipe::getInputStack),
                FluidStack.CODEC.fieldOf("outputStack").forGetter(TurbineRecipe::getOutputStack),
                Codec.DOUBLE.fieldOf("energyPerMB").forGetter(TurbineRecipe::getEnergyPerMB),
                Codec.DOUBLE.fieldOf("flowSpeedPerMB").forGetter(TurbineRecipe::getFlowSpeedPerMB),
                Codec.DOUBLE.fieldOf("expPerHundred").forGetter(TurbineRecipe::getExpPerHundred)
        ).apply(inst, TurbineRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, TurbineRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        FluidStack.STREAM_CODEC, TurbineRecipe::getInputStack,
                        FluidStack.STREAM_CODEC, TurbineRecipe::getOutputStack,
                        ByteBufCodecs.DOUBLE, TurbineRecipe::getEnergyPerMB,
                        ByteBufCodecs.DOUBLE, TurbineRecipe::getFlowSpeedPerMB,
                        ByteBufCodecs.DOUBLE, TurbineRecipe::getExpPerHundred,
                        TurbineRecipe::new
                );

        @Override
        public @NotNull MapCodec<TurbineRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, TurbineRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
