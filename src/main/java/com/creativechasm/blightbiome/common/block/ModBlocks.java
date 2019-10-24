package com.creativechasm.blightbiome.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.SlabBlock;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder("blightbiome")
public class ModBlocks
{
	@ObjectHolder("blightsoil")
	public static BlightsoilBlock BLIGHT_SOIL;

	@ObjectHolder("blightsoil_slab")
	public static SlabBlock BLIGHT_SOIL_SLAB;

	@ObjectHolder("blightmoss")
	public static Block BLIGHT_MOSS;

	@ObjectHolder("blightweeds")
	public static BlightweedBlock BLIGHT_WEED;
}
