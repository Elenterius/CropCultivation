package com.creativechasm.cropcultivation.init;

import com.creativechasm.cropcultivation.CropCultivationMod;
import net.minecraft.item.Item;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(CropCultivationMod.MOD_ID)
public abstract class ModItems
{
//    @ObjectHolder("loam_soil")
//    public static BlockItem LOAM_SOIL;
//    @ObjectHolder("silt_soil")
//    public static BlockItem SILT_SOIL;
//    @ObjectHolder("sand_soil")
//    public static BlockItem SAND_SOIL;
//    @ObjectHolder("clay_soil")
//    public static BlockItem CLAY_SOIL;

    @ObjectHolder("compost")
    public static Item COMPOST;

    @ObjectHolder("mortar_pestle")
    public static Item MORTAR_AND_PESTLE;

    @ObjectHolder("lime_dust")
    public static Item LIME_DUST;

    @ObjectHolder("granite_dust")
    public static Item GRANITE_DUST;

    @ObjectHolder("fertilizer")
    public static Item NPK_FERTILIZER;

    @ObjectHolder("feather_meal")
    public static Item FEATHER_MEAL;

    @ObjectHolder("seaweed_meal")
    public static Item SEAWEED_MEAL;

    @ObjectHolder("fish_meal")
    public static Item FISH_MEAL;

    @ObjectHolder("wood_ash")
    public static Item WOOD_ASH;

    @ObjectHolder("soil_test_kit")
    public static Item SOIL_TEST_KIT;

    @ObjectHolder("thermo_hygrometer")
    public static Item THERMO_HYGROMETER;

    @ObjectHolder("soil_sampler")
    public static Item SOIL_SAMPLER;

    @ObjectHolder("soil_meter")
    public static Item SOIL_METER;

    @ObjectHolder("crop_reader")
    public static Item CROP_READER;

    @ObjectHolder("tablet")
    public static Item TABLET;
}
