package com.creativechasm.blightbiome.common.block;

import com.creativechasm.blightbiome.registry.BlockRegistry;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.trees.Tree;
import net.minecraft.state.properties.SlabType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;

import javax.annotation.Nonnull;

public class SaplingBlockX extends SaplingBlock {
    public SaplingBlockX(Tree tree) {
        super(tree, Block.Properties.create(Material.PLANTS).doesNotBlockMovement().tickRandomly().hardnessAndResistance(0.0F).sound(SoundType.PLANT));
    }

    @Override
    protected boolean isValidGround(BlockState state, @Nonnull IBlockReader worldIn, @Nonnull BlockPos pos) {
        Block block = state.getBlock();
        if (block == BlockRegistry.BLIGHT_SOIL) return true;
        if (block == BlockRegistry.BLIGHT_MOSS) return true;
        if (block == BlockRegistry.BLIGHT_SOIL_SLAB) {
            SlabType type = state.get(SlabBlock.TYPE);
            return type == SlabType.DOUBLE;
        }
        return false;
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
        return isValidGround(worldIn.getBlockState(pos.down()), worldIn, pos.down());
    }
}
