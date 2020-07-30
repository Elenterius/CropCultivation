package com.creativechasm.cropcultivation.optionaldependency;

import com.creativechasm.cropcultivation.CropCultivationMod;
import org.apache.logging.log4j.MarkerManager;

public class HarvestCraftHandler implements IOptionalModHandler
{
    @Override
    public void onSetup() {
        CropCultivationMod.LOGGER.info(MarkerManager.getMarker("ModCompat"), "setting up mod compatibility for <Pam's HarvestCraft 2 Crops>...");
        CropCultivationMod.LOGGER.warn(MarkerManager.getMarker("ModCompat"), "The growing behavior of all compatible <Pam's HarvestCraft 2> crops will be modified!");
    }
}
