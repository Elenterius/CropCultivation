package com.creativechasm.environment.optionaldependency;

import com.creativechasm.environment.EnvironmentLib;
import org.apache.logging.log4j.MarkerManager;

public class HarvestCraftAddon extends OptionalAddon {

    private static HarvestCraftAddon INSTANCE = new HarvestCraftAddon();

    public HarvestCraftAddon() {
        super("pamhc2crops", "pam.pamhc2crops.Pamhc2crops");
    }

    public static HarvestCraftAddon getInstance() {
        return INSTANCE;
    }

    @Override
    public void onCommonSetup() {
        if (isModLoaded()) {
            EnvironmentLib.LOGGER.info(MarkerManager.getMarker("ModCompat"), "<Pam's HarvestCraft 2 Crops> Mod is loaded");
            EnvironmentLib.LOGGER.warn(MarkerManager.getMarker("ModCompat"), "The growing behavior of all compatible <Pam's HarvestCraft 2> crops will be modified!");

            //TODO: load & register data for harvest craft crops
        }
    }
}
