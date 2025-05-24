package com.creamsicle42.heavypower.block.custom.reactor;

import com.creamsicle42.heavypower.block.custom.misc.SimpleMachinePartBlock;
import com.creamsicle42.heavypower.blockentity.ModBlockEntities;
import com.creamsicle42.heavypower.blockentity.fissionreactor.FissionReactorControllerBlockEntity;
import com.creamsicle42.heavypower.blockentity.fissionreactor.FissionRodBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.OptionalInt;

public class ReactorRodBlock extends SimpleMachinePartBlock {

    public static final MapCodec<ReactorRodBlock> CODEC = simpleCodec(ReactorRodBlock::new);

    public ReactorRodBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FissionRodBlockEntity(pos, state);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.isClientSide()) {
            return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
        }
        OptionalInt slot = getHitSlot(hitResult);
        if (slot.isEmpty()) {
            return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
        }
        IItemHandler itemHandler = level.getCapability(Capabilities.ItemHandler.BLOCK, pos, hitResult.getDirection());

        if (itemHandler.getStackInSlot(slot.getAsInt()).isEmpty() && !stack.isEmpty()) {
            itemHandler.insertItem(slot.getAsInt(), stack.copyWithCount(1), false);
        } else if (
                !itemHandler.getStackInSlot(slot.getAsInt()).isEmpty()
                && stack.isEmpty() && player.isCrouching()
                ) {
            itemHandler.extractItem(slot.getAsInt(), 1, false);
        }


        level.sendBlockUpdated(pos, state, state, Block.UPDATE_ALL);
        return ItemInteractionResult.sidedSuccess(level.isClientSide());
    }


private OptionalInt getHitSlot(BlockHitResult pHitResult) {
    return getRelativeHitCoordinatesForBlockFace(pHitResult).map(pos -> {
        int i = pos.y <= 0.5F ? 0 : 1;
        int j = pos.x <= 0.5F ? 0 : 1;
        return OptionalInt.of(j + i * 2);
    }).orElseGet(OptionalInt::empty);
}
private static Optional<Vec2> getRelativeHitCoordinatesForBlockFace(BlockHitResult pHitResult) {
    Direction direction = pHitResult.getDirection();
    if (Direction.UP != direction) {
        return Optional.empty();
    } else {
        BlockPos blockpos = pHitResult.getBlockPos().relative(direction);
        Vec3 vec3 = pHitResult.getLocation().subtract(blockpos.getX(), blockpos.getY(), blockpos.getZ());
        double d0 = vec3.x();
        double d2 = vec3.z();


        return Optional.of(new Vec2((float)d0, (float)d2));
    }
}
}
