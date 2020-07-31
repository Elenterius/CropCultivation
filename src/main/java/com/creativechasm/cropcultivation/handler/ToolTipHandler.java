package com.creativechasm.cropcultivation.handler;

import com.creativechasm.cropcultivation.CropCultivationMod;
import com.creativechasm.cropcultivation.util.ModTags;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = CropCultivationMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public abstract class ToolTipHandler
{
    @SubscribeEvent
    public static void onItemToolTip(ItemTooltipEvent event) {
        Item item = event.getItemStack().getItem();
        if (ModTags.Items.FERTILIZER_GROUP.contains(item)) {  // add fertilizer info
            event.getToolTip().add(new StringTextComponent(""));
            event.getToolTip().add(new TranslationTextComponent("fertilizer.desc").applyTextStyle(TextFormatting.GRAY));
            if (ModTags.Items.N_FERTILIZER.contains(item)) event.getToolTip().add(new StringTextComponent(" ").appendSibling(new TranslationTextComponent("nutrient.nitrogen").applyTextStyle(TextFormatting.GRAY)));
            if (ModTags.Items.P_FERTILIZER.contains(item)) event.getToolTip().add(new StringTextComponent(" ").appendSibling(new TranslationTextComponent("nutrient.phosphorus").applyTextStyle(TextFormatting.GRAY)));
            if (ModTags.Items.K_FERTILIZER.contains(item)) event.getToolTip().add(new StringTextComponent(" ").appendSibling(new TranslationTextComponent("nutrient.potassium").applyTextStyle(TextFormatting.GRAY)));
        }
    }

    @SubscribeEvent
    public static void onRenderTooltipColor(RenderTooltipEvent.Color event)
    {
        ItemStack stack = event.getStack();
        if (stack != ItemStack.EMPTY && ModTags.Items.DEVICE.contains(stack.getItem()))
        {
            event.setBackground(0xED000000);
            int borderColorStart = 0xFFFFFFFF; //0x505000FF
            int borderColorEnd = (borderColorStart & 0xFEFEFE) >> 1 | borderColorStart & 0xFF000000;
            event.setBorderStart(borderColorStart);
            event.setBorderEnd(borderColorEnd);
        }
    }
}
