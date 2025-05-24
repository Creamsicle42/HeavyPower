package com.creamsicle42.heavypower.item.custom;

import com.creamsicle42.heavypower.block.ModBlocks;
import com.creamsicle42.heavypower.blockentity.misc.SimpleMachinePartBlockEntity;
import com.creamsicle42.heavypower.blockentity.turbine.TurbineBladeHatchBlockEntity;
import com.creamsicle42.heavypower.blockentity.turbine.TurbineControllerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;

public class TurbineBladeHatchItem extends Item {
    public TurbineBladeHatchItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getLevel().isClientSide()) {
            return super.useOn(context);
        }

        BlockState hitBlock = context.getLevel().getBlockState(context.getClickedPos());

        if (!hitBlock.is(ModBlocks.TURBINE_CASING)) {
            return super.useOn(context);
        }

        if (context.getLevel().getBlockEntity(context.getClickedPos()) instanceof SimpleMachinePartBlockEntity simpleMachinePartBlockEntity) {
            BlockPos controllerPos = simpleMachinePartBlockEntity.getControllerPosition();


            if (controllerPos == null) {
                return super.useOn(context);
            }
            TurbineControllerBlockEntity controller = (TurbineControllerBlockEntity)
                    simpleMachinePartBlockEntity.getController().orElse(null);
            if (controller == null) {
                return super.useOn(context);
            }
            int hatchPos = controller.isValidHatchPos(context.getClickedPos());
            if (hatchPos == -1) {
                return InteractionResult.FAIL;
            }
            context.getLevel().setBlock(context.getClickedPos(), ModBlocks.TURBINE_BLADE_HATCH.get().defaultBlockState(), 3);
            if (context.getLevel().getBlockEntity(context.getClickedPos()) instanceof TurbineBladeHatchBlockEntity hatchBlockEntity) {
                //System.out.println("Setting the controller pos to " + controllerPos);
                hatchBlockEntity.setController(controllerPos);
                hatchBlockEntity.setHatchLayer(hatchPos);
                hatchBlockEntity.setChanged();
            }
            return InteractionResult.CONSUME;
        }

        return super.useOn(context);
    }
}
