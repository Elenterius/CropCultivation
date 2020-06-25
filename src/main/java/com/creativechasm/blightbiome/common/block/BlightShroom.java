package com.creativechasm.blightbiome.common.block;

import com.creativechasm.blightbiome.registry.BlockRegistry;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.state.properties.SlabType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nonnull;

public class BlightShroom extends BushBlock {
    protected static final VoxelShape SHAPE = Block.makeCuboidShape(5.0D, 0.0D, 5.0D, 11.0D, 10.0D, 11.0D);

    public BlightShroom() {
        super(Properties.create(Material.PLANTS).doesNotBlockMovement().hardnessAndResistance(0.0F).sound(SoundType.PLANT).lightValue(3));
    }

    @Override
    @Nonnull
    public VoxelShape getShape(BlockState state, @Nonnull IBlockReader worldIn, @Nonnull BlockPos pos, @Nonnull ISelectionContext context) {
        Vec3d vec3d = state.getOffset(worldIn, pos);
        return SHAPE.withOffset(vec3d.x, vec3d.y, vec3d.z);
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

    @Override
    public boolean isEmissiveRendering(@Nonnull BlockState state) {
        return true;
    }
}
