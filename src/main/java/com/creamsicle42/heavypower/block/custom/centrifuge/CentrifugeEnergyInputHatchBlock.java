package com.creamsicle42.heavypower.block.custom.centrifuge;

import com.creamsicle42.heavypower.block.ModBlocks;
import com.creamsicle42.heavypower.block.custom.misc.IMachineHatchBlock;
import com.creamsicle42.heavypower.block.custom.misc.IWrenchableBlock;
import com.creamsicle42.heavypower.blockentity.misc.SimpleEnergyInputBlockEntity;
import com.creamsicle42.heavypower.blockentity.misc.SimpleItemBusBlockEntity;
import com.creamsicle42.heavypower.blockentity.misc.SimpleMachinePartBlockEntity;
import com.creamsicle42.heavypower.item.ModItems;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CentrifugeEnergyInputHatchBlock extends BaseEntityBlock implements IMachineHatchBlock, IWrenchableBlock {

    public static final MapCodec<CentrifugeEnergyInputHatchBlock> CODEC = simpleCodec(CentrifugeEnergyInputHatchBlock::new);
    public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;

    public CentrifugeEnergyInputHatchBlock(Properties properties) {
        super(properties);
        registerDefaultState(getStateDefinition().any()
                .setValue(FACING, Direction.NORTH)
        );
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    }

    @Override
    public void setFacing(Level level, BlockPos pos, Direction dir) {
        level.setBlockAndUpdate(pos, level.getBlockState(pos).setValue(FACING, dir));
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SimpleEnergyInputBlockEntity(pos, state);
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
        // TODO: Update Item Stack
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
