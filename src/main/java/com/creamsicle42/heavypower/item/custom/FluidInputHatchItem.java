package com.creamsicle42.heavypower.item.custom;

import com.creamsicle42.heavypower.blockentity.misc.IFluidHatchManager;
import com.creamsicle42.heavypower.blockentity.misc.SimpleMachinePartBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;

public class FluidInputHatchItem extends Item {
    public FluidInputHatchItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getLevel().isClientSide() || !context.getPlayer().isCrouching()) {
            return super.useOn(context);
        }
        if (context.getLevel().getBlockEntity(context.getClickedPos()) instanceof SimpleMachinePartBlockEntity simpleMachinePartBlockEntity) {
            BlockPos controllerPos = simpleMachinePartBlockEntity.getControllerPosition();


            if (controllerPos == null) {
                return super.useOn(context);
            }
            IFluidHatchManager controller = (IFluidHatchManager)
                    simpleMachinePartBlockEntity.getController().orElse(null);
            if (controller == null) {
                return super.useOn(context);
            }
            if (controller.tryMakeBlockInputHatch(context.getClickedPos(), context.getClickedFace())) {
                return InteractionResult.CONSUME;
            }
            return InteractionResult.FAIL;
        }

        return super.useOn(context);
    }
}
