package com.creativechasm.cropcultivation.registry;

import com.creativechasm.cropcultivation.api.plant.ICropEntry;
import com.creativechasm.cropcultivation.api.world.ClimateUtil;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class DefaultCropEntry implements ICropEntry
{
    private final float nitrogen;
    private final float phosphorus;
    private final float potassium;
    private final float minPH;
    private final float maxPH;
    private final float minMoisture;
    private final float maxMoisture;
    private final float minTemperature;
    private final float maxTemperature;

    public DefaultCropEntry(float nitrogen, float phosphorus, float potassium, float minPH, float maxPH, float minMoisture, float maxMoisture, float minTemperature, float maxTemperature) {
        this.nitrogen = nitrogen;
        this.phosphorus = phosphorus;
        this.potassium = potassium;
        this.minPH = minPH;
        this.maxPH = maxPH;
        this.minMoisture = minMoisture;
        this.maxMoisture = maxMoisture;
        this.minTemperature = ClimateUtil.convertTemperatureCelsiusToMC(minTemperature);
        this.maxTemperature = ClimateUtil.convertTemperatureCelsiusToMC(maxTemperature);
    }

    @Override
    public float getNitrogenNeed() {
        return nitrogen;
    }

    @Override
    public float getPhosphorusNeed() {
        return phosphorus;
    }

    @Override
    public float getPotassiumNeed() {
        return potassium;
    }

    @Override
    public float getMinSoilPH() {
        return minPH;
    }

    @Override
    public float getMaxSoilPH() {
        return maxPH;
    }

    @Override
    public float getMinSoilMoisture() {
        return minMoisture;
    }

    @Override
    public float getMaxSoilMoisture() {
        return maxMoisture;
    }

    @Override
    public float getMinTemperature() {
        return minTemperature;
    }

    @Override
    public float getMaxTemperature() {
        return maxTemperature;
    }

    @Override
    public String toString() {
        return "DefaultCropEntry{" +
                "nitrogen=" + nitrogen +
                ", phosphorus=" + phosphorus +
                ", potassium=" + potassium +
                ", minPH=" + minPH +
                ", maxPH=" + maxPH +
                ", minMoisture=" + minMoisture +
                ", maxMoisture=" + maxMoisture +
                ", minTemperature=" + minTemperature +
                ", maxTemperature=" + maxTemperature +
                '}';
    }
}
