package com.creativechasm.cropcultivation.client.gui;

import com.creativechasm.cropcultivation.CropCultivationMod;
import com.creativechasm.cropcultivation.environment.ClimateUtil;
import com.creativechasm.cropcultivation.environment.CropUtil;
import com.creativechasm.cropcultivation.environment.plant.IPlantGrowthCA;
import com.creativechasm.cropcultivation.init.CommonRegistry;
import com.creativechasm.cropcultivation.registry.ICropEntry;
import com.creativechasm.cropcultivation.util.BlockPropertyUtil;
import com.creativechasm.cropcultivation.util.GuiUtil;
import com.creativechasm.cropcultivation.util.TextUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.state.IntegerProperty;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class TabletScreen extends Screen
{
    public static final ResourceLocation TEXTURE = new ResourceLocation(CropCultivationMod.MOD_ID, "textures/gui/os_window.png");

    private final int xSize;
    private final int ySize;

    private TextFieldWidget searchField;
    private static String lastSearch = "";

    private ICropEntry cachedCropEntry = null;
    @Nullable
    private static BlockState cachedBlockstate = null;
    private ICropEntry currCropEntry = null;
    private List<ITextComponent> cachedTextLines = new ArrayList<>();
    private ITextComponent cachedTitle = TextUtil.EMPTY_STRING;

    public TabletScreen(Minecraft minecraft) {
        super(NarratorChatListener.EMPTY);
        this.minecraft = minecraft;
        xSize = 175;
        ySize = 229;
        cachedCropEntry = CropUtil.GENERIC_CROP;
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

        currCropEntry = CropUtil.GENERIC_CROP;
        updateSearchResults();
    }

    @Override
    public void removed() {
        getMinecraft().keyboardListener.enableRepeatEvents(false);
        if (searchField != null) {
            lastSearch = searchField.getText();
        }
    }

    @Override
    public void tick() {
        if (searchField != null) {
            searchField.tick();
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
        getMinecraft().getTextureManager().bindTexture(TEXTURE);
        blit(x, y, 0, 0, xSize, ySize);

        searchField.render(mouseX, mouseY, partialTicks);
    }

    protected void drawForegroundLayer(int x, int y, int mouseX, int mouseY, float partialTicks) {
        if (cachedCropEntry != currCropEntry) {
            cachedBlockstate = null;
            cachedTitle = CropInfo.getEntryTitle(currCropEntry);
            cachedTextLines.clear();
            CropInfo.getTextLines(currCropEntry, cachedTextLines);
        }
        cachedCropEntry = currCropEntry;

        if (cachedBlockstate != null) {
//            setBlitOffset(100);
            GuiUtil.renderBlockStateIntoGUI(cachedBlockstate, x + 7, y + 17);
//            itemRenderer.renderItemIntoGUI(cachedBlockstate, x + 7, y + 19);
//            setBlitOffset(0);
        }

        font.drawString(cachedTitle.getFormattedText(), x + 42f, y + 19f, 0xFFFFFFFF);
//        hLine(x + 42, x + 175 - 4, y + 19 + 9,0xFFFFFFFF);

        float pct = 0.8f;
        font.drawString(String.format("Data Completeness: %.1f%%", pct * 100f), x + 42f, y + 19f + 13f, 0xFFFFFFFF);
        GuiUtil.drawProgressBar(x + 42, y + 19 + 23, (x + xSize - 4) - (x + 42), pct, 0xFFFFFFFF, 0xFFB1B1B1);

        int lines = Math.min((ySize - 40) / 9, cachedTextLines.size());
        for (int idx = 0; idx < lines; ++idx) {
            ITextComponent textComponent = cachedTextLines.get(idx);
            font.drawString(textComponent.getFormattedText(), x + 7f, y + 48f + idx * 9, 0xFFFFFFFF);
        }
    }

    private void updateSearchResults() {
        if (!searchField.getText().isEmpty()) {
            String searchTerm = searchField.getText().toLowerCase(Locale.ROOT);
            Optional<ICropEntry> anyMatch = CommonRegistry.getCropRegistry().findAnyBy(searchTerm);
            currCropEntry = anyMatch.orElse(CropUtil.GENERIC_CROP);
        }
    }

    public abstract static class CropInfo
    {
        public static ITextComponent getEntryTitle(ICropEntry cropEntry) {
            if (cropEntry == null) return TextUtil.EMPTY_STRING;
            Optional<String> commonId = CommonRegistry.getCropRegistry().getCommonId(cropEntry);
            return new StringTextComponent("ID/").appendSibling(new StringTextComponent(commonId.orElse("unknown")).appendText("/").applyTextStyle(TextFormatting.GRAY));
        }

        public static void getTextLines(ICropEntry cropEntry, List<ITextComponent> lines) {
            if (cropEntry == null) return;

            Optional<String> commonId = CommonRegistry.getCropRegistry().getCommonId(cropEntry);
            List<ResourceLocation> registeredModBlocks = CommonRegistry.getCropRegistry().getModsFor(commonId.orElse(null));
            String translationKey = "block.minecraft.air";
            if (registeredModBlocks.size() > 0) {
                ResourceLocation blockEntry = registeredModBlocks.get(0);
                translationKey = "block." + blockEntry.toString().replace(":", ".");

                lines.add(TextUtil.EMPTY_STRING);
                lines.add(new StringTextComponent("Name: ").appendSibling(new TranslationTextComponent(translationKey).applyTextStyle(TextFormatting.GRAY)));

                if (ForgeRegistries.BLOCKS.containsKey(blockEntry)) {
                    Block block = ForgeRegistries.BLOCKS.getValue(blockEntry);
                    if (block != null) {
                        cachedBlockstate = block.getDefaultState();
                        Optional<IntegerProperty> ageProperty = BlockPropertyUtil.getAgeProperty(cachedBlockstate);
                        if (ageProperty.isPresent()) {
                            int maxAge = BlockPropertyUtil.getMaxAge(ageProperty.get());
                            cachedBlockstate = cachedBlockstate.with(ageProperty.get(), maxAge);
                            lines.add(new StringTextComponent("Age: ").appendSibling(new StringTextComponent("0 - " + maxAge).applyTextStyle(TextFormatting.GRAY)));
                        }
                    }
                    else cachedBlockstate = null;
                }
            }
            else {
                lines.add(TextUtil.EMPTY_STRING);
                lines.add(new StringTextComponent("Name: ").appendSibling(new TranslationTextComponent(translationKey).applyTextStyle(TextFormatting.GRAY)));
            }

            lines.add(TextUtil.EMPTY_STRING);
            lines.add(new StringTextComponent("Registered: ").appendSibling(new StringTextComponent(registeredModBlocks.toString()).applyTextStyle(TextFormatting.GRAY)));

            lines.add(TextUtil.EMPTY_STRING);
            lines.add(new StringTextComponent("Needs"));
            lines.add(new StringTextComponent(String.format("Nitrogen: %s%%", (int) (cropEntry.getNitrogenNeed() * 100))).applyTextStyle(TextFormatting.GRAY));
            lines.add(new StringTextComponent(String.format("Phosphorus: %s%%", (int) (cropEntry.getPhosphorusNeed() * 100))).applyTextStyle(TextFormatting.GRAY));
            lines.add(new StringTextComponent(String.format("Potassium: %s%%", (int) (cropEntry.getPotassiumNeed() * 100))).applyTextStyle(TextFormatting.GRAY));
            lines.add(TextUtil.EMPTY_STRING);
            lines.add(new StringTextComponent("Tolerance"));
            lines.add(new StringTextComponent(String.format("Moisture: %s - %s", cropEntry.getMinSoilMoisture(), cropEntry.getMaxSoilMoisture())).applyTextStyle(TextFormatting.GRAY));
            lines.add(new StringTextComponent(String.format("pH: %s - %s", cropEntry.getMinSoilPH(), cropEntry.getMaxSoilPH())).applyTextStyle(TextFormatting.GRAY));
            lines.add(new StringTextComponent(String.format("Temperature: %.2f°C - %.2f°C (%.2f - %.2f)", ClimateUtil.convertTemperatureMCToCelsius(cropEntry.getMinTemperature()), ClimateUtil.convertTemperatureMCToCelsius(cropEntry.getMaxTemperature()), cropEntry.getMinTemperature(), cropEntry.getMaxTemperature())).applyTextStyle(TextFormatting.GRAY));

            if (cropEntry instanceof IPlantGrowthCA) {
                lines.add(TextUtil.EMPTY_STRING);
                lines.add(new StringTextComponent("Neighborhood"));
                lines.add(new StringTextComponent(String.format("Metric: %s", ((IPlantGrowthCA) cropEntry).getNeighborhood())));
                lines.add(new StringTextComponent(String.format("Population: %s - %s", ((IPlantGrowthCA) cropEntry).getMinPlantNeighbors(), ((IPlantGrowthCA) cropEntry).getMaxPlantNeighbors())));
            }
        }
    }
}
