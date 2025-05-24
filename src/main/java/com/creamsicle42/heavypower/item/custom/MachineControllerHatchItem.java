package com.creamsicle42.heavypower.item.custom;

import com.creamsicle42.heavypower.blockentity.misc.IFluidHatchManager;
import com.creamsicle42.heavypower.blockentity.misc.ISimpleMachineController;
import com.creamsicle42.heavypower.blockentity.misc.SimpleMachinePartBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;

import java.util.function.Consumer;

public class MachineControllerHatchItem extends Item {

    public interface UseFunc {
        boolean run(BlockPos baseControllerPos, Direction face, ServerLevel level);
    }

    UseFunc use;

    public MachineControllerHatchItem(Properties properties, UseFunc use) {
        super(properties);
        this.use = use;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getLevel().isClientSide() || !context.getPlayer().isCrouching()) {
            return super.useOn(context);
        }
        if (use.run(context.getClickedPos(), context.getClickedFace(), (ServerLevel) context.getLevel())) {
            return InteractionResult.CONSUME;
        }
        return super.useOn(context);
    }
}
