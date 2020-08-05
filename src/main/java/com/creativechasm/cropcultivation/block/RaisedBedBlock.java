package com.creativechasm.cropcultivation.block;

import com.creativechasm.cropcultivation.environment.ClimateUtil;
import com.creativechasm.cropcultivation.environment.soil.IBlockTemperatureHandler;
import com.creativechasm.cropcultivation.environment.soil.SoilTexture;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;

public abstract class RaisedBedBlock extends SoilBlock implements IBlockTemperatureHandler
{
    public RaisedBedBlock(Properties properties, SoilTexture soilTexture) {
        super(properties, soilTexture);
    }

    @Override
    public float getBlockTemperature(Biome biome, float localTemperature, BlockPos pos, BlockState state) {
        float celsiusTemperature = ClimateUtil.convertTemperatureMCToCelsius(localTemperature) + 15f;
        return ClimateUtil.convertTemperatureCelsiusToMC(celsiusTemperature);
    }
}
