package com.creativechasm.cropcultivation.init;

import com.creativechasm.cropcultivation.CropCultivationMod;
import com.creativechasm.cropcultivation.api.block.ModBlocks;
import com.creativechasm.cropcultivation.api.block.SoilBlock;
import com.creativechasm.cropcultivation.api.item.ModItems;
import com.creativechasm.cropcultivation.api.soil.SoilMoisture;
import com.creativechasm.cropcultivation.api.soil.SoilTexture;
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
@Mod.EventBusSubscriber(modid = CropCultivationMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientRegistry {

    @SubscribeEvent
    public static void onModelRegistry(final ModelRegistryEvent registryEvent) {
        RenderType translucent = RenderType.getTranslucent();
        RenderTypeLookup.setRenderLayer(ModBlocks.LOAM_SOIL, translucent);
        RenderTypeLookup.setRenderLayer(ModBlocks.SAND_SOIL, translucent);
        RenderTypeLookup.setRenderLayer(ModBlocks.SILT_SOIL, translucent);
        RenderTypeLookup.setRenderLayer(ModBlocks.CLAY_SOIL, translucent);
    }

    public static IBlockColor soilColors = (state, lightReader, pos, index) -> {
        if (lightReader != null && pos != null) {
            if (index == 0) return state.get(SoilBlock.MOISTURE) >= SoilMoisture.WET.getMoistureLevel() ? BiomeColors.getWaterColor(lightReader, pos) : -1;
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
                ModBlocks.LOAM_SOIL, ModBlocks.SAND_SOIL, ModBlocks.SILT_SOIL, ModBlocks.CLAY_SOIL
        );

        event.getBlockColors().register((state, lightReader, pos, index) -> SoilTexture.SILT.color, ModBlocks.SILT);
    }

    @SubscribeEvent
    public static void onItemColorRegistry(final ColorHandlerEvent.Item event) {
        event.getItemColors().register((stack, index) -> {
            BlockState state = ((BlockItem) stack.getItem()).getBlock().getDefaultState();
            return soilColors.getColor(state, null, null, index);
        }, ModBlocks.LOAM_SOIL, ModBlocks.SAND_SOIL, ModBlocks.SILT_SOIL, ModBlocks.CLAY_SOIL);

        event.getItemColors().register((stack, index) -> SoilTexture.SILT.color, ModBlocks.SILT);

        event.getItemColors().register((stack, index) -> 0x4f9ad8, ModItems.NPK_FERTILIZER);
        event.getItemColors().register((stack, index) -> 0xdfd8bf, ModItems.FEATHER_MEAL);
        event.getItemColors().register((stack, index) -> 0x474431, ModItems.SEAWEED_MEAL);
        event.getItemColors().register((stack, index) -> 0x73868c, ModItems.FISH_MEAL);
        event.getItemColors().register((stack, index) -> 0x7e7b76, ModItems.WOOD_ASH);
        event.getItemColors().register((stack, index) -> 0xdfd8bf, ModItems.LIME_DUST);
    }
}
