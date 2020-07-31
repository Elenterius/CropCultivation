package com.creativechasm.cropcultivation.util;

import com.creativechasm.cropcultivation.CropCultivationMod;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;

public abstract class ModTags
{
    public static abstract class Items
    {
        public static final Tag<Item> COMPOST_MATERIAL = new ItemTags.Wrapper(new ResourceLocation(CropCultivationMod.MOD_ID, "compost"));

        public static final Tag<Item> FERTILIZER_GROUP = new ItemTags.Wrapper(new ResourceLocation(CropCultivationMod.MOD_ID, "fertilizer_group"));
        public static final Tag<Item> N_FERTILIZER = new ItemTags.Wrapper(new ResourceLocation(CropCultivationMod.MOD_ID, "n_fertilizer"));
        public static final Tag<Item> P_FERTILIZER = new ItemTags.Wrapper(new ResourceLocation(CropCultivationMod.MOD_ID, "p_fertilizer"));
        public static final Tag<Item> K_FERTILIZER = new ItemTags.Wrapper(new ResourceLocation(CropCultivationMod.MOD_ID, "k_fertilizer"));

        public static final Tag<Item> LIMING_MATERIAL = new ItemTags.Wrapper(new ResourceLocation(CropCultivationMod.MOD_ID, "liming_material"));
        public static final Tag<Item> ACIDIFYING_MATERIAL = new ItemTags.Wrapper(new ResourceLocation(CropCultivationMod.MOD_ID, "acidifying_material"));

        public static final Tag<Item> DEVICE = new ItemTags.Wrapper(new ResourceLocation(CropCultivationMod.MOD_ID, "device"));
    }

    public static abstract class Blocks
    {
        //use this tag to excluded your crop/growable from the modified crop growth, this does not prevent the consumption of nutrients
        public static final Tag<Block> USE_DEFAULT_GROWTH = new BlockTags.Wrapper(new ResourceLocation(CropCultivationMod.MOD_ID, "crops/default_growth"));
    }

    public static abstract class Forge
    {
        public static final Tag<Block> FARMLAND_BLOCK = new BlockTags.Wrapper(new ResourceLocation("forge", "farmland"));
        public static final Tag<Item> FARMLAND_ITEM = new ItemTags.Wrapper(new ResourceLocation("forge", "farmland"));
    }
}
