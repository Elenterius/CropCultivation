package com.creativechasm.cropcultivation.handler;

import com.creativechasm.cropcultivation.CropCultivationMod;
import com.creativechasm.cropcultivation.environment.plant.PlantMacronutrient;
import com.creativechasm.cropcultivation.init.ModItems;
import com.creativechasm.cropcultivation.init.ModTags;
import com.creativechasm.cropcultivation.util.BlockPropertyUtil;
import com.creativechasm.cropcultivation.util.GuiUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = CropCultivationMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public abstract class ToolTipHandler
{
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onItemToolTip(final ItemTooltipEvent event) {
        Item item = event.getItemStack().getItem();

        if (Tags.Items.SEEDS.contains(item) || Tags.Items.CROPS.contains(item)) {
            CompoundNBT nbtTag = event.getItemStack().getTag();
            if (nbtTag != null && nbtTag.contains("cropcultivation") && nbtTag.contains("BlockStateTag")) {
                CompoundNBT propertiesTag = nbtTag.getCompound("BlockStateTag");
                event.getToolTip().add(new StringTextComponent(""));
                event.getToolTip().add(new TranslationTextComponent("desc.cropcultivation.crop_traits").applyTextStyle(TextFormatting.GRAY));
                event.getToolTip().add(new StringTextComponent(" ").appendSibling(new TranslationTextComponent("desc.cropcultivation.yield_modifier", propertiesTag.getInt(BlockPropertyUtil.YIELD_MODIFIER.getName())).applyTextStyle(TextFormatting.GRAY)));
                event.getToolTip().add(new StringTextComponent(" ").appendSibling(new TranslationTextComponent("desc.cropcultivation.moisture_tolerance", propertiesTag.getInt(BlockPropertyUtil.MOISTURE_TOLERANCE.getName())).applyTextStyle(TextFormatting.GRAY)));
                event.getToolTip().add(new StringTextComponent(" ").appendSibling(new TranslationTextComponent("desc.cropcultivation.temperature_tolerance", propertiesTag.getInt(BlockPropertyUtil.TEMPERATURE_TOLERANCE.getName())).applyTextStyle(TextFormatting.GRAY)));
            }
        }

        if (ModTags.Items.FERTILIZER_GROUP.contains(item)) {  // add fertilizer info
            event.getToolTip().add(new StringTextComponent(""));
            event.getToolTip().add(new TranslationTextComponent("desc.cropcultivation.fertilizer").applyTextStyle(TextFormatting.GRAY));
            if (ModTags.Items.N_FERTILIZER.contains(item))
                event.getToolTip().add(new StringTextComponent(" ").appendSibling(new TranslationTextComponent("nutrient.cropcultivation.nitrogen").applyTextStyle(TextFormatting.GRAY)));
            if (ModTags.Items.P_FERTILIZER.contains(item))
                event.getToolTip().add(new StringTextComponent(" ").appendSibling(new TranslationTextComponent("nutrient.cropcultivation.phosphorus").applyTextStyle(TextFormatting.GRAY)));
            if (ModTags.Items.K_FERTILIZER.contains(item))
                event.getToolTip().add(new StringTextComponent(" ").appendSibling(new TranslationTextComponent("nutrient.cropcultivation.potassium").applyTextStyle(TextFormatting.GRAY)));
        }

        if ((item == ModItems.SOIL_TEST_KIT || item == ModItems.SOIL_METER) && event.getItemStack().hasTag() && event.getItemStack().getTag() != null) {
            CompoundNBT nbtTag = event.getItemStack().getTag();
            float pH = nbtTag.getFloat("pH");
            int nitrogen = nbtTag.getInt("N");
            float nPct = PlantMacronutrient.NITROGEN.getAvailabilityPctInSoil(pH);
            int phosphorus = nbtTag.getInt("P");
            float pPct = PlantMacronutrient.PHOSPHORUS.getAvailabilityPctInSoil(pH);
            int potassium = nbtTag.getInt("K");
            float kPct = PlantMacronutrient.POTASSIUM.getAvailabilityPctInSoil(pH);

            event.getToolTip().add(new StringTextComponent(GuiUtil.MAGIC_STRING));
            event.getToolTip().add(new TranslationTextComponent("nutrient.cropcultivation.nitrogen")
                    .appendSibling(new StringTextComponent(String.format(": %d%% x %.1f = ", nitrogen * 10, nPct)).applyTextStyle(TextFormatting.DARK_GRAY)
                            .appendSibling(new StringTextComponent(String.format("%.2f%%", nitrogen * nPct * 10f)).applyTextStyle(TextFormatting.GRAY))));
            event.getToolTip().add(new StringTextComponent(""));
            event.getToolTip().add(new StringTextComponent(""));
            event.getToolTip().add(new TranslationTextComponent("nutrient.cropcultivation.phosphorus")
                    .appendSibling(new StringTextComponent(String.format(": %d%% x %.1f = ", phosphorus * 10, pPct)).applyTextStyle(TextFormatting.DARK_GRAY)
                            .appendSibling(new StringTextComponent(String.format("%.2f%%", phosphorus * pPct * 10f)).applyTextStyle(TextFormatting.GRAY))));
            event.getToolTip().add(new StringTextComponent(""));
            event.getToolTip().add(new StringTextComponent(""));
            event.getToolTip().add(new TranslationTextComponent("nutrient.cropcultivation.potassium")
                    .appendSibling(new StringTextComponent(String.format(": %d%% x %.1f = ", potassium * 10, kPct)).applyTextStyle(TextFormatting.DARK_GRAY)
                            .appendSibling(new StringTextComponent(String.format("%.2f%%", potassium * kPct * 10f)).applyTextStyle(TextFormatting.GRAY))));
            event.getToolTip().add(new StringTextComponent(""));
            event.getToolTip().add(new StringTextComponent(""));
        }
    }

    @SubscribeEvent
    public static void onRenderTooltipColor(final RenderTooltipEvent.Color event) {
        ItemStack stack = event.getStack();
        if (stack != ItemStack.EMPTY && ModTags.Items.DEVICE.contains(stack.getItem())) {
            event.setBackground(0xED000000);
            int borderColorStart = 0xFFFFFFFF; //0x505000FF
            int borderColorEnd = (borderColorStart & 0xFEFEFE) >> 1 | borderColorStart & 0xFF000000;
            event.setBorderStart(borderColorStart);
            event.setBorderEnd(borderColorEnd);
        }
    }

    @SubscribeEvent
    public static void onRenderTooltip(final RenderTooltipEvent.Pre event) {
    }

    @SubscribeEvent
    public static void onRenderTooltip(final RenderTooltipEvent.PostBackground event) {

    }

    @SubscribeEvent
    public static void onRenderTooltip(final RenderTooltipEvent.PostText event) {
        ItemStack stack = event.getStack();
        if (!stack.isEmpty() && (stack.getItem() == ModItems.SOIL_TEST_KIT || stack.getItem() == ModItems.SOIL_METER) && stack.hasTag() && stack.getTag() != null) {
            int i = event.getLines().indexOf(GuiUtil.MAGIC_STRING);
            if (i > 0) {
                int maxWidth = 16 * (event.getWidth() / 16) + 1;
                int x = event.getX();
                int y = event.getY() + (i+3) * 10 - event.getFontRenderer().FONT_HEIGHT;

                CompoundNBT nbtTag = stack.getTag();
                float pH = nbtTag.getFloat("pH");
                int nitrogen = nbtTag.getInt("N");
                float nPct = PlantMacronutrient.NITROGEN.getAvailabilityPctInSoil(pH);
                int phosphorus = nbtTag.getInt("P");
                float pPct = PlantMacronutrient.PHOSPHORUS.getAvailabilityPctInSoil(pH);
                int potassium = nbtTag.getInt("K");
                float kPct = PlantMacronutrient.POTASSIUM.getAvailabilityPctInSoil(pH);

                GuiUtil.drawMeasureBar(x, y, maxWidth, nitrogen / 10f, nPct, 0xFFFF63FF, 0xFFB1B1B1);
                GuiUtil.drawMeasureBar(x, y + 28 + 2, maxWidth, phosphorus / 10f, pPct,0xFF63FFFF, 0xFFB1B1B1);
                GuiUtil.drawMeasureBar(x, y + 28 + 28 + 4, maxWidth, potassium / 10f, kPct, 0xFFFFFF63, 0xFFB1B1B1);
            }
        }
    }
}
