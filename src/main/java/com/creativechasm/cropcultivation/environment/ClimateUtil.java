package com.creativechasm.cropcultivation.environment;

import com.creativechasm.cropcultivation.CropCultivationMod;
import com.creativechasm.cropcultivation.environment.soil.IBlockTemperatureHandler;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

public class ClimateUtil {

    public static Marker LOG_MARKER = MarkerManager.getMarker("Climate");

    private static float TEMP_MIN = -0.5f;
    private static float TEMP_MAX = 2f;
    private static float TEMP_DIFF = TEMP_MAX - TEMP_MIN;

    public static void resetTemperatureScaler() {
        CropCultivationMod.LOGGER.debug(LOG_MARKER, "Determining temperature scale of Biomes...");
        ForgeRegistries.BIOMES.getValues().stream().map(Biome::getDefaultTemperature).forEach(f -> {
            if (f < TEMP_MIN) TEMP_MIN = f;
            else if (f > TEMP_MAX) TEMP_MAX = f;
        });
        TEMP_DIFF = TEMP_MAX - TEMP_MIN;
        CropCultivationMod.LOGGER.debug(LOG_MARKER, "Global Temperature Range: {" + TEMP_MIN + ", ... , " + TEMP_MAX + "}");
    }

    @SuppressWarnings("unused")
    public static void dumpBiomeTemperatureAndHumidity() {
        CropCultivationMod.LOGGER.info(LOG_MARKER, "dumping biome default temperatures to biome_temperatures.csv...");
        try {
            Files.write(Paths.get("biome_temperatures.csv"), (Iterable<String>) ForgeRegistries.BIOMES.getValues().stream().map(biome -> Objects.requireNonNull(biome.getRegistryName()).toString() + "," + biome.getDefaultTemperature() + "," + biome.getDownfall())::iterator);
        } catch (IOException e) {
            CropCultivationMod.LOGGER.error(LOG_MARKER, "Failed to dump biome temps!", e);
        }
    }

    /**
     * Rescales input temperature using min-max-scalar based on the default temperature of all registered biomes.<br>
     * Modded biomes with unusual temperatures will greatly effect the result.
     *
     * @param t temperature
     * @return temperature rescaled between 0.0 and 1.0 (inclusive)
     */
    public static float rescaleTemperature(float t) {
        return (t - TEMP_MIN) / TEMP_DIFF;
    }


    public static float convertTemperatureMCToCelsius(float mcTemp) {
        return 27.8f * mcTemp - 4.17f;
    }

    public static float convertTemperatureCelsiusToMC(float celsiusTemp) {
        return 0.036f * celsiusTemp + 0.15f;
//        float mcTemp = 0.036f * celsiusTemp + 0.15f;
//        return MathHelperX.roundTo2Decimals(mcTemp);
    }

    public static float getLocalTemperature(Biome biome, BlockPos pos, BlockState state) {
        float temperature = biome.getTemperature(pos);
        return state.getBlock() instanceof IBlockTemperatureHandler ? ((IBlockTemperatureHandler) state.getBlock()).getBlockTemperature(biome, temperature, pos, state) : temperature;
    }

    public static boolean isFreezingTemp(float temperature) {
        return temperature < 0.15F;
    }

    public static boolean isArid(Biome biome) {
        return biome.getDefaultTemperature() > 0.85F && biome.getDownfall() < 0.15f;
    }

    public static boolean isHighHumidity(float relativeHumidity) {
        return relativeHumidity > 0.85F;
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
