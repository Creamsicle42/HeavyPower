package com.creamsicle42.heavypower.block.custom.turbine;

import com.creamsicle42.heavypower.block.ModBlocks;
import com.creamsicle42.heavypower.block.custom.misc.IMachineHatchBlock;
import com.creamsicle42.heavypower.blockentity.turbine.TurbineBladeHatchBlockEntity;
import com.creamsicle42.heavypower.item.ModItems;
import com.creamsicle42.heavypower.menu.custom.TurbineBladeHatchMenu;
import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuConstructor;
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

public class TurbineBladeHatchBlock extends BaseEntityBlock implements IMachineHatchBlock {

    public static final MapCodec<TurbineBladeHatchBlock> CODEC = simpleCodec(TurbineBladeHatchBlock::new);

    public TurbineBladeHatchBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TurbineBladeHatchBlockEntity(pos, state);
    }

    @Override
    protected @NotNull RenderShape getRenderShape(@NotNull BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {

        if (level.isClientSide()) {return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);}
        if (level.getBlockEntity(pos) instanceof TurbineBladeHatchBlockEntity blockEntity) {
            blockEntity.breakBlock();
        }

        return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            if (level.getBlockEntity(pos) instanceof TurbineBladeHatchBlockEntity entity) {
                BlockPos controllerPos = entity.getControllerPosition();
                int hatchLayer = entity.getHatchLayer();
                serverPlayer.openMenu(state.getMenuProvider(level, pos), (registryFriendlyByteBuf ->
                    registryFriendlyByteBuf.writeBlockPos(pos).writeBlockPos(controllerPos).writeInt(hatchLayer)
                ));
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected @Nullable MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof TurbineBladeHatchBlockEntity entity) {
            return new SimpleMenuProvider(
                    entity,
                    Component.translatable("heavypower:turbine_blade_hatch")
            );
        }
        return null;
    }

    /**
     * Get the item stack to be returned when the machine associated with this block is deconstructed
     *
     * @return The Item Stack to drop
     */
    @Override
    public ItemStack getDeconstructionItemStack() {
        return new ItemStack(ModItems.BLADE_HATCH_COMPONENT.get());
    }

    /**
     * Get the blockstate to replace this block with when machine broken
     *
     * @return The Blockstate
     */
    @Override
    public BlockState getDeconstructionBlockstate() {
        return ModBlocks.TURBINE_CASING.get().defaultBlockState();
    }


}
