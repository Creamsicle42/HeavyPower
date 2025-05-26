package com.creamsicle42.heavypower.blockentity.misc;

import com.creamsicle42.heavypower.misc.ISlotFluidHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;


/**
 * Interface for multiblock controllers that can have fluid input/output hatches
 */
public interface IFluidHatchManager {

    ISlotFluidHandler getFluidHandler();

    /**
     * Attempt to setup block as input hatch
     * @param pos The position to make an input hatch
     * @param face The clicked hatch face
     * @return True if the hatch has been successfully placed
     */
    default boolean tryMakeBlockInputHatch(BlockPos pos, Direction face) {
        return tryMakeBlockInputHatch(pos);
    }

    /**
     * Attempt to setup block as output hatch
     * @param pos The position to make an output hatch
     * @param face The clicked hatch face
     * @return True if the hatch has been successfully placed
     */
    default boolean tryMakeBlockOutputHatch(BlockPos pos, Direction face) {
        return tryMakeBlockOutputHatch(pos);
    }

    /**
     * Attempt to setup block as input hatch
     * @param pos The position to make an input hatch
     * @return True if the hatch has been successfully placed
     */
    default boolean tryMakeBlockInputHatch(BlockPos pos) {
        return false;
    }

    /**
     * Attempt to setup block as output hatch
     * @param pos The position to make an output hatch
     * @return True if the hatch has ben successfully placed
     */
    default boolean tryMakeBlockOutputHatch(BlockPos pos) {
        return false;
    }
}
