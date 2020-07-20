package com.creativechasm.environment.handler;

import com.creativechasm.environment.EnvironmentLib;
import com.creativechasm.environment.api.plant.CropRegistry;
import com.creativechasm.environment.api.plant.ICrop;
import com.creativechasm.environment.api.tags.EnvirlibTags;
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

@Mod.EventBusSubscriber(modid = EnvironmentLib.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FertilizerHandler {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onBonemealUse(BonemealEvent event) {

        if (event.getBlock().getBlock() == Blocks.NETHER_WART) { //temporary fix to prevent bone meal particle spawning
            event.setCanceled(true);
        }

        //disable bone meal for compatible crops/plants
        Optional<ICrop> optionalICrop = CropRegistry.getInstance().get(event.getBlock().getBlock().getRegistryName());
        if (optionalICrop.isPresent()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onItemToolTip(ItemTooltipEvent event) {
        Item item = event.getItemStack().getItem();
        if(EnvirlibTags.FERTILIZER_GROUP.contains(item)) {  // add fertilizer info
            event.getToolTip().add(new StringTextComponent(""));
            String type = String.format("%s%s%s", EnvirlibTags.N_FERTILIZER.contains(item) ? "N" : "", EnvirlibTags.P_FERTILIZER.contains(item) ? "P" : "", EnvirlibTags.K_FERTILIZER.contains(item) ? "K" : "");
            event.getToolTip().add(new TranslationTextComponent("fertilizer.desc", type).applyTextStyle(TextFormatting.GRAY));
        }
    }
}
