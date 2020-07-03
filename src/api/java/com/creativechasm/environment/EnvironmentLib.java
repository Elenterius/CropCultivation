package com.creativechasm.environment;

import com.creativechasm.environment.util.ClimateUtil;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(EnvironmentLib.MOD_ID)
public class EnvironmentLib {
    public static final String MOD_ID = "envirlib";
    private static final Logger LOGGER = LogManager.getLogger();

    public EnvironmentLib() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onCommonSetup(final FMLCommonSetupEvent event) {
        ClimateUtil.initTemperatureNormalizer();
    }
}
