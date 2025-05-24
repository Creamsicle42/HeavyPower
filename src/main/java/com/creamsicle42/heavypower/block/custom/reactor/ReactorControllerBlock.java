package com.creamsicle42.heavypower.block.custom.reactor;

import com.creamsicle42.heavypower.block.ModBlocks;
import com.creamsicle42.heavypower.block.custom.misc.IMachineHatchBlock;
import com.creamsicle42.heavypower.blockentity.ModBlockEntities;
import com.creamsicle42.heavypower.blockentity.fissionreactor.FissionReactorControllerBlockEntity;
import com.creamsicle42.heavypower.blockentity.misc.ISimpleMachineController;
import com.creamsicle42.heavypower.blockentity.misc.SimpleMachinePartBlockEntity;
import com.creamsicle42.heavypower.item.ModItems;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.Nullable;

public class ReactorControllerBlock extends Block implements EntityBlock, IMachineHatchBlock {

    public static final MapCodec<ReactorControllerBlock> CODEC = simpleCodec(ReactorControllerBlock::new);

    public ReactorControllerBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FissionReactorControllerBlockEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return blockEntityType == ModBlockEntities.FISSION_CONTROLLER_BE.get() ? (BlockEntityTicker<T>)FissionReactorControllerBlockEntity::tick : null;
    }

    /**
     * Get the item stack to be returned when the machine associated with this block is deconstructed
     *
     * @return The Item Stack to drop
     */
    @Override
    public ItemStack getDeconstructionItemStack() {
        return new ItemStack(ModItems.FISSION_REACTOR_CONTROL_COMPONENT.get());
    }

    /**
     * Get the blockstate to replace this block with when machine broken
     *
     * @return The Blockstate
     */
    @Override
    public BlockState getDeconstructionBlockstate() {
        return ModBlocks.RADIATION_PROOF_CASING.get().defaultBlockState();
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {

        if (level.isClientSide()) {return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);}
        if (level.getBlockEntity(pos) instanceof ISimpleMachineController blockEntity) {
            blockEntity.onComponentBreak(pos);
        }

        return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    }
}
