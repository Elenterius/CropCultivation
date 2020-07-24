package com.creativechasm.cropcultivation.api.world;

public enum SunLight {
    FULL_SUNSHINE(1f),
    PARTIAL_SHADE(2f/3f),
    FULL_SHADE(1f/3f),
    NONE(0f);

    private final float brightness;

    SunLight(float brightness) {
        this.brightness = brightness;
    }

    public float getBrightness() {
        return brightness;
    }

    public static SunLight fromBrightness(float brightness) {
        if (brightness > PARTIAL_SHADE.brightness) return FULL_SUNSHINE;
        if (brightness > FULL_SHADE.brightness) return PARTIAL_SHADE;
        if (brightness > NONE.brightness) return FULL_SHADE;
        return NONE;
    }
}
