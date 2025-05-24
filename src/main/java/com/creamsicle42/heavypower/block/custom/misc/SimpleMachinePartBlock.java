package com.creamsicle42.heavypower.block.custom.misc;

import com.creamsicle42.heavypower.blockentity.misc.SimpleMachinePartBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SimpleMachinePartBlock extends BaseEntityBlock {

    public static final MapCodec<SimpleMachinePartBlock> CODEC = simpleCodec(SimpleMachinePartBlock::new);

    public SimpleMachinePartBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SimpleMachinePartBlockEntity(pos, state);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
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

}
