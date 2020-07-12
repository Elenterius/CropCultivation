package com.creativechasm.environment.api.soil;

import com.creativechasm.environment.api.util.MathHelperX;
import com.creativechasm.environment.api.world.ClimateUtil;
import com.google.common.collect.Range;
import net.minecraft.util.math.MathHelper;

import java.util.Random;

//https://en.wikipedia.org/wiki/Soil_pH#Classification_of_soil_pH_ranges
public enum SoilPH {
    ULTRA_ACIDIC(Range.lessThan(3.5f)),
    EXTREMELY_ACIDIC(Range.closed(3.5f, 4.4f)),
    VERY_STRONGLY_ACIDIC(Range.closed(4.5f, 5.0f)),
    STRONGLY_ACIDIC(Range.closed(5.1f, 5.5f)),
    MODERATELY_ACIDIC(Range.closed(5.6f, 6.0f)),
    SLIGHTLY_ACIDIC(Range.closed(6.1f, 6.5f)),
    NEUTRAL(Range.closed(6.6f, 7.3f)),
    SLIGHTLY_ALKALINE(Range.closed(7.4f, 7.8f)),
    MODERATELY_ALKALINE(Range.closed(7.9f, 8.4f)),
    STRONGLY_ALKALINE(Range.closed(8.5f, 9.0f)),
    VERY_STRONGLY_ALKALINE(Range.greaterThan(9.0f));

    public static final int MAX_VALUE = 14;
    public static final int MIN_VALUE = 0;
    private final Range<Float> pHRange;
    SoilPH(Range<Float> pHRange) {
        this.pHRange = pHRange;
    }

    public Range<Float> getPHRange() {
        return pHRange;
    }

    public boolean containsPH(float pH) {
        pH = roundPH(pH);
        return pHRange.contains(pH);
    }

    public float getPH(float pct) {
        if (pHRange.hasLowerBound() && pHRange.hasUpperBound()) {
            return MathHelper.lerp(pct, pHRange.lowerEndpoint(), pHRange.upperEndpoint());
        }
        else if (!pHRange.hasLowerBound()) return MathHelper.lerp(pct, MIN_VALUE, pHRange.upperEndpoint());
        else return MathHelper.lerp(pct, pHRange.lowerEndpoint(), MAX_VALUE);
    }

    public float randomPH(Random rand) {
        return getPH(rand.nextFloat());
    }

    public float randomPHAffectedByTemperature(Random rand, float localTemperature) {
        boolean pickHigherValues = ClimateUtil.rescaleTemperature(localTemperature) < 0.5f;
        if (pHRange.hasLowerBound() && pHRange.hasUpperBound()) {
            float midpoint = MathHelper.lerp(0.5f, pHRange.lowerEndpoint(), pHRange.upperEndpoint());
            if (pickHigherValues) return MathHelper.nextFloat(rand, midpoint, pHRange.upperEndpoint());
            else return MathHelper.nextFloat(rand, pHRange.lowerEndpoint(), midpoint);
        }
        else if (!pHRange.hasLowerBound()) {
            if (pickHigherValues) return MathHelper.nextFloat(rand, pHRange.upperEndpoint() * 0.5f, pHRange.upperEndpoint());
            else return MathHelper.nextFloat(rand, MIN_VALUE, pHRange.upperEndpoint() * 0.5f);
        }
        else {
            if (pickHigherValues) return MathHelper.nextFloat(rand, 11.5f, MAX_VALUE);
            else return MathHelper.nextFloat(rand, pHRange.lowerEndpoint(), 11.5f);
        }
    }

    public static float roundPH(float pH) {
        return MathHelperX.roundTo1Decimal(pH);
    }

    public static SoilPH fromPH(float pH) {
        pH = roundPH(pH);
        for (SoilPH phType : values()) {
            if (phType.pHRange.contains(pH)) return phType;
        }
        return NEUTRAL;
    }
}