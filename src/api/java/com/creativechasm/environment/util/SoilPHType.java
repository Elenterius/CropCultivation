package com.creativechasm.environment.util;

import com.google.common.collect.Range;
import net.minecraft.util.math.MathHelper;

import java.util.Random;

//https://en.wikipedia.org/wiki/Soil_pH#Classification_of_soil_pH_ranges
public enum SoilPHType {
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

    public static final float MAXIMUM = 14f;
    public static final int MINIMUM = 0;
    private final Range<Float> pHRange;
    SoilPHType(Range<Float> pHRange) {
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
        else if (!pHRange.hasLowerBound()) return MathHelper.lerp(pct, MINIMUM, pHRange.upperEndpoint());
        else return MathHelper.lerp(pct, pHRange.lowerEndpoint(), MAXIMUM);
    }

    public float randomPH(Random rand) {
        return getPH(rand.nextFloat());
    }

    protected float randomPHAffectedByTemperature(Random rand, float localTemperature) {
        boolean pickHigherValues = ClimateUtil.rescaleTemperature(localTemperature) < 0.5f;
        if (pHRange.hasLowerBound() && pHRange.hasUpperBound()) {
            float midpoint = MathHelper.lerp(0.5f, pHRange.lowerEndpoint(), pHRange.upperEndpoint());
            if (pickHigherValues) return MathHelper.nextFloat(rand, midpoint, pHRange.upperEndpoint());
            else return MathHelper.nextFloat(rand, pHRange.lowerEndpoint(), midpoint);
        }
        else if (!pHRange.hasLowerBound()) {
            if (pickHigherValues) return MathHelper.nextFloat(rand, pHRange.upperEndpoint() * 0.5f, pHRange.upperEndpoint());
            else return MathHelper.nextFloat(rand, MINIMUM, pHRange.upperEndpoint() * 0.5f);
        }
        else {
            if (pickHigherValues) return MathHelper.nextFloat(rand, 11.5f, MAXIMUM);
            else return MathHelper.nextFloat(rand, pHRange.lowerEndpoint(), 11.5f);
        }
    }

    public static float roundPH(float pH) {
        return Math.round(pH * 10f) / 10f;
    }

    public static SoilPHType fromPH(float pH) {
        pH = roundPH(pH);
        for (SoilPHType phType : values()) {
            if (phType.pHRange.contains(pH)) return phType;
        }
        return NEUTRAL;
    }
}