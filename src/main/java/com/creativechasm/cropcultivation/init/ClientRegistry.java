package com.creativechasm.cropcultivation.init;

import com.creativechasm.cropcultivation.CropCultivationMod;
import com.creativechasm.cropcultivation.block.ModBlocks;
import com.creativechasm.cropcultivation.block.SoilBlock;
import com.creativechasm.cropcultivation.environment.soil.SoilMoisture;
import com.creativechasm.cropcultivation.environment.soil.SoilTexture;
import com.creativechasm.cropcultivation.item.ModItems;
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
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = CropCultivationMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientRegistry
{
    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        RenderType translucent = RenderType.getTranslucent();
        RenderType solid = RenderType.getSolid();
        RenderTypeLookup.setRenderLayer(ModBlocks.LOAMY_SOIL, layer -> layer == solid || layer == translucent);
        RenderTypeLookup.setRenderLayer(ModBlocks.SANDY_SOIL, layer -> layer == solid || layer == translucent);
        RenderTypeLookup.setRenderLayer(ModBlocks.SILTY_SOIL, layer -> layer == solid || layer == translucent);
        RenderTypeLookup.setRenderLayer(ModBlocks.CLAYEY_SOIL, layer -> layer == solid || layer == translucent);
        RenderTypeLookup.setRenderLayer(ModBlocks.LOAMY_SOIL_RAISED_BED, layer -> layer == solid || layer == translucent);
        RenderTypeLookup.setRenderLayer(ModBlocks.SANDY_SOIL_RAISED_BED, layer -> layer == solid || layer == translucent);
        RenderTypeLookup.setRenderLayer(ModBlocks.SILTY_SOIL_RAISED_BED, layer -> layer == solid || layer == translucent);
        RenderTypeLookup.setRenderLayer(ModBlocks.CLAYEY_SOIL_RAISED_BED, layer -> layer == solid || layer == translucent);
    }

    @SubscribeEvent
    public static void onModelRegistry(final ModelRegistryEvent registryEvent) {

    }

    public static IBlockColor soilColors = (state, lightReader, pos, index) -> {
        if (index == 0) {
            return ((SoilBlock) state.getBlock()).soilTexture.color;
        }
        if (lightReader != null && pos != null) {
            if (index == 1) return state.get(SoilBlock.MOISTURE) >= SoilMoisture.WET.getMoistureLevel() ? BiomeColors.getWaterColor(lightReader, pos) : -1;
        }
        return -1;
    };

    @SubscribeEvent
    public static void onBlockColorRegistry(final ColorHandlerEvent.Block event) {
        event.getBlockColors().register(
                soilColors,
                ModBlocks.LOAMY_SOIL, ModBlocks.SANDY_SOIL, ModBlocks.SILTY_SOIL, ModBlocks.CLAYEY_SOIL,
                ModBlocks.LOAMY_SOIL_RAISED_BED, ModBlocks.SANDY_SOIL_RAISED_BED, ModBlocks.SILTY_SOIL_RAISED_BED, ModBlocks.CLAYEY_SOIL_RAISED_BED
        );

        event.getBlockColors().register((state, lightReader, pos, index) -> SoilTexture.SILT.color, ModBlocks.SILT);
        event.getBlockColors().register((state, lightReader, pos, index) -> SoilTexture.LOAM.color, ModBlocks.LOAM);
        event.getBlockColors().register((state, lightReader, pos, index) -> SoilTexture.SAND.color, ModBlocks.SANDY_DIRT);
        event.getBlockColors().register((state, lightReader, pos, index) -> SoilTexture.CLAY.color, ModBlocks.CLAYEY_DIRT);
    }

    @SubscribeEvent
    public static void onItemColorRegistry(final ColorHandlerEvent.Item event) {
        event.getItemColors().register(
                (stack, index) -> {
                    BlockState state = ((BlockItem) stack.getItem()).getBlock().getDefaultState();
                    return soilColors.getColor(state, null, null, index);
                },
                ModBlocks.LOAMY_SOIL, ModBlocks.SANDY_SOIL, ModBlocks.SILTY_SOIL, ModBlocks.CLAYEY_SOIL,
                ModBlocks.LOAMY_SOIL_RAISED_BED, ModBlocks.SANDY_SOIL_RAISED_BED, ModBlocks.SILTY_SOIL_RAISED_BED, ModBlocks.CLAYEY_SOIL_RAISED_BED
        );

        event.getItemColors().register((stack, index) -> SoilTexture.SILT.color, ModBlocks.SILT);
        event.getItemColors().register((state, index) -> SoilTexture.LOAM.color, ModBlocks.LOAM);
        event.getItemColors().register((state, index) -> SoilTexture.SAND.color, ModBlocks.SANDY_DIRT);
        event.getItemColors().register((state, index) -> SoilTexture.CLAY.color, ModBlocks.CLAYEY_DIRT);

        event.getItemColors().register((stack, index) -> 0x4f9ad8, ModItems.NPK_FERTILIZER);
        event.getItemColors().register((stack, index) -> 0xdfd8bf, ModItems.FEATHER_MEAL);
        event.getItemColors().register((stack, index) -> 0x474431, ModItems.SEAWEED_MEAL);
        event.getItemColors().register((stack, index) -> 0x73868c, ModItems.FISH_MEAL);
        event.getItemColors().register((stack, index) -> 0x7e7b76, ModItems.WOOD_ASH);
        event.getItemColors().register((stack, index) -> 0xdfd8bf, ModItems.LIME_DUST);
        event.getItemColors().register((stack, index) -> 0xce805a, ModItems.GRANITE_DUST);
    }
}
