package com.creativechasm.environment.util;

import com.creativechasm.environment.EnvironmentLib;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class ClimateUtil {

    public static Marker LOG_MARKER = MarkerManager.getMarker("Climate");

    private static float TEMP_MIN = 2f;
    private static float TEMP_MAX = -0.5f;
    private static float TEMP_DIFF;

    public static void determineTemperatureScale() {

        EnvironmentLib.LOGGER.debug(LOG_MARKER, "Determining temperature scale of Biomes...");
        ForgeRegistries.BIOMES.getValues().stream().map(Biome::getDefaultTemperature).forEach(f -> {
            if (f < TEMP_MIN) TEMP_MIN = f;
            else if (f > TEMP_MAX) TEMP_MAX = f;
        });
        TEMP_DIFF = TEMP_MAX - TEMP_MIN;
        EnvironmentLib.LOGGER.debug(LOG_MARKER, "Global Temperature Range: {" + TEMP_MIN + ", ... , " + TEMP_MAX + "}");
    }

    /**
     * Rescales input temperature using min-max-scalar based on the default temperature of all registered biomes.<br>
     * Modded biomes with unusual temperatures will greatly effect the result.
     * @param t temperature
     * @return temperature rescaled between 0.0 and 1.0 (inclusive)
     */
    public static float rescaleTemperature(float t) {
        return (t - TEMP_MIN) / TEMP_DIFF;
    }

    public static boolean isHighHumidity(float relativeHumidity) {
        return relativeHumidity > 0.85F;
    }

    public static boolean isFreezingTemp(float temperature) {
        return temperature < 0.15F;
    }

    public static boolean isArid(Biome biome) {
        return biome.getDefaultTemperature() > 0.85F && biome.getDownfall() < 0.15f;
    }

    public static float calcDewPointTemperature(float temperature, float relativeHumidity) {
        return temperature - (1f - relativeHumidity) / 5f; //rough approximation
    }

    public static float calcRelativeHumidity(float temperature, float dewPointTemperature) {
        return 1f - 5 * (temperature - dewPointTemperature); //rough approximation
    }

    public static float getDewPointTemperature(Biome biome) {
        return calcDewPointTemperature(biome.getDefaultTemperature(), biome.getDownfall());
    }

    public static float getLocalHumidityAt(World world, BlockPos pos) {
        Biome biome = world.getBiome(pos);
        return calcRelativeHumidity(biome.getTemperature(pos), getDewPointTemperature(biome));
    }

    public static boolean doesWaterEvaporate(float biomeTemperature, float biomeHumidity) {
        //normally we would also consider wind speed irl
        return biomeTemperature > 0.85f && biomeHumidity <= 0.85f;
    }

    public static boolean doesWaterVaporCondenseIntoDew(float localTemperature, float localHumidity) {
        return !isFreezingTemp(localTemperature) && localHumidity >= 1f;
    }

    public static boolean doesWaterVaporCondenseIntoFrost(float localTemperature, float localHumidity) {
        return isFreezingTemp(localTemperature) && localHumidity >= 1f;
    }

    enum Condensate {
        NONE,
        DEW,
        FROST
    }

    public static Condensate condenseWaterVapor(World world, BlockPos pos) {
        Biome biome = world.getBiome(pos);
        float localTemperature = biome.getTemperature(pos);
        float localHumidity = calcRelativeHumidity(localTemperature, getDewPointTemperature(biome));
        if (localHumidity >= 1f) {
            if (localTemperature < 0.15f && world.getLightFor(LightType.BLOCK, pos) < 10) { //TODO: "coldest time" is at sunrise
                return Condensate.FROST;
            } else {
                return Condensate.DEW;
            }
        }

        return Condensate.NONE;
    }

}
