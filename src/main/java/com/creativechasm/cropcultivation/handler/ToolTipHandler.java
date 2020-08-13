package com.creativechasm.cropcultivation.handler;

import com.creativechasm.cropcultivation.CropCultivationMod;
import com.creativechasm.cropcultivation.util.BlockPropertyUtil;
import com.creativechasm.cropcultivation.util.ModTags;
import net.minecraft.block.CropsBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
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
    public static void onItemToolTip(final ItemTooltipEvent event) {
        Item item = event.getItemStack().getItem();

        if (item instanceof BlockItem && ((BlockItem) item).getBlock() instanceof CropsBlock) {
            CompoundNBT nbtTag = event.getItemStack().getTag();
            if (nbtTag != null && nbtTag.contains("BlockStateTag")) {
                CompoundNBT propertiesTag = nbtTag.getCompound("BlockStateTag");
                event.getToolTip().add(new StringTextComponent(""));
                event.getToolTip().add(new TranslationTextComponent("desc.cropcultivation.crop_traits").applyTextStyle(TextFormatting.GRAY));
                event.getToolTip().add(new StringTextComponent(String.format(" Yield Modifier: %s", propertiesTag.getInt(BlockPropertyUtil.YIELD_MODIFIER.getName()))).applyTextStyle(TextFormatting.GRAY));
                event.getToolTip().add(new StringTextComponent(String.format(" Moisture Tolerance: %s", propertiesTag.getInt(BlockPropertyUtil.MOISTURE_TOLERANCE.getName()))).applyTextStyle(TextFormatting.GRAY));
                event.getToolTip().add(new StringTextComponent(String.format(" Temperature Tolerance: %s", propertiesTag.getInt(BlockPropertyUtil.TEMPERATURE_TOLERANCE.getName()))).applyTextStyle(TextFormatting.GRAY));
            }
        }

        if (ModTags.Items.FERTILIZER_GROUP.contains(item)) {  // add fertilizer info
            event.getToolTip().add(new StringTextComponent(""));
            event.getToolTip().add(new TranslationTextComponent("desc.cropcultivation.fertilizer").applyTextStyle(TextFormatting.GRAY));
            if (ModTags.Items.N_FERTILIZER.contains(item)) event.getToolTip().add(new StringTextComponent(" ").appendSibling(new TranslationTextComponent("nutrient.cropcultivation.nitrogen").applyTextStyle(TextFormatting.GRAY)));
            if (ModTags.Items.P_FERTILIZER.contains(item)) event.getToolTip().add(new StringTextComponent(" ").appendSibling(new TranslationTextComponent("nutrient.cropcultivation.phosphorus").applyTextStyle(TextFormatting.GRAY)));
            if (ModTags.Items.K_FERTILIZER.contains(item)) event.getToolTip().add(new StringTextComponent(" ").appendSibling(new TranslationTextComponent("nutrient.cropcultivation.potassium").applyTextStyle(TextFormatting.GRAY)));
        }
    }

    @SubscribeEvent
    public static void onRenderTooltipColor(final RenderTooltipEvent.Color event)
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
