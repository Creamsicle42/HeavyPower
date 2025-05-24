package com.creamsicle42.heavypower.computercraft;

import com.creamsicle42.heavypower.HeavyPower;
import com.creamsicle42.heavypower.blockentity.fissionreactor.FissionComputerHatchBlockEntity;
import com.creamsicle42.heavypower.blockentity.fissionreactor.FissionReactorControllerBlockEntity;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.GenericPeripheral;

public class FissionReactorPeripheral implements GenericPeripheral {
    /**
     * A unique identifier for this generic source.
     * <p>
     * While this can return an arbitrary string, it's recommended that this is formatted the same was as Minecraft's
     * resource locations/identifiers, so is of the form {@code "mod_id:source_id"}.
     *
     * @return This source's identifier.
     */
    @Override
    public String id() {
        return HeavyPower.modResource("fission_reactor").toString();
    }

    @LuaFunction(mainThread = true)
    public double getAverageTemperature(FissionComputerHatchBlockEntity computerHatchBlockEntity) {
        if (!(computerHatchBlockEntity.getController().orElse(null) instanceof FissionReactorControllerBlockEntity fissionController)) {return 0.0;}

        return fissionController.getAverageTemperature();
    }

    @LuaFunction(mainThread = true)
    public double getHottestCoreTemp(FissionComputerHatchBlockEntity computerHatchBlockEntity) {
        if (!(computerHatchBlockEntity.getController().orElse(null) instanceof FissionReactorControllerBlockEntity fissionController)) {return 0.0;}

        return fissionController.getHottestCore();
    }
}
