package com.creativechasm.cropcultivation;

import com.creativechasm.cropcultivation.init.ClientProxy;
import com.creativechasm.cropcultivation.init.CommonRegistry;
import com.creativechasm.cropcultivation.init.IProxy;
import com.creativechasm.cropcultivation.init.ServerProxy;
import com.creativechasm.cropcultivation.optionaldependency.OptionalCommonRegistry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(CropCultivationMod.MOD_ID)
@Mod.EventBusSubscriber(modid = CropCultivationMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CropCultivationMod
{
    public static final String MOD_ID = "cropcultivation";
    public static final Logger LOGGER = LogManager.getLogger();

    public static IProxy PROXY = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> ServerProxy::new);

    public CropCultivationMod() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CropCultivationConfig.COMMON_SPEC, "cropcultivation-common.toml");
        CommonRegistry.init();
    }

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        PROXY.onCommonSetup();

//        ClimateUtil.dumpBiomeTemperatureAndHumidity();
//        ClimateUtil.resetTemperatureScaler();

        OptionalCommonRegistry.Mods.onSetup();
        CommonRegistry.setupFirst();
        OptionalCommonRegistry.Main.onSetup();
        CommonRegistry.setupLast();
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        PROXY.onSidedSetup();
    }

    @SubscribeEvent
    public static void onServerSetup(FMLDedicatedServerSetupEvent event) {
        PROXY.onSidedSetup();
    }
}
