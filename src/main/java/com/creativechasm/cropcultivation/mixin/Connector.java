package com.creativechasm.cropcultivation.mixin;

import com.creativechasm.cropcultivation.CropCultivationMod;
import org.apache.logging.log4j.MarkerManager;
import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.connect.IMixinConnector;

public class Connector implements IMixinConnector {

    @Override
    public void connect() {
        CropCultivationMod.LOGGER.info(MarkerManager.getMarker("MIXIN"), "Invoking Mixin Connector");
        Mixins.addConfiguration("assets/cropcultivation/cropcultivation.mixins.json");
    }

}
