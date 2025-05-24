package com.creamsicle42.heavypower.blockentity.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;


/**
 * Interface for multiblock controllers that can have item input/output busses
 */
public interface IItemBusManager {

    IItemHandler getItemHandler();

    /**
     * Attempt to setup block as input hatch
     * @param pos The position to make an input hatch
     * @return True if the hatch has been successfully placed
     */
    default boolean tryMakeBlockInputBus(BlockPos pos, Direction direction) {
        return tryMakeBlockInputBus(pos);
    }

    /**
     * Attempt to setup block as output hatch
     * @param pos The position to make an output hatch
     * @return True if the hatch has been successfully placed
     */
    default boolean tryMakeBlockOutputBus(BlockPos pos, Direction direction) {
        return tryMakeBlockOutputBus(pos);
    }

    /**
     * Attempt to setup block as input hatch
     * @param pos The position to make an input hatch
     * @return True if the hatch has been successfully placed
     */
    default boolean tryMakeBlockInputBus(BlockPos pos) {
        return false;
    }

    /**
     * Attempt to setup block as output hatch
     * @param pos The position to make an output hatch
     * @return True if the hatch has been successfully placed
     */
    default boolean tryMakeBlockOutputBus(BlockPos pos) {
        return false;
    }
}
