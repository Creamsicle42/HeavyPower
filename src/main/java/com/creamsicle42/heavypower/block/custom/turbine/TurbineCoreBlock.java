package com.creamsicle42.heavypower.block.custom.turbine;

import com.creamsicle42.heavypower.blockentity.misc.SimpleMachinePartBlockEntity;
import com.creamsicle42.heavypower.blockentity.turbine.TurbineControllerBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TurbineCoreBlock extends BaseEntityBlock {

    public static final MapCodec<TurbineCoreBlock> CODEC = simpleCodec(TurbineCoreBlock::new);

    public TurbineCoreBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SimpleMachinePartBlockEntity(pos, state);
    }

    @Override
    protected @NotNull RenderShape getRenderShape(@NotNull BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(@NotNull ItemStack pStack, @NotNull BlockState pState, Level pLevel, @NotNull BlockPos pPos, @NotNull Player pPlayer, @NotNull InteractionHand pHand, @NotNull BlockHitResult pHitResult) {
        if (pLevel.getBlockEntity(pPos) instanceof SimpleMachinePartBlockEntity entity) {
            if (!entity.isFormed() && pStack.isEmpty()) {
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }

            return ItemInteractionResult.sidedSuccess(pLevel.isClientSide);
        } else {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide()) {
            return super.useWithoutItem(state, level, pos, player,  hitResult);
        }

        if (level.getBlockEntity(pos) instanceof SimpleMachinePartBlockEntity turbineCoreBlockEntity) {
            if (turbineCoreBlockEntity.isFormed()) {
                return InteractionResult.FAIL;
            }
            if (TurbineControllerBlockEntity.tryFormStructure(pos, hitResult.getDirection().getOpposite() , level)) {
                return InteractionResult.sidedSuccess(level.isClientSide());
            } else {
                return InteractionResult.FAIL;
            }
        }

        return super.useWithoutItem( state, level, pos, player,  hitResult);
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
