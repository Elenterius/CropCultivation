package com.creativechasm.environment.mixin;

import com.creativechasm.environment.EnvironmentLib;
import org.apache.logging.log4j.MarkerManager;
import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.connect.IMixinConnector;

public class Connector implements IMixinConnector {

    @Override
    public void connect() {
        EnvironmentLib.LOGGER.info(MarkerManager.getMarker("MIXIN"), "Invoking Mixin Connector");
        Mixins.addConfiguration("assets/envirlib/envirlib.mixins.json");
    }

}
