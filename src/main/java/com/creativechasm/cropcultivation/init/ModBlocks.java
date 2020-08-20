package com.creativechasm.cropcultivation.init;

import com.creativechasm.cropcultivation.CropCultivationMod;
import com.creativechasm.cropcultivation.block.DeadCropBlock;
import com.creativechasm.cropcultivation.block.RaisedBedBlock;
import com.creativechasm.cropcultivation.block.SoilBlock;
import com.creativechasm.cropcultivation.block.WeedBlock;
import net.minecraft.block.Block;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(CropCultivationMod.MOD_ID)
public abstract class ModBlocks
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

    @ObjectHolder("loam_soil_raised_bed")
    public static RaisedBedBlock LOAMY_SOIL_RAISED_BED;

    @ObjectHolder("silt_soil_raised_bed")
    public static RaisedBedBlock SILTY_SOIL_RAISED_BED;

    @ObjectHolder("sand_soil_raised_bed")
    public static RaisedBedBlock SANDY_SOIL_RAISED_BED;

    @ObjectHolder("clay_soil_raised_bed")
    public static RaisedBedBlock CLAYEY_SOIL_RAISED_BED;

    @ObjectHolder("dead_crop_withered")
    public static DeadCropBlock DEAD_CROP_WITHERED;

    @ObjectHolder("dead_crop_rotten")
    public static DeadCropBlock DEAD_CROP_ROTTEN;

    @ObjectHolder("weed")
    public static WeedBlock WEED;
}
