package com.creativechasm.environment.optionaldependency;

import com.creativechasm.environment.EnvironmentLib;
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
            EnvironmentLib.LOGGER.info(MarkerManager.getMarker("ModCompat"), "<Simple Farming> Mod is loaded");
            EnvironmentLib.LOGGER.warn(MarkerManager.getMarker("ModCompat"), "The growing behavior of all compatible <Simple Farming> crops will be modified!");

            //TODO: load & register data for simple farming crops
        }
    }
}
