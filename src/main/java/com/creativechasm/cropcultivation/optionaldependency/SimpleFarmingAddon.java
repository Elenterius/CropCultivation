package com.creativechasm.cropcultivation.optionaldependency;

import com.creativechasm.cropcultivation.CropCultivationMod;
import org.apache.logging.log4j.MarkerManager;

public class SimpleFarmingAddon extends OptionalAddon {

    private static SimpleFarmingAddon INSTANCE = new SimpleFarmingAddon();

    public SimpleFarmingAddon() {
        super("simplefarming", "enemeez.simplefarming.SimpleFarming");
    }

    public static SimpleFarmingAddon getInstance() {
        return INSTANCE;
    }

    @Override
    public void onCommonSetup() {
        if(isModLoaded()) {
            CropCultivationMod.LOGGER.info(MarkerManager.getMarker("ModCompat"), "<Simple Farming> Mod is loaded");
            CropCultivationMod.LOGGER.warn(MarkerManager.getMarker("ModCompat"), "The growing behavior of all compatible <Simple Farming> crops will be modified!");

            //TODO: load & register data for simple farming crops
        }
    }
}
