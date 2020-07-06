package com.creativechasm.environment.api.tags;

import com.creativechasm.environment.EnvironmentLib;
import net.minecraft.item.Item;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;

public class EnvirlibTags {
    public static final Tag<Item> COMPOST_ITEM = new ItemTags.Wrapper(new ResourceLocation(EnvironmentLib.MOD_ID, "compost"));
    public static final Tag<Item> FERTILIZER_ITEM = new ItemTags.Wrapper(new ResourceLocation(EnvironmentLib.MOD_ID, "fertilizer"));
}
