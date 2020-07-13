package com.creativechasm.environment;

import com.creativechasm.environment.init.CommonRegistry;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.MarkerManager;

@Mod(EnvironmentLib.MOD_ID)
public class EnvironmentLib {
    public static final String MOD_ID = "envirlib";

    public static final Logger LOGGER = LogManager.getLogger();

    public EnvironmentLib() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::onCommonSetup);
    }

    private void onCommonSetup(FMLCommonSetupEvent event) {
        CommonRegistry.registerCompostableItems();

//        ClimateUtil.dumpBiomeTemperatureAndHumidity();
//        ClimateUtil.resetTemperatureScaler();

//        try {
//            InputStream in = getClass().getResourceAsStream("/assets/settlements/foobar.txt");
//            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
//            String line = reader.readLine();
//            while (line != null) {
//                EnvironmentLib.LOGGER.info(MarkerManager.getMarker("Resource"), line);
//                line = reader.readLine();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            EnvironmentLib.LOGGER.error(MarkerManager.getMarker("Resource"), "failed to read resource", e);
//        }

        ModList modList = ModList.get();
        if (modList.isLoaded("pamhc2crops")) {
            EnvironmentLib.LOGGER.info(MarkerManager.getMarker("ModCompat"), "Harvest-Craft-2-Crops is loaded");
            //TODO: load & register data for harvest craft crops
        }
    }
}
