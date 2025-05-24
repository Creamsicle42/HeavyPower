package com.creamsicle42.heavypower.registry.types;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class ReactorMaterial {

    public static final Codec<ReactorMaterial> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                    Codec.STRING.fieldOf("name").forGetter(ReactorMaterial::getName),
                    Codec.DOUBLE.fieldOf("hotNeutronConversion").orElse(0.0).forGetter(ReactorMaterial::getHotNeutronConversion),
                    Codec.DOUBLE.fieldOf("coolNeutronConversion").orElse(0.0).forGetter(ReactorMaterial::getCoolNeutronConversion),
                    Codec.DOUBLE.fieldOf("hotNeutronAbsorption").orElse(0.0).forGetter(ReactorMaterial::getHotNeutronAbsorption),
                    Codec.DOUBLE.fieldOf("coolNeutronAbsorption").orElse(0.0).forGetter(ReactorMaterial::getCoolNeutronAbsorption),
                    Codec.DOUBLE.fieldOf("hotNeutProductionRate").orElse(0.0).forGetter(ReactorMaterial::getHotNeutProductionRate),
                    Codec.DOUBLE.fieldOf("coolNeutProductionRate").orElse(0.0).forGetter(ReactorMaterial::getCoolNeutProductionRate),
                    Codec.DOUBLE.fieldOf("heatProductionRate").orElse(0.0).forGetter(ReactorMaterial::getHeatProductionRate),
                    Codec.DOUBLE.fieldOf("hotNeutronPassive").orElse(0.0).forGetter(ReactorMaterial::getHotNeutronPassive),
                    Codec.DOUBLE.fieldOf("coolNeutronPassive").orElse(0.0).forGetter(ReactorMaterial::getCoolNeutronPassive),
                    Codec.DOUBLE.fieldOf("heatAbsorbRate").orElse(0.0).forGetter(ReactorMaterial::getHeatAbsorbRate),
                    Codec.DOUBLE.fieldOf("meltdownTemp").orElse(0.0).forGetter(ReactorMaterial::getMeltdownTemp),
                    Codec.BOOL.fieldOf("isCoolant").orElse(Boolean.FALSE).forGetter(ReactorMaterial::getCoolant)
            ).apply(inst, ReactorMaterial::new)
    );

    public static final ReactorMaterial BLANK = new ReactorMaterial(
            "blank",
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
            false
    );

    private final String name;
    private final Double hotNeutronConversion;
    private final Double coolNeutronConversion;
    private final Double hotNeutronAbsorption;
    private final Double coolNeutronAbsorption;
    private final Double hotNeutProductionRate;
    private final Double coolNeutProductionRate;
    private final Double heatProductionRate;
    private final Double hotNeutronPassive;
    private final Double coolNeutronPassive;
    private final Double heatAbsorbRate;
    private final Double meltdownTemp;
    private final Boolean isCoolant;


    public ReactorMaterial(String name, Double hotNeutronConversion, Double coolNeutronConversion, Double hotNeutronAbsorption, Double coolNeutronAbsorption, Double hotNeutProductionRate, Double coolNeutProductionRate, Double heatProductionRate, Double hotNeutronPassive, Double coolNeutronPassive, Double maxHeatAbsorb, Double meltdownTemp, Boolean isCoolant) {
        this.name = name;
        this.hotNeutronConversion = hotNeutronConversion;
        this.coolNeutronConversion = coolNeutronConversion;
        this.hotNeutronAbsorption = hotNeutronAbsorption;
        this.coolNeutronAbsorption = coolNeutronAbsorption;
        this.hotNeutProductionRate = hotNeutProductionRate;
        this.coolNeutProductionRate = coolNeutProductionRate;
        this.heatProductionRate = heatProductionRate;
        this.hotNeutronPassive = hotNeutronPassive;
        this.coolNeutronPassive = coolNeutronPassive;
        this.heatAbsorbRate = maxHeatAbsorb;
        this.isCoolant = isCoolant;
        this.meltdownTemp = meltdownTemp;
    }

    public ReactorMaterial(ReactorMaterial other) {
        this.name = other.name;
        this.hotNeutronConversion = other.hotNeutronConversion;
        this.coolNeutronConversion = other.coolNeutronConversion;
        this.hotNeutronAbsorption = other.hotNeutronAbsorption;
        this.coolNeutronAbsorption = other.coolNeutronAbsorption;
        this.hotNeutProductionRate = other.hotNeutProductionRate;
        this.coolNeutProductionRate = other.coolNeutProductionRate;
        this.heatProductionRate = other.heatProductionRate;
        this.hotNeutronPassive = other.hotNeutronPassive;
        this.coolNeutronPassive = other.coolNeutronPassive;
        this.heatAbsorbRate = other.heatAbsorbRate;
        this.isCoolant = other.isCoolant;
        this.meltdownTemp = other.meltdownTemp;

    }

    public String getName() {
        return name;
    }


    public Double getHotNeutronConversion() {
        return hotNeutronConversion;
    }

    public Double getCoolNeutronConversion() {
        return coolNeutronConversion;
    }

    public Double getHotNeutronAbsorption() {
        return hotNeutronAbsorption;
    }

    public Double getCoolNeutronAbsorption() {
        return coolNeutronAbsorption;
    }

    public Double getHotNeutProductionRate() {
        return hotNeutProductionRate;
    }

    public Double getCoolNeutProductionRate() {
        return coolNeutProductionRate;
    }

    public Double getHeatProductionRate() {
        return heatProductionRate;
    }

    public Double getHotNeutronPassive() {
        return hotNeutronPassive;
    }

    public Double getCoolNeutronPassive() {
        return coolNeutronPassive;
    }

    public Double getHeatAbsorbRate() {
        return heatAbsorbRate;
    }

    public Boolean getCoolant() {
        return isCoolant;
    }

    public Double getMeltdownTemp() {
        return meltdownTemp;
    }


    public static class Builder {
        private final String name;
        private Double hotNeutronConversion;
        private Double coolNeutronConversion;
        private Double hotNeutronAbsorption;
        private Double coolNeutronAbsorption;
        private Double hotNeutProductionRate;
        private Double coolNeutProductionRate;
        private Double heatProductionRate;
        private Double hotNeutronPassive;
        private Double coolNeutronPassive;
        private Double heatAbsorbRate;
        private Double meltdownTemp;
        private Boolean isCoolant;

        public Builder(String name) {
            this.name = name;
            hotNeutronConversion = 0.0;
            coolNeutronConversion = 0.0;
            hotNeutronAbsorption = 0.0;
            coolNeutronAbsorption = 0.0;
            hotNeutProductionRate = 0.0;
            coolNeutProductionRate = 0.0;
            heatProductionRate = 0.0;
            hotNeutronPassive = 0.0;
            coolNeutronPassive = 0.0;
            heatAbsorbRate = 0.0;
            meltdownTemp = 0.0;
            isCoolant = false;
        }

        public ReactorMaterial build() {
            return new ReactorMaterial(
                name,
                hotNeutronConversion,
                coolNeutronConversion,
                hotNeutronAbsorption,
                coolNeutronAbsorption,
                hotNeutProductionRate,
                coolNeutProductionRate,
                heatProductionRate,
                hotNeutronPassive,
                coolNeutronPassive,
                    heatAbsorbRate,
                meltdownTemp,
                isCoolant
            );
        }

        public Builder withPassiveStats(double hot, double cool) {
            hotNeutronPassive = hot;
            coolNeutronPassive = cool;
            return this;
        }

        public Builder conversionStats(double hot, double cool) {
            hotNeutronConversion = hot;
            coolNeutronConversion = cool;
            return this;
        }

        public Builder withAbsorptionStats(double hot, double cool) {
            hotNeutronAbsorption = hot;
            coolNeutronAbsorption = cool;
            return this;
        }

        public Builder withProductionStats(double hot, double cool, double heat) {
            hotNeutProductionRate = hot;
            coolNeutProductionRate = cool;
            heatProductionRate = heat;
            return this;
        }

        public Builder withCoolantStats(double maxHeatAbsorb) {
            this.heatAbsorbRate = maxHeatAbsorb;
            this.isCoolant = true;
            return this;
        }

        public Builder withMeltdownStats(double meltdownTemp) {
            this.meltdownTemp = meltdownTemp;
            return this;
        }


    }
}
