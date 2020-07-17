package com.creativechasm.environment;

import com.creativechasm.environment.init.CommonRegistry;
import com.creativechasm.environment.optionaldependency.HarvestCraftAddon;
import com.creativechasm.environment.optionaldependency.OptionalRegistry;
import com.creativechasm.environment.optionaldependency.SimpleFarmingAddon;
import com.creativechasm.environment.optionaldependency.XLFoodAddon;
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

        HarvestCraftAddon.getInstance().onCommonSetup();
        SimpleFarmingAddon.getInstance().onCommonSetup();
        XLFoodAddon.getInstance().onCommonSetup();
        OptionalRegistry.Common.onSetup();

        CommonRegistry.registerCompostableItems();
    }
}
