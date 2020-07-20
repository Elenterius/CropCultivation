package com.creativechasm.environment.api.plant;

import com.creativechasm.environment.api.soil.SoilStateContext;
import com.creativechasm.environment.api.util.AgricultureUtil;
import net.minecraft.block.BlockState;
import net.minecraft.state.IntegerProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.PlantType;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

@ParametersAreNonnullByDefault
public interface ICrop {

    float getNitrogenNeed();

    float getPhosphorusNeed();

    float getPotassiumNeed();

    float getMinSoilPH();

    float getMaxSoilPH();

    float getMinSoilMoisture();

    float getMaxSoilMoisture();

    float getMinTemperature();

    float getMaxTemperature();


    IntegerProperty getCropAgeProperty();

    int getMaxCropAge();

    default int getCropAge(BlockState state) {
        return state.get(getCropAgeProperty());
    }

    default boolean isMaxCropAge(BlockState state) {
        return state.get(getCropAgeProperty()) >= getMaxCropAge();
    }


    static boolean canCropGrow(World world, BlockPos cropPos, BlockState cropState, ICrop iCrop, SoilStateContext soilContext) {
        //check soil pH
        if (soilContext.pH + 0.1f < iCrop.getMinSoilPH() || soilContext.pH - 0.1f > iCrop.getMaxSoilPH()) {
            return false; //don't grow outside the pH tolerance range
        }

        //check soil moisture
        if (soilContext.moisture < iCrop.getMinSoilMoisture() || soilContext.moisture > iCrop.getMaxSoilMoisture()) {
            return false; //don't grow outside the moisture tolerance range
        }

        //check air temperature
        float localTemperature = world.getBiome(cropPos).getTemperature(cropPos);
        if (localTemperature < iCrop.getMinTemperature() || localTemperature > iCrop.getMaxTemperature()) {
            return false; //don't grow outside the moisture tolerance range
        }

        //check for optimal neighborhood count
        if (iCrop instanceof IPlantGrowthCA) {
            int neighbors = ((IPlantGrowthCA) iCrop).countPlantNeighbors(world, cropPos);
            IPlantGrowthCA growthCA = (IPlantGrowthCA) iCrop;
            //noinspection RedundantIfStatement
            if (growthCA.isPlantLonely(neighbors) || growthCA.isPlantOvercrowded(neighbors)) {
                return false; //don't grow outside the moisture tolerance range
            }
        }

        return true;
    }

    static float getGrowthChance(ServerWorld world, BlockPos cropPos, BlockState cropState, ICrop iCrop, SoilStateContext soilContext) {
        //minimum nutrient requirement for plant to consider growing
        float reqN = iCrop.getNitrogenNeed() * soilContext.getMaxNutrientAmount();
        float reqP = iCrop.getPhosphorusNeed() * soilContext.getMaxNutrientAmount();
        float reqK = iCrop.getPotassiumNeed() * soilContext.getMaxNutrientAmount();

        float nPct = PlantMacronutrient.NITROGEN.getAvailabilityPctInSoilForPlant(soilContext.pH);
        float pPct = PlantMacronutrient.PHOSPHORUS.getAvailabilityPctInSoilForPlant(soilContext.pH);
        float kPct = PlantMacronutrient.POTASSIUM.getAvailabilityPctInSoilForPlant(soilContext.pH);
        //nutrients available in the soil depending on soil pH
        float currN = soilContext.nitrogen * nPct, currP = soilContext.phosphorus * pPct, currK = soilContext.potassium * kPct;

        return currN >= reqN && currP >= reqP && currK >= reqK ? AgricultureUtil.BASE_GROWTH_CHANCE * ((nPct + pPct + kPct) / 3f) : 0.0f;
    }

    static boolean canConsumeNutrient(Random rand, float nutrientNeed) {
        return rand.nextFloat() < nutrientNeed * 1.25f;
    }

    static void consumeSoilMoistureAndNutrients(ServerWorld world, BlockPos cropPos, BlockState cropState, ICrop iCrop, SoilStateContext soilContext) {
        int[] ages = AgricultureUtil.getCurrentAgeAndMaxAge(cropState);
        int currAge = ages[0], maxAge = ages[1];

        if (currAge < maxAge * (1f / 3f)) { //root growth phase
            if (canConsumeNutrient(world.rand, iCrop.getNitrogenNeed())) soilContext.nitrogen--;
            if (canConsumeNutrient(world.rand, iCrop.getPhosphorusNeed())) soilContext.phosphorus--;
        }
        else if (currAge < maxAge * (2f / 3f)) { //foliage growth phase
            if (canConsumeNutrient(world.rand, iCrop.getNitrogenNeed())) soilContext.nitrogen--;
            if (canConsumeNutrient(world.rand, iCrop.getPotassiumNeed())) soilContext.potassium--;
        }
        else if (currAge < maxAge) { //flower/fruit growth phase
            if (canConsumeNutrient(world.rand, iCrop.getPhosphorusNeed())) soilContext.phosphorus--;
        }
        else { //fallback, what crop has no age property? penalize the player for using "illegal" plant
            if (canConsumeNutrient(world.rand, iCrop.getNitrogenNeed())) soilContext.nitrogen -= 2;
            if (canConsumeNutrient(world.rand, iCrop.getPhosphorusNeed())) soilContext.phosphorus -= 2;
            if (canConsumeNutrient(world.rand, iCrop.getPotassiumNeed())) soilContext.potassium -= 1;
        }
        soilContext.moisture--;
    }

    void applyFertilizerBurn(ServerWorld worldIn, BlockPos pos, BlockState state);

    default PlantType getPlantType() {
        return PlantType.Crop;
    }

    /* removed in favor for Serene Season Mod */
    //boolean isGrowthSeason(Season currSeason);

    /**
     * @return true if the plant is healthy and can grow
     */
    boolean isHealthy(World world, BlockPos pos, BlockState state);

    /**
     * @return true when important nutrients are insufficient
     */
    boolean isWilting(World world, BlockPos pos, BlockState state);

    default boolean isMature(World world, BlockPos pos, BlockState state) {
        return isMaxCropAge(state);
    }

    /**
     * @return true if the plant can be "pollinated" (e.g. bees, wind, manual)
     */
    boolean isFlowering(World world, BlockPos pos, BlockState state);

    /**
     * @return true if the plant has fruit/seeds
     */
    default boolean hasFruit(World world, BlockPos pos, BlockState state) {
        return isMaxCropAge(state);
    }

    /**
     * fruit is ripe for "picking"
     */
    default boolean canBeHarvested(World world, BlockPos pos, BlockState state) {
        return isMature(world, pos, state) && hasFruit(world, pos, state);
    }

    /**
     * if the fruit can be raked from the individual branches using hand rakes or vibrating rakes (tools)
     */
    default boolean canBeRaked(World world, BlockPos pos, BlockState state) {
        return hasFruit(world, pos, state);
    }
}
