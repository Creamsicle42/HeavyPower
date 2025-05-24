package com.creamsicle42.heavypower.blockentity.misc;

import com.creamsicle42.heavypower.blockentity.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/// Block entity for simple machine component blocks
/// Only holds an optional reference to it's controller
public class SimpleMachinePartBlockEntity extends BlockEntity {

    public static final String HAS_CONTROLLER_TAG = "HasController";
    public static final String CONTROLLER_POS_TAG = "ControllerPosition";

    protected BlockPos controllerPosition;

    public SimpleMachinePartBlockEntity(BlockEntityType<?> pType, BlockPos pos, BlockState blockState) {
        super(pType, pos, blockState);
    }

    public SimpleMachinePartBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.SIMPLE_MACHINE_PART_BE.get(), pos, blockState);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putBoolean(HAS_CONTROLLER_TAG, controllerPosition != null);
        tag.putLong(CONTROLLER_POS_TAG, controllerPosition != null ? controllerPosition.asLong() : 0);
        //System.out.println("Saving simple machine data");
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains(HAS_CONTROLLER_TAG) && tag.getBoolean(HAS_CONTROLLER_TAG) && tag.contains(CONTROLLER_POS_TAG)) {
            controllerPosition = BlockPos.of(tag.getLong(CONTROLLER_POS_TAG));
        } else {
            controllerPosition = null;
        }
    }

    public Optional<ISimpleMachineController> getController() {
        if (controllerPosition == null) {return Optional.empty();}
        if (getLevel() == null) {return Optional.empty();}
        ISimpleMachineController entity = (ISimpleMachineController)getLevel().getBlockEntity(controllerPosition);
        return entity == null ? Optional.empty() : Optional.of(entity);
    }


    /**
     * Notifies the controller of breaking this block, if needed
     */
    public void breakBlock() {
        if (getLevel().isClientSide()) {
            return;}
        if (controllerPosition == null) {
            return;}
        if (getLevel().getBlockEntity(controllerPosition) instanceof ISimpleMachineController controller) {
            controller.onComponentBreak(getBlockPos());
        }
    }

    public void setController(BlockPos controllerPosition) {
        this.controllerPosition = controllerPosition;
    }

    public BlockPos getControllerPosition() {
        return controllerPosition;
    }

    public boolean isFormed() {return controllerPosition != null;}
}
