package com.creativechasm.cropcultivation.client.gui;

import com.creativechasm.cropcultivation.CropCultivationMod;
import com.creativechasm.cropcultivation.client.util.ColorScheme;
import com.creativechasm.cropcultivation.client.util.GuiUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TabletScreen extends Screen
{
    public static final ResourceLocation TEXTURE = new ResourceLocation(CropCultivationMod.MOD_ID, "textures/gui/os_agrinet_screen.png");
    public static final ResourceLocation TEXTURE_LOADING_VIEW = new ResourceLocation(CropCultivationMod.MOD_ID, "textures/gui/os_loading_screen.png");

    private final int xSize;
    private final int ySize;

    private TextFieldWidget searchField;
    private static String lastSearch = "";
    private final CropInfo cropInfo;

    private float progress = 0f;
    private List<String> loadingTextLines;

    public TabletScreen(Minecraft minecraft) {
        super(NarratorChatListener.EMPTY);
        this.minecraft = minecraft;
        xSize = 175;
        ySize = 229;
        cropInfo = new CropInfo(minecraft);
        loadingTextLines = new ArrayList<>();
        loadingTextLines.add("fetching data");
    }

    @Override
    protected void init() {
        getMinecraft().keyboardListener.enableRepeatEvents(true);
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        searchField = new TextFieldWidget(font, x - 84, y + 20, 80, 20, I18n.format("itemGroup.search"));
        searchField.setVisible(true);
        searchField.setSuggestion("search");
        searchField.setMaxStringLength(50);
        searchField.setFocused2(true);
//        searchField.setEnableBackgroundDrawing(false);
        searchField.setTextColor(0xffffff);
        searchField.setResponder(value -> {
            if (value.isEmpty()) searchField.setSuggestion("search");
            else searchField.setSuggestion(null);
        });

        addButton(new Button(x - 70 - 3, y + 44, 70, 20, I18n.format("gui.cancel"), (button) -> {
            searchField.setText("");
            updateSearchResults();
        }));

        searchField.setText(lastSearch);
        children.add(searchField);
        setFocusedDefault(searchField);
        updateSearchResults();
    }

    @Override
    public void removed() {
        getMinecraft().keyboardListener.enableRepeatEvents(false);
        if (searchField != null) {
            lastSearch = searchField.getText();
        }
    }

    private void updateSearchResults() {
        cropInfo.updateCropEntryByName(searchField.getText().toLowerCase(Locale.ROOT).trim());
    }

    @Override
    public void tick() {
        if (searchField != null) {
            searchField.tick();

            if (!cropInfo.isValid) {
                progress += 0.1f;
                if (progress > 1f) {
                    progress = 0;
                    if (minecraft != null) {
                        String loadingText = minecraft.getSplashes().getSplashText();
                        loadingTextLines = font.listFormattedStringToWidth(loadingText != null ? loadingText : "fetching data", (int) (xSize * 0.8f));
                    }
                }
            }
            else progress = 0f;
        }
    }

    @Override
    public boolean charTyped(char typedChar, int i) {
        String textValue = searchField.getText();
        if (searchField.charTyped(typedChar, i)) {
            if (!textValue.equals(searchField.getText())) {
                updateSearchResults();
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int keyPressed2, int keyPressed3) {
        String textValue = searchField.getText();
        if (searchField.keyPressed(keyCode, keyPressed2, keyPressed3)) {
            if (!textValue.equals(searchField.getText())) {
                updateSearchResults();
            }
            return true;
        }
        else {
            return (searchField.isFocused() && searchField.getVisible() && (keyCode != 256)) || super.keyPressed(keyCode, keyPressed2, keyPressed3);
        }
    }

    @Override
    public void resize(Minecraft mc, int width, int height) {
        String textValue = searchField.getText();
        super.resize(mc, width, height);
        searchField.setText(textValue);
        updateSearchResults();
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        renderBackground();
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        drawBackgroundLayer(x, y, mouseX, mouseY, partialTicks);
        drawForegroundLayer(x, y, mouseX, mouseY, partialTicks);
        super.render(mouseX, mouseY, partialTicks);
    }

    protected void drawBackgroundLayer(int x, int y, int mouseX, int mouseY, float partialTicks) {
        RenderSystem.color4f(1F, 1F, 1F, 1F);
        getMinecraft().getTextureManager().bindTexture(cropInfo.isValid ? TEXTURE : TEXTURE_LOADING_VIEW);
        blit(x, y, 0, 0, xSize, ySize);

        searchField.render(mouseX, mouseY, partialTicks);
    }

    protected void drawForegroundLayer(int x, int y, int mouseX, int mouseY, float partialTicks) {
        if (!cropInfo.isValid) {
            int width = (int) (xSize * 0.8f);
            GuiUtil.drawProgressBar(x + (xSize - width), y + ySize/2 + 8, width - (xSize-width), progress, ColorScheme.SECONDARY_COLOR, ColorScheme.BACKGROUND_COLOR_LIGHT_GREY);

            for (int idx = 0; idx < loadingTextLines.size(); ++idx) {
                String line = loadingTextLines.get(idx);
                font.drawString(line, x + xSize/2f - font.getStringWidth(line) / 2f, y + ySize/2f + 9 + 12 + idx * (9+2), ColorScheme.PRIMARY_COLOR);
            }

            return;
        }

//            setBlitOffset(100);
        GuiUtil.renderBlockStateIntoGUI(cropInfo.getMatureBlockState(), x + 7, y + 17, 32f);
//            itemRenderer.renderItemIntoGUI(cachedBlockstate, x + 7, y + 19);
//            setBlitOffset(0);

//            MatrixStack matrixstack = new MatrixStack();
//            matrixstack.translate(0.0D, 0.0D, 300D);
//            matrixstack.scale(0.75f, 0.75f, 0f);
        for (int i = 0; i < cropInfo.getGrowthBlockStates().length; i++) {
            GuiUtil.renderBlockStateIntoGUI(cropInfo.getGrowthBlockState(i), x + 7 + i * 20, y + 57 + 18 + 3, 16f);
//                font.drawString(i + "", x + 6 + i * 20, y + 57 + 18 + 3, ColorScheme.PRIMARY_COLOR);

//                String str = i + "";
//                IRenderTypeBuffer.Impl renderTypeBuffer = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());
//                font.renderString(str, x + 6 + i * 20 + 16 - font.getStringWidth(str) + 3, y + 57 + 18 + 3, ColorScheme.PRIMARY_COLOR, true, matrixstack.getLast().getMatrix(), renderTypeBuffer, false, 0, 0xf000f0);
//                renderTypeBuffer.finish();

            fill(x + 6 + i * 20, y + 57 + 18 + 20, x + 7 + i * 20 + 16 + 1, y + 57 + 18 + 20 + 1, 0xFF5E4B39);
        }

        font.drawString(cropInfo.getCommonId().getFormattedText(), x + 42f, y + 19f, ColorScheme.ACCENT_COLOR);
//        hLine(x + 42, x + 175 - 4, y + 19 + 9,0xFFFFFFFF);

        float pct = cropInfo.getDataCompletnessPct();
        font.drawString(cropInfo.getDataCompleteness().getFormattedText(), x + 42f, y + 19f + 13f, ColorScheme.ACCENT_COLOR);
        GuiUtil.drawProgressBar(x + 42, y + 19 + 23, (x + xSize - 4) - (x + 42), pct, ColorScheme.SECONDARY_COLOR, ColorScheme.BACKGROUND_COLOR_LIGHT_GREY);

        List<ItemStack> itemDrops = cropInfo.itemDrops;
        for (int i = 0, itemDropsSize = itemDrops.size(); i < itemDropsSize; i++) {
            itemRenderer.renderItemIntoGUI(itemDrops.get(i), x + xSize/2 + i * 20, y + 57);
        }

        int lines = Math.min((ySize - 40) / 9, cropInfo.getTextLines().size());
        for (int idx = 0; idx < lines; ++idx) {
            ITextComponent textComponent = cropInfo.getTextLines().get(idx);
            font.drawString(textComponent.getFormattedText(), x + 7f, y + 57f + idx * 9, ColorScheme.SECONDARY_COLOR);
        }
    }

}
