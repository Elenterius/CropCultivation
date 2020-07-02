package com.creativechasm.blightbiome.common.util;

import net.minecraft.block.BlockState;
import net.minecraft.block.CropsBlock;
import net.minecraft.state.IProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.FarmlandWaterManager;
import net.minecraftforge.registries.ForgeRegistries;

public class NatureUtil {

    private static float normMIN = -0.5f;
    private static float normMAX = 2f;
    private static float normDiff = normMAX - normMIN;

    public static void initTemperatureNormalizer() {
        ForgeRegistries.BIOMES.getValues().stream().map(Biome::getDefaultTemperature).forEach(f -> {
            if (f < normMIN) normMIN = f;
            else if (f > normMAX) normMAX = f;
        });
        normDiff = normMAX - normMIN;
    }

    /**
     * Rescales input temperature using min-max-scalar based on the default temperature of all registered biomes.<br>
     * Modded biomes with unusual temperatures will greatly effect the result.
     * @param t temperature
     * @return temperature rescaled between 0.0 and 1.0 (inclusive)
     */
    public static float rescaleTemperature(float t) {
        return (t - normMIN) / normDiff;
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

    public static boolean doesSoilHaveWater(IWorldReader worldIn, BlockPos pos, int distance) {
        for (BlockPos blockpos : BlockPos.getAllInBoxMutable(pos.add(-distance, 0, -distance), pos.add(distance, 1, distance))) {
            if (worldIn.getFluidState(blockpos).isTagged(FluidTags.WATER)) return true;
        }
        return FarmlandWaterManager.hasBlockWaterTicket(worldIn, pos);
    }

    public static float calculateWaterInfiltrationScoreForSoil(IWorldReader worldIn, BlockPos pos, int distance) {
        float score = 0f;

        for (BlockPos blockpos : BlockPos.getAllInBoxMutable(pos.add(-distance, 0, -distance), pos.add(distance, 1, distance))) {
            if (worldIn.getFluidState(blockpos).isTagged(FluidTags.WATER)) {
                score += (distance - blockpos.manhattanDistance(pos) + 0.5f) / distance;
            }
        }
        if (FarmlandWaterManager.hasBlockWaterTicket(worldIn, pos)) score += 1f;

        float n = (distance + distance) * (distance + distance) - 1;
        return score / n;
    }

    public static int[] getCurrentAgeAndMaxAge(BlockState state) {
        if (state.getBlock() instanceof CropsBlock) {
            CropsBlock block = (CropsBlock) state.getBlock();
            return new int[]{state.get(block.getAgeProperty()), block.getMaxAge()};
        }
        for (IProperty<?> prop : state.getProperties()) {
            if (prop.getName().equals("age") && prop instanceof IntegerProperty) {
                IntegerProperty age = (IntegerProperty) prop;
                return new int[]{state.get(age), age.getAllowedValues().stream().max(Integer::compareTo).orElse(0)};
            }
        }
        return new int[]{0,0};
    }

}
