package com.creativechasm.cropcultivation.client.gui;

import com.creativechasm.cropcultivation.CropCultivationMod;
import com.creativechasm.cropcultivation.environment.ClimateUtil;
import com.creativechasm.cropcultivation.environment.plant.IPlantGrowthCA;
import com.creativechasm.cropcultivation.registry.ICropEntry;
import com.creativechasm.cropcultivation.util.BlockPropertyUtil;
import com.creativechasm.cropcultivation.util.MiscUtil;
import com.creativechasm.cropcultivation.util.TextUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropsBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.IntegerProperty;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CropInfo
{
    private Minecraft mc;

    private ICropEntry cachedCropEntry = null;
    boolean isValid = false;
    private String lastSearchStr = "";

    private String commonId;
    private BlockState[] cachedBlockstates = null;
    private final List<ITextComponent> cachedTextCompLines = new ArrayList<>();
    private ITextComponent cachedTextCompId;
    private ITextComponent cachedTextCompDataCompleteness;

    final List<ItemStack> itemDrops = new ArrayList<>();

    public CropInfo(Minecraft mc) {
        this.mc = mc;
    }

    private void invalidate() {
        isValid = false;
        cachedCropEntry = null;
        cachedBlockstates = new BlockState[]{Blocks.AIR.getDefaultState()};
        itemDrops.clear();
    }

    public void updateCropEntryByName(String searchStr) {
        if (lastSearchStr.equals(searchStr)) return;
        lastSearchStr = searchStr;

        if (searchStr.isEmpty()) {
            invalidate();
            return;
        }

        Optional<ICropEntry> anyMatch = CropCultivationMod.PROXY.getCropRegistry().findAnyBy(searchStr);
        ICropEntry foundEntry = anyMatch.orElse(null); //CropUtil.GENERIC_CROP

        if (cachedCropEntry == foundEntry) return;

        invalidate();
        cachedCropEntry = foundEntry;

        Optional<String> stringOptional = CropCultivationMod.PROXY.getCropRegistry().getCommonId(cachedCropEntry);
        if (stringOptional.isPresent()) {
            commonId = stringOptional.get();
            update();
            isValid = true;
        }
        else {
            commonId = "unknown";
        }
    }

    float getDataCompletenessPct() {
        return 1f;
    }

    public BlockState[] getGrowthBlockStates() {
        return cachedBlockstates;
    }

    public BlockState getGrowthBlockState(int age) {
        return cachedBlockstates[age];
    }

    public BlockState getMatureBlockState() {
        return cachedBlockstates[cachedBlockstates.length - 1];
    }

    public ITextComponent getCommonId() {
        return cachedCropEntry != null ? cachedTextCompId : TextUtil.EMPTY_STRING;
    }

    public List<ITextComponent> getTextLines() {
        return cachedCropEntry != null ? cachedTextCompLines : TextUtil.EMPTY_LINES;
    }

    public ITextComponent getDataCompleteness() {
        return cachedCropEntry != null ? cachedTextCompDataCompleteness : TextUtil.EMPTY_STRING;
    }

    private void update() {
        cachedTextCompId = new StringTextComponent("ID/").appendSibling(new StringTextComponent(commonId).appendText("/").applyTextStyle(TextFormatting.GRAY));

        cachedTextCompDataCompleteness = new TranslationTextComponent("gui.cropcultivation.data_completeness", getDataCompletenessPct() * 100f);

        cachedTextCompLines.clear();
        List<ResourceLocation> registeredModBlocks = CropCultivationMod.PROXY.getCropRegistry().getModsFor(commonId);
        if (registeredModBlocks.size() > 0) {
            ResourceLocation blockEntry = registeredModBlocks.get(0);
            String translationKey = "block." + blockEntry.toString().replace(":", ".");
            cachedTextCompLines.add(new TranslationTextComponent("gui.cropcultivation.name").appendText(": ").appendSibling(new TranslationTextComponent(translationKey).applyTextStyle(TextFormatting.GRAY)));

            if (ForgeRegistries.BLOCKS.containsKey(blockEntry)) {
                Block block = ForgeRegistries.BLOCKS.getValue(blockEntry);
                if (block != null) {
                    BlockState defaultState = block.getDefaultState();

                    if (mc.player != null) {
                        ItemStack stack = block.getItem(mc.player.world, BlockPos.ZERO, defaultState);
                        if (!stack.isEmpty()) {
                            Item item = stack.getItem();
                            stack.setCount(1);
                            if (Tags.Items.CROPS.contains(item)) itemDrops.add(stack);
                            if (Tags.Items.SEEDS.contains(item)) itemDrops.add(stack);
                        }
                    }
                    if (block instanceof CropsBlock) {
                        Item item = MiscUtil.getSeedItem((CropsBlock) block);
                        if (item != null && item != Items.AIR) {
                            if (Tags.Items.CROPS.contains(item)) itemDrops.add(new ItemStack(item));
                            if (Tags.Items.SEEDS.contains(item)) itemDrops.add(new ItemStack(item));
                        }
                    }
                    List<ItemStack> filtered = itemDrops.stream().map(ItemStack::getItem).distinct().map(ItemStack::new).collect(Collectors.toList());
                    itemDrops.clear();
                    itemDrops.addAll(filtered);

                    Optional<IntegerProperty> ageProperty = BlockPropertyUtil.getAgeProperty(defaultState);
                    if (ageProperty.isPresent()) {
                        int maxAge = BlockPropertyUtil.getMaxAge(ageProperty.get());
                        cachedBlockstates = new BlockState[maxAge + 1];
                        for (int age = 0; age < maxAge + 1; age++) {
                            cachedBlockstates[age] = block.getDefaultState().with(ageProperty.get(), age);
                        }
                        cachedTextCompLines.add(new TranslationTextComponent("gui.cropcultivation.age").appendText(": ").appendSibling(new StringTextComponent("0-" + maxAge).applyTextStyle(TextFormatting.GRAY)));
                        cachedTextCompLines.add(TextUtil.EMPTY_STRING);
                        cachedTextCompLines.add(TextUtil.EMPTY_STRING);
                    }
                    else {
                        cachedBlockstates = new BlockState[]{defaultState};
                    }
                }
            }
        }
        else {
            cachedTextCompLines.add(new TranslationTextComponent("gui.cropcultivation.name").appendText(": ").appendSibling(new StringTextComponent("N/A").applyTextStyle(TextFormatting.GRAY)));
            cachedTextCompLines.add(new TranslationTextComponent("gui.cropcultivation.age").appendText(": ").appendSibling(new StringTextComponent("N/A").applyTextStyle(TextFormatting.GRAY)));
            cachedTextCompLines.add(TextUtil.EMPTY_STRING);
            cachedTextCompLines.add(TextUtil.EMPTY_STRING);
        }

        cachedTextCompLines.add(TextUtil.EMPTY_STRING);
        cachedTextCompLines.add(new TranslationTextComponent("gui.cropcultivation.macronutrients").appendText(":"));
        cachedTextCompLines.add(TextUtil.insetTextComponent(new TranslationTextComponent("nutrient.cropcultivation.nitrogen")
                .appendSibling(new StringTextComponent(String.format(": %.0f%%", cachedCropEntry.getNitrogenNeed() * 100f)).applyTextStyle(TextFormatting.GRAY))));
        cachedTextCompLines.add(TextUtil.insetTextComponent(new TranslationTextComponent("nutrient.cropcultivation.phosphorus")
                .appendSibling(new StringTextComponent(String.format(": %.0f%%", cachedCropEntry.getPhosphorusNeed() * 100f)).applyTextStyle(TextFormatting.GRAY))));
        cachedTextCompLines.add(TextUtil.insetTextComponent(new TranslationTextComponent("nutrient.cropcultivation.potassium")
                .appendSibling(new StringTextComponent(String.format(": %.0f%%", cachedCropEntry.getPotassiumNeed() * 100f)).applyTextStyle(TextFormatting.GRAY))));

        cachedTextCompLines.add(TextUtil.EMPTY_STRING);
        cachedTextCompLines.add(new TranslationTextComponent("measurement.cropcultivation.temperature", String.format("%.2f°C - %.2f°C", ClimateUtil.convertTemperatureMCToCelsius(cachedCropEntry.getMinTemperature()), ClimateUtil.convertTemperatureMCToCelsius(cachedCropEntry.getMaxTemperature()))).applyTextStyle(TextFormatting.GRAY));

        cachedTextCompLines.add(TextUtil.EMPTY_STRING);
        cachedTextCompLines.add(new StringTextComponent("Soil"));
        cachedTextCompLines.add(new TranslationTextComponent("measurement.cropcultivation.soil_moisture", String.format("%.0f%% - %.0f%%", cachedCropEntry.getMinSoilMoisture() * 100f, cachedCropEntry.getMaxSoilMoisture() * 100f)).applyTextStyle(TextFormatting.GRAY));
        cachedTextCompLines.add(new TranslationTextComponent("measurement.cropcultivation.soil_ph", String.format("%s - %s", cachedCropEntry.getMinSoilPH(), cachedCropEntry.getMaxSoilPH())).applyTextStyle(TextFormatting.GRAY));

        if (cachedCropEntry instanceof IPlantGrowthCA) {
            cachedTextCompLines.add(TextUtil.EMPTY_STRING);
            cachedTextCompLines.add(new StringTextComponent("Neighborhood"));
            cachedTextCompLines.add(new StringTextComponent(String.format("Metric: %s", ((IPlantGrowthCA) cachedCropEntry).getNeighborhood())));
            cachedTextCompLines.add(new StringTextComponent(String.format("Population: %s - %s", ((IPlantGrowthCA) cachedCropEntry).getMinPlantNeighbors(), ((IPlantGrowthCA) cachedCropEntry).getMaxPlantNeighbors())));
        }

        if (Minecraft.getInstance().gameSettings.advancedItemTooltips) {
            cachedTextCompLines.add(TextUtil.EMPTY_STRING);
            cachedTextCompLines.add(new StringTextComponent("[DEBUG] blocks: ").appendSibling(new StringTextComponent(registeredModBlocks.toString()).applyTextStyle(TextFormatting.GRAY)));
        }
    }
}
