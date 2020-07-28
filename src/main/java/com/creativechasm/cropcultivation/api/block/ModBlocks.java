package com.creativechasm.cropcultivation.api.block;

import com.creativechasm.cropcultivation.CropCultivationMod;
import net.minecraft.block.Block;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(CropCultivationMod.MOD_ID)
public class ModBlocks
{
    @ObjectHolder("silt")
    public static Block SILT;

    @ObjectHolder("sandy_dirt")
    public static Block SANDY_DIRT;

    @ObjectHolder("loam")
    public static Block LOAM;

    @ObjectHolder("clayey_dirt")
    public static Block CLAYEY_DIRT;

    @ObjectHolder("loam_soil")
    public static SoilBlock LOAMY_SOIL;

    @ObjectHolder("silt_soil")
    public static SoilBlock SILTY_SOIL;

    @ObjectHolder("sand_soil")
    public static SoilBlock SANDY_SOIL;

    @ObjectHolder("clay_soil")
    public static SoilBlock CLAYEY_SOIL;
}
