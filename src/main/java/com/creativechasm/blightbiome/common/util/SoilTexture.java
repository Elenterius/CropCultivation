package com.creativechasm.blightbiome.common.util;

public enum SoilTexture {
    // https://support.rainmachine.com/hc/en-us/articles/228001248-Soil-Types

    CLAY(0.357f, DrainageType.VERY_POORLY_DRAINED, 6),
    SILTY_CLAY(0.3485f, DrainageType.POORLY_DRAINED, 6),
    SANDY_CLAY(0.306f, DrainageType.IMPERFECTLY_DRAINED, 5),
    CLAY_LOAM(0.306f, DrainageType.MODERATELY_WELL_DRAINED, 5),
    SANDY_CLAY_LOAM(0.306f, DrainageType.MODERATELY_WELL_DRAINED, 5),
    LOAM(0.26f, DrainageType.WELL_DRAINED, 5),
    SILTY_LOAM(0.272f, DrainageType.IMPERFECTLY_DRAINED, 5),
    SILT(0.255f, DrainageType.IMPERFECTLY_DRAINED, 4),
    SILTY_CLAY_LOAM(0.2365f, DrainageType.MODERATELY_WELL_DRAINED, 4),
    SANDY_LOAM(0.17f, DrainageType.IMPERFECTLY_DRAINED, 4),
    LOAMY_SAND(0.14f, DrainageType.IMPERFECTLY_DRAINED, 4),
    SAND(0.1f, DrainageType.RAPIDLY_DRAINED, 3);

    public static float MAX_DRAINAGE_AMOUNT = 2f;
    public static float ORGANIC_MATTER_MODIFIER = 0.125f;
    public static byte maxMoistureContent = 10;
    public static float depletionLinePct = 0.25f;

    private final float fieldCapacity;
    private final DrainageType drainageType;
    private final byte maxWaterDistance;

    SoilTexture(float fieldCapacityPct, DrainageType drainageType, int maxWaterDistance) {
        this.fieldCapacity = fieldCapacityPct;
        this.drainageType = drainageType;
        this.maxWaterDistance = (byte) maxWaterDistance;
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