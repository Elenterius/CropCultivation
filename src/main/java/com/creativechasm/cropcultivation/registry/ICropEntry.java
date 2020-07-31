package com.creativechasm.cropcultivation.registry;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface ICropEntry
{
    float getNitrogenNeed();

    float getPhosphorusNeed();

    float getPotassiumNeed();

    float getMinSoilPH();

    float getMaxSoilPH();

    float getMinSoilMoisture();

    float getMaxSoilMoisture();

    float getMinTemperature();

    float getMaxTemperature();

//    IntegerProperty getCropAgeProperty();
//
//    int getMaxCropAge();
//
//    default int getCropAge(BlockState state) {
//        return state.get(getCropAgeProperty());
//    }
//
//    default boolean isMaxCropAge(BlockState state) {
//        return state.get(getCropAgeProperty()) >= getMaxCropAge();
//    }

//    void applyFertilizerBurn(ServerWorld worldIn, BlockPos pos, BlockState state);

//    default PlantType getPlantType() {
//        return PlantType.Crop;
//    }

    /* removed in favor for Serene Season Mod */
    //boolean isGrowthSeason(Season currSeason);

    /**
     * @return true if the plant is healthy and can grow
     */
//    boolean isHealthy(World world, BlockPos pos, BlockState state);

    /**
     * @return true when important nutrients are insufficient
     */
//    boolean isWilting(World world, BlockPos pos, BlockState state);

//    default boolean isMature(World world, BlockPos pos, BlockState state) {
//        return isMaxCropAge(state);
//    }

    /**
     * @return true if the plant can be "pollinated" (e.g. bees, wind, manual)
     */
//    boolean isFlowering(World world, BlockPos pos, BlockState state);

    /**
     * @return true if the plant has fruit/seeds
     */
//    default boolean hasFruit(World world, BlockPos pos, BlockState state) {
//        return isMaxCropAge(state);
//    }

    /**
     * fruit is ripe for "picking"
     */
//    default boolean canBeHarvested(World world, BlockPos pos, BlockState state) {
//        return isMature(world, pos, state) && hasFruit(world, pos, state);
//    }

    /**
     * if the fruit can be raked from the individual branches using hand rakes or vibrating rakes (tools)
     */
//    default boolean canBeRaked(World world, BlockPos pos, BlockState state) {
//        return hasFruit(world, pos, state);
//    }
}
