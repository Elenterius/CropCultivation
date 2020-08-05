package com.creativechasm.cropcultivation.block;

import com.creativechasm.cropcultivation.environment.ClimateUtil;
import com.creativechasm.cropcultivation.environment.soil.IBlockTemperatureHandler;
import com.creativechasm.cropcultivation.environment.soil.SoilMoisture;
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
        int moisture = state.get(MOISTURE);
        if (moisture < SoilMoisture.AVERAGE_0.getMoistureLevel()) return localTemperature;

        float compostPct = state.get(ORGANIC_MATTER) / 4f;
        float celsiusTemperature = (ClimateUtil.convertTemperatureMCToCelsius(localTemperature) + 34f * compostPct) / 2f;
        return Math.max(localTemperature, ClimateUtil.convertTemperatureCelsiusToMC(celsiusTemperature));
    }
}
