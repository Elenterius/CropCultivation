package com.creativechasm.blightbiome.client;

import com.creativechasm.blightbiome.BlightBiomeMod;
import com.creativechasm.blightbiome.client.renderer.entity.BlightBroodRenderer;
import com.creativechasm.blightbiome.client.renderer.entity.BlobInsectRenderer;
import com.creativechasm.blightbiome.client.renderer.entity.BroodmotherRenderer;
import com.creativechasm.blightbiome.client.renderer.entity.PestererRenderer;
import com.creativechasm.blightbiome.common.registry.BlockRegistry;
import com.creativechasm.blightbiome.common.registry.EntityRegistry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
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

//			ClientRegistry.bindTileEntitySpecialRenderer(WormholeTileEntity.class, new WormholeTileEntityRenderer());

        RenderType cutout = RenderType.getCutout();
        RenderTypeLookup.setRenderLayer(BlockRegistry.BLIGHT_WEED, cutout);
        RenderTypeLookup.setRenderLayer(BlockRegistry.BLIGHT_MUSHROOM_TALL, cutout);
        RenderTypeLookup.setRenderLayer(BlockRegistry.BLIGHT_MAIZE, cutout);
        RenderTypeLookup.setRenderLayer(BlockRegistry.BLIGHT_SPROUT, cutout);
        RenderTypeLookup.setRenderLayer(BlockRegistry.BLIGHT_SPROUT_SMALL, cutout);
    }
}
