package com.creativechasm.cropcultivation.handler;

import com.creativechasm.cropcultivation.CropCultivationMod;
import com.creativechasm.cropcultivation.api.plant.ICropEntry;
import com.creativechasm.cropcultivation.api.tags.EnvirlibTags;
import com.creativechasm.cropcultivation.init.CommonRegistry;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Optional;

@Mod.EventBusSubscriber(modid = CropCultivationMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public abstract class FertilizerHandler
{

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onBonemealUse(BonemealEvent event) {

        //disable bone meal for supported crops/plants
        Optional<ICropEntry> optionalICrop = CommonRegistry.CROP_REGISTRY.get(event.getBlock().getBlock().getRegistryName());
        if (optionalICrop.isPresent()) {
            event.setCanceled(true);
            return;
        }

        if (event.getBlock().getBlock() == Blocks.NETHER_WART) { //temporary fix to prevent bone meal particle spawning
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onItemToolTip(ItemTooltipEvent event) {
        Item item = event.getItemStack().getItem();
        if (EnvirlibTags.FERTILIZER_GROUP.contains(item)) {  // add fertilizer info
            event.getToolTip().add(new StringTextComponent(""));
            event.getToolTip().add(new TranslationTextComponent("fertilizer.desc").applyTextStyle(TextFormatting.GRAY));
            if (EnvirlibTags.N_FERTILIZER.contains(item)) event.getToolTip().add(new StringTextComponent(" ").appendSibling(new TranslationTextComponent("nutrient.nitrogen").applyTextStyle(TextFormatting.GRAY)));
            if (EnvirlibTags.P_FERTILIZER.contains(item)) event.getToolTip().add(new StringTextComponent(" ").appendSibling(new TranslationTextComponent("nutrient.phosphorus").applyTextStyle(TextFormatting.GRAY)));
            if (EnvirlibTags.K_FERTILIZER.contains(item)) event.getToolTip().add(new StringTextComponent(" ").appendSibling(new TranslationTextComponent("nutrient.potassium").applyTextStyle(TextFormatting.GRAY)));
        }
    }
}
