package com.creativechasm.environment.api.world;

import net.minecraft.world.World;
import net.minecraft.world.dimension.Dimension;

public enum MoonPhase {
    NEW_MOON(4, 0f),
    WAXING_CRESCENT(5, 0.25f),
    FIRST_QUARTER(6, 0.5f),
    WAXING_GIBBOUS(7, 0.75f),
    FULL_MOON(0, 1f),
    WANING_GIBBOUS(1, 0.75f),
    THIRD_QUARTER(2, 0.5f),
    WANING_CRESCENT(3, 0.25f);

    private final int phase;
    private static final MoonPhase[] phases = new MoonPhase[]{FULL_MOON, WANING_GIBBOUS, THIRD_QUARTER, WANING_CRESCENT, NEW_MOON, WAXING_CRESCENT, FIRST_QUARTER, WAXING_GIBBOUS};

    MoonPhase(int phase, float fullness) {
        this.phase = phase;
    }

    public int getPhase() {
        return phase;
    }

    public float getMoonFullness() {
        return Dimension.MOON_PHASE_FACTORS[phase];
    }

    public static MoonPhase fromPhase(int i) {
        return phases[i];
    }

    public static MoonPhase from(World world) {
        return phases[world.getDimension().getMoonPhase(world.getDayTime())];
    }
}
