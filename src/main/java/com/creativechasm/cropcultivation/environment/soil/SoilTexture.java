package com.creativechasm.cropcultivation.environment.soil;

import net.minecraft.util.math.MathHelper;

public enum SoilTexture {
    // https://support.rainmachine.com/hc/en-us/articles/228001248-Soil-Types

    // slightly alkaline to neutral soil pH was picked because rain decreases soil pH levels in the long run
    CLAY(0.357f, SoilDrainage.VERY_POORLY_DRAINED, 6, SoilPH.SLIGHTLY_ALKALINE, 0xb1d6ff),
    SILTY_CLAY(0.3485f, SoilDrainage.POORLY_DRAINED, 6, SoilPH.SLIGHTLY_ALKALINE, MathHelper.hsvToRGB(224f, 0.21f, 0.8f)),
    SANDY_CLAY(0.306f, SoilDrainage.IMPERFECTLY_DRAINED, 5, SoilPH.SLIGHTLY_ALKALINE, MathHelper.hsvToRGB(195f, 0.21f, 0.99f)),
    CLAY_LOAM(0.306f, SoilDrainage.MODERATELY_WELL_DRAINED, 5, SoilPH.SLIGHTLY_ALKALINE, MathHelper.hsvToRGB(195f, 0.31f, 1f)),
    SANDY_CLAY_LOAM(0.306f, SoilDrainage.MODERATELY_WELL_DRAINED, 5, SoilPH.NEUTRAL, MathHelper.hsvToRGB(28f, 0.76f, 0.85f)), //0xd87f33
    LOAM(0.26f, SoilDrainage.WELL_DRAINED, 5, SoilPH.NEUTRAL, 0xad8363),
    SILTY_LOAM(0.272f, SoilDrainage.IMPERFECTLY_DRAINED, 5, SoilPH.NEUTRAL, MathHelper.hsvToRGB(16f, 0.23f, 0.50f)), //0x806a62
    SILT(0.255f, SoilDrainage.IMPERFECTLY_DRAINED, 4, SoilPH.NEUTRAL, MathHelper.hsvToRGB(0f, 0f, 0.50f)), //0x808080
    SILTY_CLAY_LOAM(0.2365f, SoilDrainage.MODERATELY_WELL_DRAINED, 4, SoilPH.SLIGHTLY_ALKALINE, MathHelper.hsvToRGB(16f, 0.23f, 0.50f)), //0x806a62
    SANDY_LOAM(0.17f, SoilDrainage.IMPERFECTLY_DRAINED, 4, SoilPH.NEUTRAL, MathHelper.hsvToRGB(26f, 0.11f, 0.89f)), //0xe3d5ca
    LOAMY_SAND(0.14f, SoilDrainage.IMPERFECTLY_DRAINED, 4, SoilPH.NEUTRAL, MathHelper.hsvToRGB(17f, 0.11f, 0.89f)), //0xe3d1ca
    SAND(0.1f, SoilDrainage.RAPIDLY_DRAINED, 3, SoilPH.NEUTRAL, MathHelper.hsvToRGB(57f, 0.21f, 0.99f)); //0xfcf9c7

    public static float MAX_DRAINAGE_AMOUNT = 2f;
    public static float ORGANIC_MATTER_MULTIPLIER = 0.125f;
    public static float DEPLETION_POINT_PCT = 0.25f;
    public static float WILTING_POINT_PCT = 0.5f;

    private final float fieldCapacity;
    private final SoilDrainage drainageType;
    private final byte maxWaterDistance;
    public final SoilPH pHType;
    public final int color;

    SoilTexture(float fieldCapacityPct, SoilDrainage drainageType, int maxWaterDistance, SoilPH pHType, int color) {
        this.fieldCapacity = fieldCapacityPct;
        this.drainageType = drainageType;
        this.maxWaterDistance = (byte) maxWaterDistance;
        this.pHType = pHType;
        this.color = color;
    }

    public float getWaterHoldingCapacity() {
        return fieldCapacity;
    }

    public float getDepletionPoint() {
        return fieldCapacity * DEPLETION_POINT_PCT;
    }

    public float getWiltingPoint() {
        return fieldCapacity * WILTING_POINT_PCT;
    }

    public int getMaxMoistureCapacity(int organicMatter) {
        int capacity = MathHelper.floor(SoilMoisture.MAX_VALUE * (2f * fieldCapacity + organicMatter * ORGANIC_MATTER_MULTIPLIER));
        return MathHelper.clamp(capacity, 0, SoilMoisture.MAX_VALUE - 1);
    }

    public int getMinMoistureCapacity(int organicMatter, int maxMoistureCapacity) {
        float retentionModifier = organicMatter * ORGANIC_MATTER_MULTIPLIER;
        int capacity = MathHelper.floor(maxMoistureCapacity - SoilMoisture.MAX_VALUE * (2f * getWiltingPoint() + retentionModifier) - retentionModifier + organicMatter);
        return MathHelper.clamp(capacity, 1, SoilMoisture.MAX_VALUE);
    }

    public int getMaxWaterDistance() {
        return maxWaterDistance;
    }

    public SoilDrainage getDrainageType() {
        return drainageType;
    }

    public float getDrainageLoss() {
        return drainageType.getMultiplier() * MAX_DRAINAGE_AMOUNT;
    }

    /**
     * Depending on the soil consistency, returns the amount by how much the the pH can be increased.<br>
     * Implicitly reflects amount of lime needed for increasing pH depending on soil consistency.
     *
     * @return pH increase amount
     */
    public float getLimingModifier() {
        return (drainageType.getMultiplier() + 0.01f) * 0.6f;
    }

    public float getAcidifyingModifier() {
        return getLimingModifier();
    }
}