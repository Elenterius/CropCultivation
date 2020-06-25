package com.creativechasm.blightbiome.common.block.plant;

import net.minecraft.world.biome.Biome;

import javax.annotation.Nullable;

public class PlantHabitat {
    float temperature;
    Biome.TempCategory tempCategory;
    Biome.Category biomeCategory;
    int skyColor;
    int waterColor;
    float rainfall;
    Biome.RainType precipitation;

    int minElevation = 0;
    int maxElevation = 255;

    boolean isCompatibleWithBiome(Biome biome) {
        return tempCategory == biome.getTempCategory();
    }

    public static class Builder {
        @Nullable
        private Float temperature;

        @Nullable
        private Biome.RainType precipitation;

        @Nullable
        private Float downfall;

        public Builder precipitation(Biome.RainType precipitationIn) {
            precipitation = precipitationIn;
            return this;
        }

        public Builder temperature(float temperatureIn) {
            temperature = temperatureIn;
            return this;
        }

        public Builder downfall(float downfallIn) {
            downfall = downfallIn;
            return this;
        }
    }
}
