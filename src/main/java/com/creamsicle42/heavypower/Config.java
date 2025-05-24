package com.creamsicle42.heavypower;


import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;


// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Forge's config APIs
@EventBusSubscriber(modid = HeavyPower.MODID, bus = EventBusSubscriber.Bus.MOD)
public class Config
{
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();


    private static final ModConfigSpec.IntValue HU_PER_DEGREE = BUILDER
            .comment("Heat units per degree of temperature.")
            .defineInRange("huPerDeg", 10, 1, 100);

    private static final ModConfigSpec.IntValue FISSION_REACTOR_THERMAL_MASS = BUILDER
            .comment("Heat units per degree of temperature in the fission reactor")
            .defineInRange("fission_thermal_mass", 100, 10, 1000);

    private static final ModConfigSpec.IntValue FISSION_HEAT_SPREAD_STEPS = BUILDER
            .comment("Number of heat spread steps to run per fission reactor update")
            .defineInRange("fision_heat_steps", 8, 1, 16);

    private static final ModConfigSpec.DoubleValue FISSION_HEAT_SPREAD_FACTOR = BUILDER
            .comment("Heat spread factor for fission reactor")
            .defineInRange("fission_heat_spread", 0.1, 0.01, 0.25);

    private static final ModConfigSpec.IntValue MAX_TURBINE_LENGTH = BUILDER
            .comment("Maximum length of the turbine multiblock")
            .defineInRange("max_turbine_length", 13, 4, 64);

    private static final ModConfigSpec.IntValue MAX_TURBINE_RADIUS = BUILDER
            .comment("Maximum radius of the turbine multiblock")
            .defineInRange("max_turbine_radius", 3, 1, 7);

    private static final ModConfigSpec.IntValue MAX_EVAP_TOWER_SIZE = BUILDER
            .comment("Maximum side length of the Evaporation Tower Multiblock")
            .defineInRange("evapTowerMaxSize", 16, 3, 32);

    private static final ModConfigSpec.IntValue MAX_EVAP_TOWER_HEIGHT = BUILDER
            .comment("Maximum height of the Evaporation Tower Multiblock")
            .defineInRange("evapTowerMaxHeight", 32, 1, 100);

    static final ModConfigSpec SPEC = BUILDER.build();

    public static int heatUnitsPerDegree = 10;
    public static int maxTurbineLength = 13;
    public static int maxTurbineRadius = 3;

    public static int fissionReactorThermalMass = 100;
    public static int fissionReactorHeatSteps = 8;
    public static double fissionReactorHeatFactor = 0.1;

    public static int evaporationTowerMaxSize = 16;
    public static int evaporationTowerMaxHeight = 32;

    @net.neoforged.bus.api.SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        heatUnitsPerDegree = HU_PER_DEGREE.get();
        maxTurbineLength = MAX_TURBINE_LENGTH.get();
        maxTurbineRadius = MAX_TURBINE_RADIUS.get();

        fissionReactorThermalMass = FISSION_REACTOR_THERMAL_MASS.get();
        fissionReactorHeatSteps = FISSION_HEAT_SPREAD_STEPS.get();
        fissionReactorHeatFactor = FISSION_HEAT_SPREAD_FACTOR.get();

        evaporationTowerMaxSize = MAX_EVAP_TOWER_SIZE.get();
        evaporationTowerMaxHeight = MAX_EVAP_TOWER_HEIGHT.get();
    }
}
