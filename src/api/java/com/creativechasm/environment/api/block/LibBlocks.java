package com.creativechasm.environment.api.block;

import com.creativechasm.environment.EnvironmentLib;
import net.minecraft.block.Block;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(EnvironmentLib.MOD_ID)
public class LibBlocks {

    @ObjectHolder("silt")
    public static Block SILT;

    @ObjectHolder("loam_soil")
    public static SoilBlock LOAM_SOIL;

    @ObjectHolder("silt_soil")
    public static SoilBlock SILT_SOIL;

    @ObjectHolder("sand_soil")
    public static SoilBlock SAND_SOIL;

    @ObjectHolder("clay_soil")
    public static SoilBlock CLAY_SOIL;
}
