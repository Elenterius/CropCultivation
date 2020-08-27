package com.creativechasm.cropcultivation.init;

import com.creativechasm.cropcultivation.registry.CropRegistry;

public abstract class CommonProxy implements IProxy
{
    private final CropRegistry cropRegistry = new CropRegistry();

    public CropRegistry getCropRegistry() {
        return cropRegistry;
    }

    @Override
    public void onCommonSetup() {
//        String VOLUME_LOCATION = "sortedTableMapVol.db";
//
//        Volume vol = MappedFileVol.FACTORY.makeVolume(VOLUME_LOCATION, false);
//        SortedTableMap.Sink<String, String> sink = SortedTableMap.create(vol, Serializer.STRING, Serializer.STRING).createFromSink();
//        for (int i = 97; i < 123; i++) {
//            sink.put(String.valueOf((char) i), "Value " + i);
//        }
//        sink.create();
//
//        Volume openVol = MappedFileVol.FACTORY.makeVolume(VOLUME_LOCATION, true);
//        SortedTableMap<String, String> sortedTableMap = SortedTableMap.open(openVol, Serializer.STRING, Serializer.STRING);
//
//        sortedTableMap.close();
//        vol.close();
//        openVol.close();
    }



    @Override
    public void openTabletScreen() {
        //only implemented on client side
    }
}
