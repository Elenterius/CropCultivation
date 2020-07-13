package com.creativechasm.environment.api.tags;

import com.creativechasm.environment.EnvironmentLib;
import net.minecraft.item.Item;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;

public class EnvirlibTags {
    public static final Tag<Item> COMPOST_MATERIAL = new ItemTags.Wrapper(new ResourceLocation(EnvironmentLib.MOD_ID, "compost"));

    public static final Tag<Item> FERTILIZER_GROUP = new ItemTags.Wrapper(new ResourceLocation(EnvironmentLib.MOD_ID, "fertilizer_group"));
    public static final Tag<Item> N_FERTILIZER = new ItemTags.Wrapper(new ResourceLocation(EnvironmentLib.MOD_ID, "n_fertilizer"));
    public static final Tag<Item> P_FERTILIZER = new ItemTags.Wrapper(new ResourceLocation(EnvironmentLib.MOD_ID, "p_fertilizer"));
    public static final Tag<Item> K_FERTILIZER = new ItemTags.Wrapper(new ResourceLocation(EnvironmentLib.MOD_ID, "k_fertilizer"));

    public static final Tag<Item> LIMING_MATERIAL = new ItemTags.Wrapper(new ResourceLocation(EnvironmentLib.MOD_ID, "liming_material"));
    public static final Tag<Item> ACIDIFYING_MATERIAL = new ItemTags.Wrapper(new ResourceLocation(EnvironmentLib.MOD_ID, "acidifying_material"));
}
