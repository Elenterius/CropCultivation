package com.creativechasm.cropcultivation.environment.soil;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;

public interface IBlockTemperatureHandler
{
    float getBlockTemperature(Biome biome, float localTemperature, BlockPos pos, BlockState state);
}
