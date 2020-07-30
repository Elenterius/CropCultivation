package com.creativechasm.cropcultivation.optionaldependency;

import com.creativechasm.cropcultivation.CropCultivationMod;
import org.apache.logging.log4j.MarkerManager;

public class XLFoodHandler implements IOptionalModHandler
{
    @Override
    public void onSetup() {
        CropCultivationMod.LOGGER.info(MarkerManager.getMarker("ModCompat"), "setting up mod compatibility for <XL Food>...");
        CropCultivationMod.LOGGER.warn(MarkerManager.getMarker("ModCompat"), "The growing behavior of all compatible <XL Food> crops will be modified!");
    }
}
