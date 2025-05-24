package com.creamsicle42.heavypower.registry.types;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class TurbineStage {
    private final double efficiency;
    private final double size;
    private final double mass;
    private final double friction;
    private final double expansion;

    public static final Codec<TurbineStage> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.DOUBLE.fieldOf("efficiency").orElse(0.0).forGetter(TurbineStage::getEfficiency),
            Codec.DOUBLE.fieldOf("size").orElse(0.0).forGetter(TurbineStage::getSize),
            Codec.DOUBLE.fieldOf("mass").orElse(0.0).forGetter(TurbineStage::getMass),
            Codec.DOUBLE.fieldOf("friction").orElse(0.0).forGetter(TurbineStage::getFriction),
            Codec.DOUBLE.fieldOf("expansion").orElse(1.0).forGetter(TurbineStage::getExpansion)
    ).apply(inst, TurbineStage::new));

    public TurbineStage(double efficiency, double size, double mass, double friction, double expansion) {
        this.efficiency = efficiency;
        this.size = size;
        this.mass = mass;
        this.friction = friction;
        this.expansion = expansion;
    }

    public double getEfficiency() {
        return efficiency;
    }

    public double getSize() {
        return size;
    }

    public double getMass() {
        return mass;
    }

    public double getFriction() {
        return friction;
    }

    public double getExpansion() {
        return expansion;
    }
}
