package com.creativechasm.cropcultivation.api.soil;

import net.minecraft.util.math.MathHelper;

public enum SoilMoisture {
    STANDING_WATER(4f, Float.POSITIVE_INFINITY, 10), /* flooded? - "waterlogged" */
    EXCESSIVELY_WET(3f, 3.9f, 9),
    WET(2f, 2.9f, 8),
    ABNORMALLY_MOIST(1f, 1.9f, 7),
    MOIST(0.1f, 0.9f, 6),
    AVERAGE_1(0f, 0f, 5), //TODO: remove this and add waterlogged state instead?
    AVERAGE_0(0f, 0f, 4),
    DRY(-0.1f, -0.9f, 3),
    ABNORMALLY_DRY(-1f, -1.9f, 2),
    EXCESSIVELY_DRY(-2f, -2.9f, 1),
    SEVERELY_DRY(-3f, Float.NEGATIVE_INFINITY, 0);

    public static int MAX_VALUE = 10;
    float minValue;
    float maxValue;
    int level;
    private static final SoilMoisture[] sortedDryToWet = new SoilMoisture[]{SEVERELY_DRY, EXCESSIVELY_DRY, ABNORMALLY_DRY, DRY, AVERAGE_0, AVERAGE_1, MOIST, ABNORMALLY_MOIST, WET, EXCESSIVELY_WET, STANDING_WATER};

    SoilMoisture(float minValue, float maxValue, int moistureLevel) {
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.level = moistureLevel;
    }

    public static SoilMoisture fromMoistureLevel(int i) {
        return sortedDryToWet[MathHelper.clamp(i, 0, MAX_VALUE)];
    }

    public int getMoistureLevel() {
        return level;
    }
}
