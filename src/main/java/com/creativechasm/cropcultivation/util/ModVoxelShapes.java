package com.creativechasm.cropcultivation.util;

import net.minecraft.block.Block;
import net.minecraft.util.math.shapes.VoxelShape;

public abstract class ModVoxelShapes
{
    public static final VoxelShape BUSH = Block.makeCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 13.0D, 14.0D);
    public static final VoxelShape SMALL_FLOWER = Block.makeCuboidShape(5.0D, 0.0D, 5.0D, 11.0D, 10.0D, 11.0D);
}
