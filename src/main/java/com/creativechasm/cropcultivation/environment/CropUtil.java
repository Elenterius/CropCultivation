package com.creativechasm.cropcultivation.environment;

import com.creativechasm.cropcultivation.environment.plant.IPlantGrowthCA;
import com.creativechasm.cropcultivation.environment.plant.PlantMacronutrient;
import com.creativechasm.cropcultivation.environment.soil.SoilMoisture;
import com.creativechasm.cropcultivation.environment.soil.SoilStateContext;
import com.creativechasm.cropcultivation.registry.DefaultCropEntry;
import com.creativechasm.cropcultivation.registry.ICropEntry;
import com.creativechasm.cropcultivation.util.MathHelperX;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;

import java.util.Random;

public abstract class CropUtil
{
    public static float BASE_GROWTH_CHANCE = 0.4f; //0.33f
    public static float BASE_YIELD_MULTIPLIER = 1.65f;
    public static final ICropEntry GENERIC_CROP = new DefaultCropEntry("generic", 0.2f, 0.1f, 0.1f, 5.5f, 7.5f, 0.5f, 0.7f, 10f, 22f);

//    public static boolean grow(ServerWorld world, Random rand, BlockPos pos, BlockState state) {
//        if (state.getBlock() instanceof IGrowable) {
//            IGrowable iGrowable = (IGrowable) state.getBlock();
//            iGrowable.grow(world, rand, pos, state);
//            return true;
//        }
//        return false;
//    }

    public static void consumeSoilMoisture(BlockPos cropPos, BlockState cropState, SoilStateContext soilContext) {
        int consumption = 1;
        if (cropState.getBlock() instanceof IPlantable) {
            PlantType type = ((IPlantable) cropState.getBlock()).getPlantType(soilContext.getWorld(), cropPos);
            if (ClimateUtil.isArid(soilContext.getWorld().getBiome(cropPos)) && type != PlantType.Desert || type != PlantType.Nether)
                consumption++;
        }
        soilContext.moisture -= consumption;
    }

    public static void updateYield(int prevCropAge, int newCropAge, SoilStateContext soilContext) {
        if (prevCropAge <= 0 || newCropAge <= 0) {
            soilContext.getTileState().resetCropYield();
        }

        if (newCropAge > 0) {
            //get yield based on PK concentration in soil
            float currP = soilContext.phosphorus * PlantMacronutrient.PHOSPHORUS.getAvailabilityPctInSoil(soilContext.pH);
            float currK = soilContext.potassium * PlantMacronutrient.POTASSIUM.getAvailabilityPctInSoil(soilContext.pH);
            float yieldModifier = (currP / soilContext.getMaxNutrientAmount() + currK / soilContext.getMaxNutrientAmount()) * 0.5f * BASE_YIELD_MULTIPLIER;

            //the crop might have advanced several ages at once
            int ageDiff = newCropAge - prevCropAge; //age difference can be negative when the crop age was decreased --> newCropAge < prevCropAge
            yieldModifier *= ageDiff; //scale yieldModifier

            soilContext.getTileState().addCropYield(yieldModifier); //increase/decrease yield
        }
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

            //check temperature
            float localTemperature = ClimateUtil.getLocalTemperature(world.getBiome(cropPos), cropPos, cropState);
            float soilTemperature = ClimateUtil.getLocalTemperature(world.getBiome(soilContext.getBlockPos()), soilContext.getBlockPos(), soilContext.getBlockState());
            float temperature = MathHelperX.lerp(0.7f, localTemperature, soilTemperature);
            if (temperature < iCrop.getMinTemperature() || temperature > iCrop.getMaxTemperature()) {
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

        public static float getGrowthChance(ICropEntry iCrop, SoilStateContext soilContext) {
            //minimum nutrient requirement for plant to consider growing
            float reqN = iCrop.getNitrogenNeed() * soilContext.getMaxNutrientAmount();
            float reqP = iCrop.getPhosphorusNeed() * soilContext.getMaxNutrientAmount();
            float reqK = iCrop.getPotassiumNeed() * soilContext.getMaxNutrientAmount();

            float nPct = PlantMacronutrient.NITROGEN.getAvailabilityPctInSoil(soilContext.pH);
            float pPct = PlantMacronutrient.PHOSPHORUS.getAvailabilityPctInSoil(soilContext.pH);
            float kPct = PlantMacronutrient.POTASSIUM.getAvailabilityPctInSoil(soilContext.pH);
            //nutrients available in the soil depending on soil pH
            float currN = soilContext.nitrogen * nPct, currP = soilContext.phosphorus * pPct, currK = soilContext.potassium * kPct;

            return currN >= reqN && currP >= reqP && currK >= reqK ? BASE_GROWTH_CHANCE * ((nPct + pPct + kPct) / 3f) : 0.0f;
        }

        public static void updateYield(int prevCropAge, int newCropAge, SoilStateContext soilContext) {
            CropUtil.updateYield(prevCropAge, newCropAge, soilContext);
        }

        public static boolean canConsumeNutrient(Random rand, float nutrientNeed) {
            return rand.nextFloat() < nutrientNeed * 1.25f;
        }

        public static void consumeSoilNutrients(Random rand, int cropAge, int maxAge, ICropEntry iCrop, SoilStateContext soilContext) {
                if (cropAge < maxAge * (1f / 3f)) { //root growth phase
                    if (canConsumeNutrient(rand, iCrop.getNitrogenNeed())) soilContext.nitrogen--;
                    if (canConsumeNutrient(rand, iCrop.getPhosphorusNeed())) soilContext.phosphorus--;
                }
                else if (cropAge < maxAge * (2f / 3f)) { //foliage growth phase
                    if (canConsumeNutrient(rand, iCrop.getNitrogenNeed())) soilContext.nitrogen--;
                    if (canConsumeNutrient(rand, iCrop.getPotassiumNeed())) soilContext.potassium--;
                }
                else { //flower/fruit growth phase
                    if (canConsumeNutrient(rand, iCrop.getPhosphorusNeed())) soilContext.phosphorus--;
                }
        }

        public static void consumeSoilMoisture(BlockPos cropPos, BlockState cropState, SoilStateContext soilContext) {
            CropUtil.consumeSoilMoisture(cropPos, cropState, soilContext);
        }
    }

    public static abstract class FallbackCrop
    {
        public static void updateYield(int prevCropAge, int newCropAge, SoilStateContext soilContext) {
            CropUtil.updateYield(prevCropAge, newCropAge, soilContext);
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
            CropUtil.consumeSoilMoisture(cropPos, cropState, soilContext);
        }
    }
}
