package com.creamsicle42.heavypower.blockentity.misc;

import net.minecraft.core.BlockPos;

public interface IComputerHatchManager {

    /**
     * Attempt to setup block as computer hatch
     * @param pos The position to make an computer hatch
     * @return True if the hatch has been successfully placed
     */
    boolean tryMakeBlockComputerHatch(BlockPos pos);
}
