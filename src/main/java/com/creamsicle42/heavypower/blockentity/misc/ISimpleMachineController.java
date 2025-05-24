package com.creamsicle42.heavypower.blockentity.misc;

import net.minecraft.core.BlockPos;

public interface ISimpleMachineController {
    /**
     * Called when a component block is broken
     * @param componentPos The position of the component block
     */
    void onComponentBreak(BlockPos componentPos);

}
