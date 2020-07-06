package com.creativechasm.environment.api.item;

import com.creativechasm.environment.EnvironmentLib;
import net.minecraft.item.Item;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(EnvironmentLib.MOD_ID)
public class LibItems {
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

    @ObjectHolder("lime_dust")
    public static Item LIME_DUST;

    @ObjectHolder("fertilizer")
    public static Item FERTILIZER;
}
