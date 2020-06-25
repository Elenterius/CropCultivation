package com.creativechasm.blightbiome.common.util;

import net.minecraft.util.math.MathHelper;

public enum SoilTexture {
    SAND(0.17f, 2.45f),
    SANDY_CLAY(0.3f, 1.2f),
    SANDY_LOAM(0.32f, 1.3f),
    SANDY_SILT(0.36f, 1.311f),
    LOAM(0.4f, 0.825f),
    SILT_CLAY(0.4f, 0.718f),
    SILT(0.41f, 1.3125f),
    LOAM_CLAY(0.420f, 0.025f),
    CLAY(0.48f, 0.125f);

    public static float ORGANIC_MATTER_MODIFIER = 0.125f;
    public static int maxMoistureContent = 10;
    public static float depletionLinePct = 0.25f;

    float waterCapacity;
    float seepageLoss;

    SoilTexture(float waterHoldingCapacity, float seepageLoss) {
        this.waterCapacity = waterHoldingCapacity;
        this.seepageLoss = seepageLoss;
    }

    public float getWaterHoldingCapacity() {
        return waterCapacity;
    }

    public int getMaxWaterDistance() {
        // water search radius of vanilla farmland block is 4
        // loam is considered "equal" to vanilla farmland
        // LOAM.waterCapacity * (10 + 1f) = 4.4f --> 4
        return MathHelper.floor(waterCapacity * (maxMoistureContent + 1f));
    }

    public float getSeepageLoss() {
        return seepageLoss;
    }
}