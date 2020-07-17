package com.creativechasm.environment.optionaldependency;

import com.creativechasm.environment.EnvironmentLib;
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
            EnvironmentLib.LOGGER.info(MarkerManager.getMarker("ModCompat"), "<XL Food> Mod is loaded");
            EnvironmentLib.LOGGER.warn(MarkerManager.getMarker("ModCompat"), "The growing behavior of all compatible <XL Food> crops will be modified!");

            //TODO: load & register data for simple farming crops
        }
    }
}
