package com.creativechasm.cropcultivation.api.tags;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;

public class ForgeTags {
    public static final Tag<Block> FARMLAND_BLOCK = new BlockTags.Wrapper(new ResourceLocation("forge", "farmland"));
    public static final Tag<Item> FARMLAND_ITEM = new ItemTags.Wrapper(new ResourceLocation("forge", "farmland"));
}
