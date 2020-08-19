package com.creativechasm.cropcultivation.util;

import com.creativechasm.cropcultivation.CropCultivationMod;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class GuiUtil
{
    public static final String MAGIC_STRING = "\u0020\u00a7" + 'r' + "\u0020\u0020";
    public static final ResourceLocation TEXTURE_MEASURE_BAR = new ResourceLocation(CropCultivationMod.MOD_ID, "textures/gui/measure_bar.png");

    public static void drawMeasureBar(int x, int y, int width, float pct, int colorAlpha) {
        int maxWidth = 16 * (width / 16 - 2) + 1;

        RenderSystem.color3f(1f, 1f, 1f);
        Minecraft.getInstance().getTextureManager().bindTexture(GuiUtil.TEXTURE_MEASURE_BAR);
        AbstractGui.blit(x, y, 0, 0, maxWidth, 16, 16, 16);

        if (pct > 0f) {
            AbstractGui.fill(x, y + 2, x + (int) (pct * maxWidth), y + 9, colorAlpha);
        }
    }

    public static void drawMeasureBar(int x, int y, int width, float pct, float pct2, int colorAlpha, int colorAlpha2) {
        int maxWidth = 16 * (width / 16 - 2) + 1;

        RenderSystem.color3f(1f, 1f, 1f);
        Minecraft.getInstance().getTextureManager().bindTexture(GuiUtil.TEXTURE_MEASURE_BAR);
        AbstractGui.blit(x, y, 0, 0, maxWidth, 16, 16, 16);

        if (pct * pct2 > 0f) {
            AbstractGui.fill(x, y + 2, x + (int) (pct * pct2 * maxWidth), y + 9, colorAlpha);
        }
        if (pct2 > 0f && pct2 < 1f) {
            AbstractGui.fill(x + (int) (pct * pct2 * maxWidth), y + 2, x + (int) (pct * maxWidth), y + 9, colorAlpha2);
        }
    }

    public static void drawBarChart(int x, int y, int width, int height, float[] pct, int[] colorAlpha) {

    }

    public static void drawLineChart(int x, int y, int width, int height, float[] xValues, float[] yValues, int[] colorAlpha) {

    }

    public static void hLine(int x, int y, int x2, int colorAlpha) {
        if (x2 < x) {
            int i = x; x = x2; x2 = i;
        }
        AbstractGui.fill(x, y, x2 + 1, y + 1, colorAlpha);
    }

    public static void vLine(int x, int y, int y2, int colorAlpha) {
        if (y2 < y) {
            int i = y; y = y2; y2 = i;
        }
        AbstractGui.fill(x, y + 1, x + 1, y2, colorAlpha);
    }
}
