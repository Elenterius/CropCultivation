package com.creativechasm.cropcultivation.block;

import com.creativechasm.cropcultivation.environment.soil.IRaisedBed;
import com.creativechasm.cropcultivation.environment.soil.SoilTexture;

public abstract class RaisedBedBlock extends SoilBlock implements IRaisedBed
{
    public RaisedBedBlock(Properties properties, SoilTexture soilTexture) {
        super(properties, soilTexture);
    }
}
