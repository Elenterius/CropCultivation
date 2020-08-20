package com.creativechasm.cropcultivation.environment;

import com.creativechasm.cropcultivation.CropCultivationConfig;
import com.creativechasm.cropcultivation.block.WeedBlock;
import com.creativechasm.cropcultivation.environment.plant.IPlantGrowthCA;
import com.creativechasm.cropcultivation.environment.plant.PlantMacronutrient;
import com.creativechasm.cropcultivation.environment.plant.WeedType;
import com.creativechasm.cropcultivation.environment.soil.SoilMoisture;
import com.creativechasm.cropcultivation.environment.soil.SoilStateContext;
import com.creativechasm.cropcultivation.init.CommonRegistry;
import com.creativechasm.cropcultivation.init.ModBlocks;
import com.creativechasm.cropcultivation.registry.DefaultCropEntry;
import com.creativechasm.cropcultivation.registry.ICropEntry;
import com.creativechasm.cropcultivation.util.BlockPropertyUtil;
import com.creativechasm.cropcultivation.util.MathHelperX;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;

import java.util.List;
import java.util.Random;

public abstract class CropUtil
{
    public static final ICropEntry GENERIC_CROP = new DefaultCropEntry("generic", 0.2f, 0.1f, 0.1f, 5.5f, 7.5f, 0.5f, 0.7f, 10f, 22f);

    public static float getBaseGrowthChance() {
        return CropCultivationConfig.BASE_GROWTH_CHANCE.get().floatValue(); //0.4f; //vanilla ~0.33f
    }

    public static float getBaseYieldMultiplier() {
        return CropCultivationConfig.BASE_YIELD_MULTIPLIER.get().floatValue(); //1.65f
    }

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
            float yieldModifier = (currP / soilContext.getMaxNutrientAmount() + currK / soilContext.getMaxNutrientAmount()) * 0.5f * getBaseYieldMultiplier();

            //the crop might have advanced several ages at once
            int ageDiff = newCropAge - prevCropAge; //age difference can be negative when the crop age was decreased --> newCropAge < prevCropAge
            yieldModifier *= ageDiff; //scale yieldModifier

            soilContext.getTileState().addCropYield(yieldModifier); //increase/decrease yield
        }
    }

    public static void modifyGeneratedLoot(List<ItemStack> generatedLoot, Item targetItem, int lootAmount, int yieldAmount, Random rand) {
        if (lootAmount != yieldAmount) {
            int n = 0;
            generatedLoot.removeIf(stack -> stack.getItem() == targetItem);

            //get the loot as multiple ItemStacks
            while (n < yieldAmount) {
                if (yieldAmount - n > 3) {
                    int amount = rand.nextInt(3) + 1; // 1-3
                    generatedLoot.add(new ItemStack(targetItem, amount));
                    n += amount;
                }
                else { //remainder
                    int amount = yieldAmount - n;
                    generatedLoot.add(new ItemStack(targetItem, amount));
                    break;
                }
            }
        }
    }

    public static boolean canBlockTurnIntoFarmland(Block block) {
        return CommonRegistry.HOE_LOOKUP.containsKey(block);
    }

    public static BlockState getWeedPlant(SoilStateContext soilContext) {
        if (soilContext.moisture < SoilMoisture.MOIST.getMoistureLevel()) {
            return soilContext.getWorld().rand.nextFloat() < 0.75f ? ModBlocks.WEED.getDefaultState() : ModBlocks.WEED.getDefaultState().with(WeedBlock.WEED_TYPE, WeedType.SOWTHISTLE);
        }
        return soilContext.getWorld().rand.nextFloat() < 0.35f ? ModBlocks.WEED.getDefaultState() : ModBlocks.WEED.getDefaultState().with(WeedBlock.WEED_TYPE, WeedType.TALL_GRASS);
    }

    public static BlockState getDeadPlant(ICropEntry iCrop, SoilStateContext soilContext) {
        return soilContext.moisture > iCrop.getMaxSoilMoisture() * SoilMoisture.MAX_VALUE ? ModBlocks.DEAD_CROP_ROTTEN.getDefaultState() : ModBlocks.DEAD_CROP_WITHERED.getDefaultState();
    }

    public static abstract class RegisteredCrop
    {
        public static boolean canCropGrow(World world, BlockPos cropPos, BlockState cropState, ICropEntry iCrop, SoilStateContext soilContext) {

            //check soil pH
            if (soilContext.pH + 0.1f < iCrop.getMinSoilPH() || soilContext.pH - 0.1f > iCrop.getMaxSoilPH()) {
                return false; //don't grow outside the pH tolerance range
            }

            //check soil moisture
            int moistureTolerance = BlockPropertyUtil.getMoistureTolerance(cropState);
            if (soilContext.moisture + moistureTolerance < iCrop.getMinSoilMoisture() * SoilMoisture.MAX_VALUE || soilContext.moisture - moistureTolerance > iCrop.getMaxSoilMoisture() * SoilMoisture.MAX_VALUE) {
                return false; //don't grow outside the moisture tolerance range
            }

            //check temperature
            float temperatureTolerance = BlockPropertyUtil.getTemperatureTolerance(cropState);
            float localTemperature = ClimateUtil.getLocalTemperature(world.getBiome(cropPos), cropPos, cropState);
            float soilTemperature = ClimateUtil.getLocalTemperature(world.getBiome(soilContext.getBlockPos()), soilContext.getBlockPos(), soilContext.getBlockState());
            float temperature = MathHelperX.lerp(0.7f, localTemperature, soilTemperature);
            if (temperature + temperatureTolerance < iCrop.getMinTemperature() || temperature - temperatureTolerance > iCrop.getMaxTemperature()) {
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

            return currN >= reqN && currP >= reqP && currK >= reqK ? getBaseGrowthChance() * ((nPct + pPct + kPct) / 3f) : 0.0f;
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
