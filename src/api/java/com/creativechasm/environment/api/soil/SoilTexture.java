package com.creativechasm.environment.api.soil;

import net.minecraft.util.math.MathHelper;

public enum SoilTexture {
    // https://support.rainmachine.com/hc/en-us/articles/228001248-Soil-Types

    // slightly alkaline to neutral soil pH was picked because rain decreases soil pH levels in the long run
    CLAY(0.357f, DrainageType.VERY_POORLY_DRAINED, 6, SoilPHType.SLIGHTLY_ALKALINE, 0xb1d6ff),
    SILTY_CLAY(0.3485f, DrainageType.POORLY_DRAINED, 6, SoilPHType.SLIGHTLY_ALKALINE, MathHelper.hsvToRGB(224f, 0.21f, 0.8f)),
    SANDY_CLAY(0.306f, DrainageType.IMPERFECTLY_DRAINED, 5, SoilPHType.SLIGHTLY_ALKALINE, MathHelper.hsvToRGB(195f, 0.21f, 0.99f)),
    CLAY_LOAM(0.306f, DrainageType.MODERATELY_WELL_DRAINED, 5, SoilPHType.SLIGHTLY_ALKALINE, MathHelper.hsvToRGB(195f, 0.31f, 1f)),
    SANDY_CLAY_LOAM(0.306f, DrainageType.MODERATELY_WELL_DRAINED, 5, SoilPHType.NEUTRAL, MathHelper.hsvToRGB(28f, 0.76f, 0.85f)), //0xd87f33
    LOAM(0.26f, DrainageType.WELL_DRAINED, 5, SoilPHType.NEUTRAL, 0xad8363),
    SILTY_LOAM(0.272f, DrainageType.IMPERFECTLY_DRAINED, 5, SoilPHType.NEUTRAL, MathHelper.hsvToRGB(16f, 0.23f, 0.50f)), //0x806a62
    SILT(0.255f, DrainageType.IMPERFECTLY_DRAINED, 4, SoilPHType.NEUTRAL, MathHelper.hsvToRGB(0f, 0f, 0.50f)), //0x808080
    SILTY_CLAY_LOAM(0.2365f, DrainageType.MODERATELY_WELL_DRAINED, 4, SoilPHType.SLIGHTLY_ALKALINE, MathHelper.hsvToRGB(16f, 0.23f, 0.50f)), //0x806a62
    SANDY_LOAM(0.17f, DrainageType.IMPERFECTLY_DRAINED, 4, SoilPHType.NEUTRAL, MathHelper.hsvToRGB(26f, 0.11f, 0.89f)), //0xe3d5ca
    LOAMY_SAND(0.14f, DrainageType.IMPERFECTLY_DRAINED, 4, SoilPHType.NEUTRAL, MathHelper.hsvToRGB(17f, 0.11f, 0.89f)), //0xe3d1ca
    SAND(0.1f, DrainageType.RAPIDLY_DRAINED, 3, SoilPHType.NEUTRAL, MathHelper.hsvToRGB(57f, 0.21f, 0.99f)); //0xfcf9c7

    public static float MAX_DRAINAGE_AMOUNT = 2f;
    public static float ORGANIC_MATTER_MODIFIER = 0.125f;
    public static int maxMoistureContent = 10;
    public static float depletionPointPct = 0.25f;
    public static float wiltingPointPct = 0.25f;

    private final float fieldCapacity;
    private final DrainageType drainageType;
    private final byte maxWaterDistance;
    public final SoilPHType pHType;
    public final int color;

    SoilTexture(float fieldCapacityPct, DrainageType drainageType, int maxWaterDistance, SoilPHType pHType, int color) {
        this.fieldCapacity = fieldCapacityPct;
        this.drainageType = drainageType;
        this.maxWaterDistance = (byte) maxWaterDistance;
        this.pHType = pHType;
        this.color = color;
    }

    public float getWaterHoldingCapacity() {
        return fieldCapacity;
    }

    public int getMaxWaterDistance() {
        return maxWaterDistance;
    }

    public float getDrainageLoss() {
        return drainageType.getMultiplier() + MAX_DRAINAGE_AMOUNT;
    }
}