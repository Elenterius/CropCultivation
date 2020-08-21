package com.creativechasm.cropcultivation;

import com.creativechasm.cropcultivation.init.ClientProxy;
import com.creativechasm.cropcultivation.init.CommonProxy;
import com.creativechasm.cropcultivation.init.CommonRegistry;
import com.creativechasm.cropcultivation.init.IProxy;
import com.creativechasm.cropcultivation.optionaldependency.OptionalRegistry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(CropCultivationMod.MOD_ID)
@Mod.EventBusSubscriber(modid = CropCultivationMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CropCultivationMod
{
    public static final String MOD_ID = "cropcultivation";
    public static final Logger LOGGER = LogManager.getLogger();

    public static IProxy PROXY = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> CommonProxy::new);

    public CropCultivationMod() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CropCultivationConfig.COMMON_SPEC, "cropcultivation-common.toml");
        CommonRegistry.init();
    }

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
//        ClimateUtil.dumpBiomeTemperatureAndHumidity();
//        ClimateUtil.resetTemperatureScaler();

        OptionalRegistry.Mods.onSetup();
        CommonRegistry.setupFirst();
        OptionalRegistry.Common.onSetup();
        CommonRegistry.setupLast();
    }
}
