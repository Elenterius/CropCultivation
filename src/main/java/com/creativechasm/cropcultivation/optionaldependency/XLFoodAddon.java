package com.creativechasm.cropcultivation.optionaldependency;

import com.creativechasm.cropcultivation.CropCultivationMod;
import org.apache.logging.log4j.MarkerManager;

public class XLFoodAddon extends OptionalAddon {

    private static XLFoodAddon INSTANCE = new XLFoodAddon();

    public XLFoodAddon() {
        super("xlfoodmod", "mariot7.xlfoodmod.XLFoodMod");
    }

    public static XLFoodAddon getInstance() {
        return INSTANCE;
    }

    @Override
    public void onCommonSetup() {
        if(isModLoaded()) {
            CropCultivationMod.LOGGER.info(MarkerManager.getMarker("ModCompat"), "<XL Food> Mod is loaded");
            CropCultivationMod.LOGGER.warn(MarkerManager.getMarker("ModCompat"), "The growing behavior of all compatible <XL Food> crops will be modified!");

            //TODO: load & register data for simple farming crops
        }
    }
}
