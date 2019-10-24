package com.creativechasm.blightbiome.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;

public class BlightsoilBlock extends Block
{
	public BlightsoilBlock()
	{
		super(Properties
				.create(Material.EARTH, MaterialColor.DIRT)
				.hardnessAndResistance(0.5F)
				.sound(SoundType.GROUND)
		);
	}
}
