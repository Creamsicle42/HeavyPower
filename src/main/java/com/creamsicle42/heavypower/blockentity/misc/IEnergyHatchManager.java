package com.creamsicle42.heavypower.blockentity.misc;

import net.minecraft.core.BlockPos;
import net.neoforged.neoforge.energy.IEnergyStorage;


/**
 * Interface for multiblock controllers that can have fluid input/output hatches
 */
public interface IEnergyHatchManager {
    /**
     * Attempt to setup block as input hatch
     * @param pos The position to make an input hatch
     * @return True if the hatch has been successfully placed
     */
    boolean tryMakeBlockEnergyInputHatch(BlockPos pos);

    /**
     * Attempt to setup block as output hatch
     * @param pos The position to make an output hatch
     * @return True if the hatch has been successfully placed
     */
    boolean tryMakeBlockEnergyOutputHatch(BlockPos pos);

    IEnergyStorage getEnergyStorage();
}
