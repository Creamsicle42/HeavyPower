package com.creamsicle42.heavypower.blockentity.fissionreactor;

import com.creamsicle42.heavypower.Config;
import com.creamsicle42.heavypower.item.custom.ReactorRodItem;
import com.creamsicle42.heavypower.registry.types.ReactorMaterial;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Helper class for fission reactor calculations
 */
public class FissionCalculationHelper {

    /**
     * Runs one tick of reactor simulation. Modifies input lists. Input lists must all be of the same shape
     * @param items A grid representing all reactor rod items
     * @param heats A grid representing the heat of all reactor spaces
     * @param hotNeutrons A grid representing the not neutron count in all reactor spaces
     * @param coolNeutrons A grid representing the cool neutron count in all reactor spaces
     * @return The relevant sim results
     */
    public static SimulationResult doSimStep(ItemStack[][] items, double[][] heats, double[][] hotNeutrons, double[][] coolNeutrons, double coolantMaxTemp, double coolantTransferBonus, HolderLookup.Provider registry) {
        if (heats.length != items.length) {return new SimulationResult(0, 0, 0);}

        // Heat and neutron spread
        for (int i = 0; i < Config.fissionReactorHeatSteps; i++)
            runSpreadStep(heats, Config.fissionReactorHeatFactor);
        runSpreadStep(hotNeutrons, 0.24);
        runSpreadStep(coolNeutrons, 0.24);

        // Setup data constants
        double absorbedHeat = 0.0;
        double hottestCore = 0.0;
        double heatSum = 0.0;
        int cellCount = 0;

        for (int y = 0; y < items.length; y++) {
            for (int x = 0; x < items[y].length; x++) {
                cellCount++;
                if (!(items[y][x].getItem() instanceof ReactorRodItem rodItem)) {continue;}

                ReactorMaterial rodMaterial = rodItem.getReactorMaterial(registry);
                // Emission step
                hotNeutrons[y][x] += rodMaterial.getHotNeutronPassive();
                coolNeutrons[y][x] += rodMaterial.getCoolNeutronPassive();

                // Conversion Step
                double hotNeutronConversion = hotNeutrons[y][x] * rodMaterial.getHotNeutronConversion();
                double coolNeutronConversion = hotNeutrons[y][x] * rodMaterial.getCoolNeutronConversion();

                hotNeutrons[y][x] -= hotNeutronConversion;
                coolNeutrons[y][x] += hotNeutronConversion - coolNeutronConversion;
                heats[y][x] += coolNeutronConversion + hotNeutronConversion;

                // Reaction Step
                double hotNeutronAbsorption = hotNeutrons[y][x] * rodMaterial.getHotNeutronAbsorption();
                double coolNeutronAbsorption = coolNeutrons[y][x] * rodMaterial.getCoolNeutronAbsorption();

                hotNeutrons[y][x] -= hotNeutronAbsorption;
                coolNeutrons[y][x] -= coolNeutronAbsorption;

                double totalAbsorbed = hotNeutronAbsorption + coolNeutronAbsorption;

                hotNeutrons[y][x] += totalAbsorbed * rodMaterial.getHotNeutProductionRate();
                coolNeutrons[y][x] += totalAbsorbed * rodMaterial.getCoolNeutProductionRate();
                heats[y][x] += totalAbsorbed * rodMaterial.getHeatProductionRate();

                // Coolant Step
                double rodTemp = heats[y][x] / Config.fissionReactorThermalMass;
                if (rodMaterial.getCoolant() && rodTemp > coolantMaxTemp) {
                    double coolantTempDelta = rodTemp - coolantMaxTemp;
                    double desiredAbsorb = coolantTempDelta * rodMaterial.getHeatAbsorbRate() * coolantTransferBonus;
                    double maxAbsorb = coolantTempDelta * Config.fissionReactorThermalMass;
                    absorbedHeat += Math.min(desiredAbsorb, maxAbsorb);
                    heats[y][x] -= Math.min(desiredAbsorb, maxAbsorb);
                }

                hottestCore = Math.max(hottestCore, heats[y][x] / Config.fissionReactorThermalMass);
                heatSum += heats[y][x] / Config.fissionReactorThermalMass;
            }
        }

        return new SimulationResult(absorbedHeat, hottestCore, heatSum / cellCount);
    }

    /**
     * Runs a hookes law heat spread simulation on a field. Modifies input field.
     * @param field The field to modify
     * @param spreadFactor The spread factor between adjacent grid cells (should NEVER be greater than or equal to  0.25)
     */
    static void runSpreadStep(double[][] field, double spreadFactor) {
        double[][] oldHeats = new double[field.length][field[0].length];
        for (int y = 0; y < field.length; y++) {
            oldHeats[y] = field[y].clone();
        }
        for (int y = 0; y < field.length; y++) {
            for (int x = 0; x < field[y].length; x++) {
                double heatDelta = 0.0;
                if (x > 0) {
                    heatDelta += (oldHeats[y][x - 1] - oldHeats[y][x]) * spreadFactor;
                }
                if (x < field[0].length - 1) {
                    heatDelta += (oldHeats[y][x + 1] - oldHeats[y][x]) * spreadFactor;
                }
                if (y > 0) {
                    heatDelta += (oldHeats[y - 1][x] - oldHeats[y][x]) * spreadFactor;
                }
                if (y < field.length - 1) {
                    heatDelta += (oldHeats[y + 1][x] - oldHeats[y][x]) * spreadFactor;
                }
                field[y][x] += heatDelta;
            }
        }
    }

    public record SimulationResult(double absorbedHeat, double hottest, double averageHeat) {}
}
