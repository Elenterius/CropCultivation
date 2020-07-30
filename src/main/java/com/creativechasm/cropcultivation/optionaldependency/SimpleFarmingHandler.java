package com.creativechasm.cropcultivation.optionaldependency;

import com.creativechasm.cropcultivation.CropCultivationMod;
import org.apache.logging.log4j.MarkerManager;

public class SimpleFarmingHandler implements IOptionalModHandler
{
    @Override
    public void onSetup() {
        CropCultivationMod.LOGGER.info(MarkerManager.getMarker("ModCompat"), "setting up mod compatibility for <Simple Farming>...");
        CropCultivationMod.LOGGER.warn(MarkerManager.getMarker("ModCompat"), "The growing behavior of all compatible <Simple Farming> crops will be modified!");
    }
}
