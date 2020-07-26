package com.creativechasm.cropcultivation;

import com.creativechasm.cropcultivation.init.CommonRegistry;
import com.creativechasm.cropcultivation.optionaldependency.HarvestCraftAddon;
import com.creativechasm.cropcultivation.optionaldependency.OptionalRegistry;
import com.creativechasm.cropcultivation.optionaldependency.SimpleFarmingAddon;
import com.creativechasm.cropcultivation.optionaldependency.XLFoodAddon;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(CropCultivationMod.MOD_ID)
public class CropCultivationMod
{
    public static final String MOD_ID = "cropcultivation";

    public static final Logger LOGGER = LogManager.getLogger();

    public CropCultivationMod() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::onCommonSetup);
    }

    private void onCommonSetup(FMLCommonSetupEvent event) {
//        ClimateUtil.dumpBiomeTemperatureAndHumidity();
//        ClimateUtil.resetTemperatureScaler();

        CommonRegistry.registerCrops();

        HarvestCraftAddon.getInstance().onCommonSetup();
        SimpleFarmingAddon.getInstance().onCommonSetup();
        XLFoodAddon.getInstance().onCommonSetup();
        OptionalRegistry.Common.onSetup();

        CommonRegistry.registerCompostableItems();
    }
}
