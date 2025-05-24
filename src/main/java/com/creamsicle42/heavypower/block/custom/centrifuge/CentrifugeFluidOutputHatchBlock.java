package com.creamsicle42.heavypower.block.custom.centrifuge;

import com.creamsicle42.heavypower.block.ModBlocks;
import com.creamsicle42.heavypower.block.custom.misc.IMachineHatchBlock;
import com.creamsicle42.heavypower.blockentity.misc.SimpleFluidHatchBlockEntity;
import com.creamsicle42.heavypower.blockentity.misc.SimpleMachinePartBlockEntity;
import com.creamsicle42.heavypower.item.ModItems;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CentrifugeFluidOutputHatchBlock extends BaseEntityBlock implements IMachineHatchBlock {

    public static final MapCodec<CentrifugeFluidOutputHatchBlock> CODEC = simpleCodec(CentrifugeFluidOutputHatchBlock::new);

    public CentrifugeFluidOutputHatchBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SimpleFluidHatchBlockEntity(pos, state);
    }

    @Override
    protected @NotNull RenderShape getRenderShape(@NotNull BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {

        if (level.isClientSide()) {return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);}
        if (level.getBlockEntity(pos) instanceof SimpleMachinePartBlockEntity blockEntity) {
            blockEntity.breakBlock();
        }

        return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    }




    /**
     * Get the item stack to be returned when the machine associated with this block is deconstructed
     *
     * @return The Item Stack to drop
     */
    @Override
    public ItemStack getDeconstructionItemStack() {
        return new ItemStack(ModItems.FLUID_OUTPUT_HATCH_COMPONENTS.get());
    }

    /**
     * Get the blockstate to replace this block with when machine broken
     *
     * @return The Blockstate
     */
    @Override
    public BlockState getDeconstructionBlockstate() {
        return ModBlocks.TIER_ONE_CASING.get().defaultBlockState();
    }


}
