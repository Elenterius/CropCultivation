package com.creativechasm.cropcultivation.optionaldependency;

import com.creativechasm.cropcultivation.CropCultivationMod;
import enemeez.simplefarming.block.growable.DoubleCropBlock;
import enemeez.simplefarming.block.growable.PlantBlock;
import net.minecraft.block.Block;
import org.apache.logging.log4j.MarkerManager;

public class SimpleFarmingHandler implements IOptionalModHandler
{
    public static boolean isCrop(Object owner) {
        return owner instanceof DoubleCropBlock || owner instanceof PlantBlock;
    }

    public static boolean isDoubleCrop(Block block) {
        return block instanceof DoubleCropBlock;
    }

    @Override
    public void onSetup() {
        CropCultivationMod.LOGGER.info(MarkerManager.getMarker("ModCompat"), "setting up mod compatibility for <Simple Farming>...");
        CropCultivationMod.LOGGER.warn(MarkerManager.getMarker("ModCompat"), "The growing behavior of all compatible <Simple Farming> crops will be modified!");
    }

}
