package com.creativechasm.cropcultivation.api.plant;

import com.creativechasm.cropcultivation.api.block.BlockPropertyUtil;
import com.creativechasm.cropcultivation.api.soil.SoilMoisture;
import com.creativechasm.cropcultivation.api.soil.SoilStateContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.IGrowable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Optional;
import java.util.Random;

public abstract class CropUtil
{
    public static float BASE_GROWTH_CHANCE = 0.4f; //0.33f
    public static float BASE_YIELD_MULTIPLIER = 1.65f;

    public static boolean grow(ServerWorld world, Random rand, BlockPos pos, BlockState state) {
        if (state.getBlock() instanceof IGrowable) {
            IGrowable iGrowable = (IGrowable) state.getBlock();
            iGrowable.grow(world, rand, pos, state);
            return true;
        }
        return false;
    }

    public static abstract class RegisteredCrop
    {
        public static boolean canCropGrow(World world, BlockPos cropPos, BlockState cropState, ICropEntry iCrop, SoilStateContext soilContext) {
            //check soil pH
            if (soilContext.pH + 0.1f < iCrop.getMinSoilPH() || soilContext.pH - 0.1f > iCrop.getMaxSoilPH()) {
                return false; //don't grow outside the pH tolerance range
            }

            //check soil moisture
            if (soilContext.moisture < iCrop.getMinSoilMoisture() * SoilMoisture.MAX_VALUE || soilContext.moisture > iCrop.getMaxSoilMoisture() * SoilMoisture.MAX_VALUE) {
                return false; //don't grow outside the moisture tolerance range
            }

            //check air temperature
            float localTemperature = world.getBiome(cropPos).getTemperature(cropPos);
            if (localTemperature < iCrop.getMinTemperature() || localTemperature > iCrop.getMaxTemperature()) {
                return false; //don't grow outside the temperature tolerance range
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

        public static float getGrowthChance(ServerWorld world, BlockPos cropPos, BlockState cropState, ICropEntry iCrop, SoilStateContext soilContext) {
            //minimum nutrient requirement for plant to consider growing
            float reqN = iCrop.getNitrogenNeed() * soilContext.getMaxNutrientAmount();
            float reqP = iCrop.getPhosphorusNeed() * soilContext.getMaxNutrientAmount();
            float reqK = iCrop.getPotassiumNeed() * soilContext.getMaxNutrientAmount();

            float nPct = PlantMacronutrient.NITROGEN.getAvailabilityPctInSoilForPlant(soilContext.pH);
            float pPct = PlantMacronutrient.PHOSPHORUS.getAvailabilityPctInSoilForPlant(soilContext.pH);
            float kPct = PlantMacronutrient.POTASSIUM.getAvailabilityPctInSoilForPlant(soilContext.pH);
            //nutrients available in the soil depending on soil pH
            float currN = soilContext.nitrogen * nPct, currP = soilContext.phosphorus * pPct, currK = soilContext.potassium * kPct;

            return currN >= reqN && currP >= reqP && currK >= reqK ? BASE_GROWTH_CHANCE * ((nPct + pPct + kPct) / 3f) : 0.0f;
        }

        public static void updateYield(ServerWorld world, BlockPos cropPos, BlockState prevCropState, BlockState newCropState, ICropEntry iCrop, SoilStateContext soilContext) {
            int prevCropAge = BlockPropertyUtil.getAge(prevCropState);
            int newCropAge = BlockPropertyUtil.getAge(newCropState);

            if (prevCropAge <= 0 || newCropAge <= 0) {
                soilContext.getTileState().resetCropYield();
            }

            if (newCropAge > 0) {
                //get yield based on PK concentration in soil
                float currP = soilContext.phosphorus * PlantMacronutrient.PHOSPHORUS.getAvailabilityPctInSoilForPlant(soilContext.pH);
                float currK = soilContext.potassium * PlantMacronutrient.POTASSIUM.getAvailabilityPctInSoilForPlant(soilContext.pH);
                float yieldModifier = (currP / soilContext.getMaxNutrientAmount() + currK / soilContext.getMaxNutrientAmount()) * 0.5f * BASE_YIELD_MULTIPLIER;

                //the crop might have advanced several ages at once
                int ageDiff = newCropAge - prevCropAge; //age difference can be negative when the crop age was decreased --> newCropAge < prevCropAge
                yieldModifier *= ageDiff; //scale yieldModifier

                soilContext.getTileState().addCropYield(yieldModifier); //increase/decrease yield
            }
        }

        public static boolean canConsumeNutrient(Random rand, float nutrientNeed) {
            return rand.nextFloat() < nutrientNeed * 1.25f;
        }

        public static void consumeSoilNutrients(ServerWorld world, BlockPos cropPos, BlockState prevCropState, BlockState cropState, ICropEntry iCrop, SoilStateContext soilContext) {
            Optional<int[]> optional = BlockPropertyUtil.getCurrentAgeAndMaxAge(cropState);
            if (optional.isPresent()) {
                int currAge = optional.get()[0], maxAge = optional.get()[1];

                if (currAge < maxAge * (1f / 3f)) { //root growth phase
                    if (canConsumeNutrient(world.rand, iCrop.getNitrogenNeed())) soilContext.nitrogen--;
                    if (canConsumeNutrient(world.rand, iCrop.getPhosphorusNeed())) soilContext.phosphorus--;
                }
                else if (currAge < maxAge * (2f / 3f)) { //foliage growth phase
                    if (canConsumeNutrient(world.rand, iCrop.getNitrogenNeed())) soilContext.nitrogen--;
                    if (canConsumeNutrient(world.rand, iCrop.getPotassiumNeed())) soilContext.potassium--;
                }
                else { //flower/fruit growth phase
                    if (canConsumeNutrient(world.rand, iCrop.getPhosphorusNeed())) soilContext.phosphorus--;
                }
            }
            else { //fallback, what crop has no age property? penalize the player for using an "illegal" plant
                if (canConsumeNutrient(world.rand, iCrop.getNitrogenNeed())) soilContext.nitrogen -= 2;
                if (canConsumeNutrient(world.rand, iCrop.getPhosphorusNeed())) soilContext.phosphorus -= 2;
                if (canConsumeNutrient(world.rand, iCrop.getPotassiumNeed())) soilContext.potassium -= 1;
            }
        }

        public static void consumeSoilMoisture(BlockPos cropPos, BlockState cropState, SoilStateContext soilContext) {
            soilContext.moisture--;
        }
    }

    public static abstract class GenericCrop
    {
        public static void updateYield(int prevCropAge, int newCropAge, SoilStateContext soilContext) {
            if (prevCropAge <= 0 || newCropAge <= 0) {
                soilContext.getTileState().resetCropYield();
            }

            if (newCropAge > 0) {
                //get yield based on PK concentration in soil
                float currP = soilContext.phosphorus * PlantMacronutrient.PHOSPHORUS.getAvailabilityPctInSoilForPlant(soilContext.pH);
                float currK = soilContext.potassium * PlantMacronutrient.POTASSIUM.getAvailabilityPctInSoilForPlant(soilContext.pH);
                float yieldModifier = (currP / soilContext.getMaxNutrientAmount() + currK / soilContext.getMaxNutrientAmount()) * 0.5f * BASE_YIELD_MULTIPLIER;

                //the crop might have advanced several ages at once
                int ageDiff = newCropAge - prevCropAge; //age difference can be negative when the crop age was decreased --> newCropAge < prevCropAge
                yieldModifier *= ageDiff; //scale yieldModifier

                soilContext.getTileState().addCropYield(yieldModifier); //increase/decrease yield
            }
        }

        public static void consumeSoilNutrients(Random rand, int cropAge, int maxAge, SoilStateContext soilContext) {
            if (cropAge < maxAge * (1f / 3f)) { //root growth phase
                if (rand.nextFloat() < 0.25f) soilContext.phosphorus--;
                if (rand.nextFloat() < 0.25f) soilContext.nitrogen--;
            }
            else if (cropAge < maxAge * (2f / 3f)) { //foliage growth phase
                if (rand.nextFloat() < 0.25f) soilContext.nitrogen--;
                if (rand.nextFloat() < 0.25f) soilContext.potassium--;
            }
            else if (cropAge <= maxAge) { //flower/fruit growth phase
                if (rand.nextFloat() < 0.25f) soilContext.phosphorus--;
            }
        }

        public static void consumeSoilMoisture(BlockPos cropPos, BlockState cropState, SoilStateContext soilContext) {
            soilContext.moisture--;
        }
    }
}
