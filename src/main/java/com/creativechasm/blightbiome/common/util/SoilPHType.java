package com.creativechasm.blightbiome.common.util;

import com.google.common.collect.Range;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.server.ServerWorld;

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

    private final Range<Float> pHRange;
    SoilPHType(Range<Float> pHRange) {
        this.pHRange = pHRange;
    }

    public Range<Float> getPHRange() {
        return pHRange;
    }

    public boolean containsPH(float pH) {
        pH = (Math.round(pH * 10f) / 10f);
        return pHRange.contains(pH);
    }

    public float getRandomPH(Random rand) {
        if (pHRange.hasLowerBound() && pHRange.hasUpperBound()) {
            return MathHelper.nextFloat(rand, pHRange.lowerEndpoint(), pHRange.upperEndpoint());
        }
        else if (!pHRange.hasLowerBound()) return MathHelper.nextFloat(rand, 0, pHRange.upperEndpoint());
        else return MathHelper.nextFloat(rand, pHRange.lowerEndpoint(), 14f);
    }

    protected float getRandomPHAffectedByTemperature(Random rand, float localTemperature) {
        boolean pickHigherValues = NatureUtil.rescaleTemperature(localTemperature) < 0.5f;
        if (pHRange.hasLowerBound() && pHRange.hasUpperBound()) {
            float midpoint = MathHelper.lerp(0.5f, pHRange.lowerEndpoint(), pHRange.upperEndpoint());
            if (pickHigherValues) return MathHelper.nextFloat(rand, midpoint, pHRange.upperEndpoint());
            else return MathHelper.nextFloat(rand, pHRange.lowerEndpoint(), midpoint);
        }
        else if (!pHRange.hasLowerBound()) {
            if (pickHigherValues) return MathHelper.nextFloat(rand, pHRange.upperEndpoint() * 0.5f, pHRange.upperEndpoint());
            else return MathHelper.nextFloat(rand, 0, pHRange.upperEndpoint() * 0.5f);
        }
        else {
            if (pickHigherValues) return MathHelper.nextFloat(rand, 11.5f, 14f);
            else return MathHelper.nextFloat(rand, pHRange.lowerEndpoint(), 11.5f);
        }
    }

    public static SoilPHType fromPHValue(float pH) {
        pH = (Math.round(pH * 10f) / 10f);
        for (SoilPHType phType : values()) {
            if (phType.pHRange.contains(pH)) return phType;
        }
        return NEUTRAL;
    }

    public static float getPHForBlockInWorld(ServerWorld world, BlockPos pos, BlockState state) {
        float temperature = world.getBiome(pos).getTemperature(pos);
        if (state.getBlock() == Blocks.PODZOL) return STRONGLY_ACIDIC.getRandomPHAffectedByTemperature(world.rand, temperature);
        if (state.getBlock() == Blocks.CLAY) return SLIGHTLY_ALKALINE.getRandomPHAffectedByTemperature(world.rand, temperature);
        if (state.getBlock() == Blocks.SAND) return NEUTRAL.getRandomPHAffectedByTemperature(world.rand, temperature);
        if (state.getBlock() == Blocks.DIRT) return SLIGHTLY_ACIDIC.getRandomPHAffectedByTemperature(world.rand, temperature);
        return NEUTRAL.getRandomPHAffectedByTemperature(world.rand, temperature);
    }
}