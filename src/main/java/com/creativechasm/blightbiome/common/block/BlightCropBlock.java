package com.creativechasm.blightbiome.common.block;

import com.creativechasm.blightbiome.common.registry.BlockRegistry;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.state.properties.SlabType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nonnull;

public class BlightCropBlock extends BushBlock {
    protected static final VoxelShape SHAPE = Block.makeCuboidShape(0D, 0D, 0D, 16D, 16D, 16D);

    public BlightCropBlock() {
        super(Properties.create(Material.PLANTS).doesNotBlockMovement().hardnessAndResistance(0.0F).sound(SoundType.PLANT));
    }

    @Override
    @Nonnull
    public VoxelShape getShape(@Nonnull BlockState state, @Nonnull IBlockReader worldIn, @Nonnull BlockPos pos, @Nonnull ISelectionContext context) {
        return SHAPE;
    }

    @Override
    protected boolean isValidGround(BlockState state, @Nonnull IBlockReader worldIn, @Nonnull BlockPos pos) {
        Block block = state.getBlock();
        if (block == BlockRegistry.BLIGHT_SOIL) return true;
        if (block == BlockRegistry.BLIGHT_SOIL_SLAB) {
            SlabType type = state.get(SlabBlock.TYPE);
            return type == SlabType.TOP || type == SlabType.DOUBLE;
        }
        return false;
    }

}
