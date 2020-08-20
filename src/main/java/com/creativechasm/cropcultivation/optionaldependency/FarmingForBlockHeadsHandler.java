package com.creativechasm.cropcultivation.optionaldependency;

import com.creativechasm.cropcultivation.CropCultivationMod;
import com.creativechasm.cropcultivation.init.ModItems;
import net.blay09.mods.farmingforblockheads.api.FarmingForBlockheadsAPI;
import net.blay09.mods.farmingforblockheads.api.IMarketCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.MarkerManager;

public final class FarmingForBlockHeadsHandler implements IOptionalModHandler
{
    @Override
    public void onSetup() {
        CropCultivationMod.LOGGER.info(MarkerManager.getMarker("ModCompat"), "setting up mod compatibility for <Farming For Blockheads>...");
//        register();
    }

    private void register() {
        //this whole thing doesn't work. wtf is going on ffs?? but the json datapack works... -.-

        ResourceLocation fertilizer_category = new ResourceLocation(CropCultivationMod.MOD_ID, "fertilizers");
        IMarketCategory marketCategory = FarmingForBlockheadsAPI.registerMarketCategoryAndReturn(fertilizer_category, "crop_reader.desc", new ItemStack(ModItems.NPK_FERTILIZER), 1);

        FarmingForBlockheadsAPI.registerMarketEntry(new ItemStack(ModItems.COMPOST, 3), new ItemStack(net.minecraft.item.Items.EMERALD, 1), FarmingForBlockheadsAPI.getMarketCategoryOther());

        FarmingForBlockheadsAPI.registerMarketEntry(new ItemStack(ModItems.COMPOST, 3), new ItemStack(net.minecraft.item.Items.EMERALD, 1), marketCategory);

        FarmingForBlockheadsAPI.registerMarketEntry(new ItemStack(ModItems.LIME_DUST, 3), new ItemStack(net.minecraft.item.Items.EMERALD, 1), marketCategory);
        FarmingForBlockheadsAPI.registerMarketEntry(new ItemStack(ModItems.WOOD_ASH, 3), new ItemStack(net.minecraft.item.Items.EMERALD, 1), marketCategory);
        FarmingForBlockheadsAPI.registerMarketEntry(new ItemStack(ModItems.FEATHER_MEAL, 3), new ItemStack(net.minecraft.item.Items.EMERALD, 1), marketCategory);
        FarmingForBlockheadsAPI.registerMarketEntry(new ItemStack(ModItems.SEAWEED_MEAL, 3), new ItemStack(net.minecraft.item.Items.EMERALD, 2), marketCategory);
        FarmingForBlockheadsAPI.registerMarketEntry(new ItemStack(ModItems.FISH_MEAL, 3), new ItemStack(net.minecraft.item.Items.EMERALD, 2), marketCategory);
        FarmingForBlockheadsAPI.registerMarketEntry(new ItemStack(ModItems.NPK_FERTILIZER, 3), new ItemStack(net.minecraft.item.Items.EMERALD, 9), marketCategory);

        if (OptionalRegistry.Items.SOYBEAN_MEAL != null) {
            FarmingForBlockheadsAPI.registerMarketEntry(new ItemStack(OptionalRegistry.Items.SOYBEAN_MEAL, 3), new ItemStack(net.minecraft.item.Items.EMERALD, 2), marketCategory);
        }
    }
}
