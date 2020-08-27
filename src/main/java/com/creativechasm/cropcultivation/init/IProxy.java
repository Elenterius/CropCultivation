package com.creativechasm.cropcultivation.init;

import com.creativechasm.cropcultivation.registry.CropRegistry;

public interface IProxy
{
    void onSidedSetup();
    void onCommonSetup();
    CropRegistry getCropRegistry();
    void openTabletScreen();
}
