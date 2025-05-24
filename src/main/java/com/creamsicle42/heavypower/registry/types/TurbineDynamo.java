package com.creamsicle42.heavypower.registry.types;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class TurbineDynamo {
    private final double clampForce;
    private final double efficiencyBonus;
    private final double efficiencyFalloff;
    private final double idealSpeed;
    private final double baseEfficiency;

    public static final Codec<TurbineDynamo> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.DOUBLE.fieldOf("clampForce").orElse(0.0).forGetter(TurbineDynamo::getClampForce),
            Codec.DOUBLE.fieldOf("efficiencyBonus").orElse(0.0).forGetter(TurbineDynamo::getEfficiencyBonus),
            Codec.DOUBLE.fieldOf("efficiencyFalloff").orElse(0.0).forGetter(TurbineDynamo::getEfficiencyFalloff),
            Codec.DOUBLE.fieldOf("idealSpeed").orElse(0.0).forGetter(TurbineDynamo::getIdealSpeed),
            Codec.DOUBLE.fieldOf("baseEfficiency").orElse(0.0).forGetter(TurbineDynamo::getBaseEfficiency)
    ).apply(inst, TurbineDynamo::new));

    public TurbineDynamo(double clampForce, double efficiencyBonus, double efficiencyFalloff, double idealSpeed, double baseEfficiency) {
        this.clampForce = clampForce;
        this.efficiencyBonus = efficiencyBonus;
        this.efficiencyFalloff = efficiencyFalloff;
        this.idealSpeed = idealSpeed;
        this.baseEfficiency = baseEfficiency;
    }

    public double getClampForce() {
        return clampForce;
    }

    public double getEfficiencyBonus() {
        return efficiencyBonus;
    }

    public double getEfficiencyFalloff() {
        return efficiencyFalloff;
    }

    public double getIdealSpeed() {
        return idealSpeed;
    }

    public double getBaseEfficiency() {
        return baseEfficiency;
    }
}
