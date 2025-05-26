package com.creamsicle42.heavypower.misc;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

/**
 * Specialized fluid handler that can deal with individual slots being targeted for filling/draining
 */
public interface ISlotFluidHandler extends IFluidHandler {


    /**
     * Fills fluid into internal tanks, distribution is left entirely to the IFluidHandler.
     *
     * @param targetSlot The target slot to be filled
     * @param resource FluidStack representing the Fluid and maximum amount of fluid to be filled.
     * @param action   If SIMULATE, fill will only be simulated.
     * @return Amount of resource that was (or would have been, if simulated) filled.
     */
    default int fill(int targetSlot ,FluidStack resource, FluidAction action) {
        return fill(resource, action);
    }

    /**
     * Drains fluid out of internal tanks, distribution is left entirely to the IFluidHandler.
     *
     * @param targetSlot The target slot to be drained
     * @param resource FluidStack representing the Fluid and maximum amount of fluid to be drained.
     * @param action   If SIMULATE, drain will only be simulated.
     * @return FluidStack representing the Fluid and amount that was (or would have been, if
     *         simulated) drained.
     */
    default FluidStack drain(int targetSlot, FluidStack resource, FluidAction action) {
        return drain(resource, action);
    }

    /**
     * Drains fluid out of internal tanks, distribution is left entirely to the IFluidHandler.
     * <p>
     * This method is not Fluid-sensitive.
     *
     * @param targetSlot The target slot to be drained
     * @param maxDrain Maximum amount of fluid to drain.
     * @param action   If SIMULATE, drain will only be simulated.
     * @return FluidStack representing the Fluid and amount that was (or would have been, if
     *         simulated) drained.
     */
    default FluidStack drain(int targetSlot, int maxDrain, FluidAction action) {
        return drain(maxDrain, action);
    }
}
