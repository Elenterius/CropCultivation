package com.creativechasm.cropcultivation.environment.soil;

public enum SoilDrainage {
    NOT_DRAINED(0f),
    VERY_POORLY_DRAINED(1/6f),
    POORLY_DRAINED(2/6f),
    IMPERFECTLY_DRAINED(3/6f),
    MODERATELY_WELL_DRAINED(4/6f),
    WELL_DRAINED(5/6f),
    RAPIDLY_DRAINED(1f);

    private final float drainMultiplier;

    SoilDrainage(float drainMultiplier) {
        this.drainMultiplier = drainMultiplier;
    }

    public float getMultiplier() { return drainMultiplier; }
}
