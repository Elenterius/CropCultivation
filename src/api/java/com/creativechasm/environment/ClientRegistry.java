package com.creativechasm.environment;

import com.creativechasm.environment.api.block.LibBlocks;
import com.creativechasm.environment.api.block.SoilBlock;
import com.creativechasm.environment.api.soil.MoistureType;
import com.creativechasm.environment.api.soil.SoilTexture;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.item.BlockItem;
import net.minecraft.world.biome.BiomeColors;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = EnvironmentLib.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientRegistry {

    @SubscribeEvent
    public static void onModelRegistry(final ModelRegistryEvent registryEvent) {
        RenderType translucent = RenderType.getTranslucent();
        RenderTypeLookup.setRenderLayer(LibBlocks.LOAM_SOIL, translucent);
        RenderTypeLookup.setRenderLayer(LibBlocks.SAND_SOIL, translucent);
        RenderTypeLookup.setRenderLayer(LibBlocks.SILT_SOIL, translucent);
        RenderTypeLookup.setRenderLayer(LibBlocks.CLAY_SOIL, translucent);
    }

    public static IBlockColor soilColors = (state, lightReader, pos, index) -> {
        if (lightReader != null && pos != null) {
            if (index == 0) return state.get(SoilBlock.MOISTURE) >= MoistureType.WET.getMoistureLevel() ? BiomeColors.getWaterColor(lightReader, pos) : -1;
        }
        if (index == 1 && state.getBlock() instanceof SoilBlock) {
            return ((SoilBlock) state.getBlock()).soilTexture.color;
        }
        return -1;
    };

    @SubscribeEvent
    public static void onBlockColorRegistry(final ColorHandlerEvent.Block event) {
        event.getBlockColors().register(
                soilColors,
                LibBlocks.LOAM_SOIL, LibBlocks.SAND_SOIL, LibBlocks.SILT_SOIL, LibBlocks.CLAY_SOIL
        );

        event.getBlockColors().register((state, lightReader, pos, index) -> SoilTexture.SILT.color, LibBlocks.SILT);
    }

    @SubscribeEvent
    public static void onItemColorRegistry(final ColorHandlerEvent.Item event) {
        event.getItemColors().register((stack, index) -> {
            BlockState state = ((BlockItem) stack.getItem()).getBlock().getDefaultState();
            return soilColors.getColor(state, null, null, index);
        }, LibBlocks.LOAM_SOIL, LibBlocks.SAND_SOIL, LibBlocks.SILT_SOIL, LibBlocks.CLAY_SOIL);

        event.getItemColors().register((stack, index) -> SoilTexture.SILT.color, LibBlocks.SILT);
    }
}
