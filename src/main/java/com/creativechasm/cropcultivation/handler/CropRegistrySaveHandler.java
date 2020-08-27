package com.creativechasm.cropcultivation.handler;

import com.creativechasm.cropcultivation.CropCultivationConfig;
import com.creativechasm.cropcultivation.CropCultivationMod;
import com.creativechasm.cropcultivation.registry.CropRegistry;
import net.minecraft.block.CropsBlock;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

@Mod.EventBusSubscriber(modid = CropCultivationMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public abstract class CropRegistrySaveHandler
{
    private static File prevWorldDir = null;

    @SubscribeEvent
    public static void onWorldLoad(WorldEvent.Load event) {
        if (!event.getWorld().isRemote() && event.getWorld().getDimension().getType() == DimensionType.OVERWORLD) {
            if (event.getWorld() instanceof ServerWorld) {
                File worldDir = ((ServerWorld) event.getWorld()).getSaveHandler().getWorldDirectory();

                if (prevWorldDir == worldDir) {
                    CropCultivationMod.LOGGER.info(CropRegistry.LOG_MARKER, "crop data already loaded... skipping");
                    return;
                }
                prevWorldDir = worldDir;

                CropCultivationMod.LOGGER.info(CropRegistry.LOG_MARKER, "loading crop data for world: " + event.getWorld().getWorldInfo().getWorldName());
                File cropDataDir = new File(worldDir, "data/cropcultivation/crop_registry");
                if (!cropDataDir.exists()) {
                    boolean flag = cropDataDir.mkdirs();
                }

                String mappingResource = "data/cropcultivation/crop_registry/mappings.csv";
                String entriesResource = "data/cropcultivation/crop_registry/entries.csv";
                File cropIdMappings = new File(worldDir, mappingResource);
                File cropEntries = new File(worldDir, entriesResource);

                if (!CropCultivationConfig.randomCropPropertyMode.get()) {
                    if (!cropEntries.exists()) copyCropRegistryFiles("/" + entriesResource, cropEntries);
                    if (!cropIdMappings.exists()) copyCropRegistryFiles("/" + mappingResource, cropIdMappings);

                    try {
                        CropCultivationMod.PROXY.getCropRegistry().clear();
                        CropCultivationMod.PROXY.getCropRegistry().buildRegistry(cropIdMappings, cropEntries);
                    }
                    catch (Exception e) {
                        CropCultivationMod.LOGGER.error(CropRegistry.LOG_MARKER, "failed to populate registry", e);
                        throw new RuntimeException();
                    }
                }
                else {
                    //TODO: implement this
                    ForgeRegistries.BLOCKS.forEach(block -> {
                        if (block instanceof CropsBlock) {
                            String commonId = block.getRegistryName().getPath().replace("_crops", "").replace("_crop", "").trim();
                        }
                    });

                    //Stuff
                }
            }
        }
    }

    private static void copyCropRegistryFiles(String resourceName, File target) {
        try {
            FileUtils.copyInputStreamToFile(CropRegistrySaveHandler.class.getResourceAsStream(resourceName), target);
        }
        catch (IOException e) {
            CropCultivationMod.LOGGER.error("Failed to copy crop registry file: " + resourceName, e);
        }
    }

    @SubscribeEvent
    public static void onWorldUnload(WorldEvent.Unload event) {
        if (!event.getWorld().isRemote() && event.getWorld().getDimension().getType() == DimensionType.OVERWORLD) {
            prevWorldDir = null;
        }
    }
}
