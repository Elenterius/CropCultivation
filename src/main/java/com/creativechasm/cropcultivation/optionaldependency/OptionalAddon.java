package com.creativechasm.cropcultivation.optionaldependency;

import net.minecraftforge.fml.ModList;

public abstract class OptionalAddon {

    public final String modId;
    public final String modClass;
    private boolean isModLoaded = false;

    public OptionalAddon(String modId, String modClass) {
        this.modId = modId;
        this.modClass = modClass;
    }

    public abstract void onCommonSetup();

    public boolean isModPresent() {
        return isPresent(modClass);
    }

    public boolean isModLoaded() {
        if (!isModLoaded) {
            ModList modList = ModList.get();
            isModLoaded = modList.isLoaded(modId);
        }
        return isModLoaded;
    }

    public static boolean isPresent(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
//            EnvironmentLib.LOGGER.error(MarkerManager.getMarker("ModCompat"),"optional mod dependency not present", e);
            return false;
        }
    }

}
