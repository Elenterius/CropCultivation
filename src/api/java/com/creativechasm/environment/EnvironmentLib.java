package com.creativechasm.environment;

import com.creativechasm.environment.util.ClimateUtil;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(EnvironmentLib.MOD_ID)
public class EnvironmentLib {
    public static final String MOD_ID = "envirlib";

    public static final Logger LOGGER = LogManager.getLogger();

    public EnvironmentLib() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::onCommonSetup);
    }

    private void onCommonSetup(FMLCommonSetupEvent event) {
        ClimateUtil.determineTemperatureScale();
    }
}
