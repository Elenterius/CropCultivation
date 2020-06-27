package com.creativechasm.blightbiome.registry;

import com.creativechasm.blightbiome.BlightBiomeMod;
import com.creativechasm.blightbiome.client.renderer.entity.*;
import com.creativechasm.blightbiome.common.block.SoilBlock;
import com.creativechasm.blightbiome.common.util.MoistureType;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.world.biome.BiomeColors;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = BlightBiomeMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModelRegistry {

    @SubscribeEvent
    public static void onModelRegistry(final ModelRegistryEvent registryEvent) {
        RenderingRegistry.registerEntityRenderingHandler(EntityRegistry.BLOB_INSECT, BlobInsectRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityRegistry.BROOD_MOTHER, BroodmotherRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityRegistry.PESTERER, PestererRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityRegistry.BLIGHT_BROOD, BlightBroodRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityRegistry.BELL_SLIME, BellSlimeRenderer::new);

//			ClientRegistry.bindTileEntitySpecialRenderer(WormholeTileEntity.class, new WormholeTileEntityRenderer());

        RenderType cutout = RenderType.getCutout();
        RenderTypeLookup.setRenderLayer(BlockRegistry.BLIGHT_WEED, cutout);
        RenderTypeLookup.setRenderLayer(BlockRegistry.BLIGHT_MUSHROOM_TALL, cutout);
        RenderTypeLookup.setRenderLayer(BlockRegistry.BLIGHT_MAIZE, cutout);
        RenderTypeLookup.setRenderLayer(BlockRegistry.BLIGHT_SPROUT, cutout);
        RenderTypeLookup.setRenderLayer(BlockRegistry.BLIGHT_SPROUT_SMALL, cutout);
        RenderTypeLookup.setRenderLayer(BlockRegistry.LILY_TREE_SAPLING, cutout);

        RenderTypeLookup.setRenderLayer(BlockRegistry.BLOOMING_FLOWER_TEST, cutout);


        RenderTypeLookup.setRenderLayer(BlockRegistry.LOAM_SOIL, RenderType.getTranslucent());
    }

    @SubscribeEvent
    public static void onBlockColorRegistry(final ColorHandlerEvent.Block event) {
        event.getBlockColors().register(
                (state, lightReader, pos, index) -> lightReader != null && pos != null && state.get(SoilBlock.MOISTURE) >= MoistureType.WET.getMoistureLevel() ? BiomeColors.getWaterColor(lightReader, pos) : -1,
                BlockRegistry.LOAM_SOIL
        );
    }
}
