package com.creativechasm.cropcultivation;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod.EventBusSubscriber(modid = CropCultivationMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class CropCultivationConfig
{
//    public static ForgeConfigSpec.BooleanValue randomCropPropertyMode;
    public static ForgeConfigSpec.DoubleValue BASE_GROWTH_CHANCE;
    public static ForgeConfigSpec.DoubleValue BASE_YIELD_MULTIPLIER;

    static final ForgeConfigSpec COMMON_SPEC = new ForgeConfigSpec.Builder().configure(CropCultivationConfig::new).getRight();

    private CropCultivationConfig(ForgeConfigSpec.Builder builder) {
        builder.comment("Common Configuration for Crop Cultivation");
        builder.push("common");

//        randomCropPropertyMode = builder.comment("Generate Random Crop Properties for each World?").define("randomCropPropertyMode", false);
        BASE_GROWTH_CHANCE = builder.comment("Base value used to calculate the Growth Chance dependent on the available nutrients in the soil. A value of 0.33 would be \"similar\" to vanilla minecraft growth chance.").defineInRange("baseGrowthChance", 0.4D, 0, 1);
        BASE_YIELD_MULTIPLIER = builder.comment("Multiplier that influences how much loot is dropped depending on how well nourished the crop was.").defineInRange("baseYieldMultiplier", 1.65D, 0.1D, 9.65D);

        builder.pop();
    }

    @SubscribeEvent
    public static void onLoad(final ModConfig.Loading event) {

    }

    @SubscribeEvent
    public static void onReload(final ModConfig.Reloading event) {

    }
}
