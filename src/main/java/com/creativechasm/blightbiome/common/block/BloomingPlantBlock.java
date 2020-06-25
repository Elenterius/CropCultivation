package com.creativechasm.blightbiome.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BushBlock;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.LightType;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

public class BloomingPlantBlock extends BushBlock {

    public static final BooleanProperty BLOOMING = BooleanProperty.create("blooming");

    public BloomingPlantBlock(Properties properties) {
        super(properties);
        setDefaultState(stateContainer.getBaseState().with(BLOOMING, true));
    }

    @Override
    @Nonnull
    public BlockState updatePostPlacement(@Nonnull BlockState stateIn, @Nonnull Direction facing, @Nonnull BlockState facingState, @Nonnull IWorld worldIn, @Nonnull BlockPos currentPos, @Nonnull BlockPos facingPos) {
        //called when neighborhood changes
        worldIn.getPendingBlockTicks().scheduleTick(currentPos, this, 1); // "delay" call of tick() method
        return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        if (!context.getWorld().isDaytime() && context.getWorld().getLightFor(LightType.BLOCK, context.getPos()) < 8) {
            return getDefaultState().with(BLOOMING, false);
        }
        return super.getStateForPlacement(context);
    }

    @Override
    public void tick(@Nonnull BlockState state, @Nonnull ServerWorld worldIn, @Nonnull BlockPos pos, @Nonnull Random rand) {
        randomTick(state, worldIn, pos, rand);
    }

    @Override
    public void randomTick(@Nonnull BlockState state, @Nonnull ServerWorld worldIn, @Nonnull BlockPos pos, @Nonnull Random rand) {
        boolean isBlooming = state.get(BLOOMING);
        if (isBlooming) {
            if (!worldIn.isDaytime() && worldIn.getLightFor(LightType.BLOCK, pos) < 8) {
                worldIn.setBlockState(pos, state.with(BLOOMING, false), 2);
            }
        } else {
            if (worldIn.isDaytime() && worldIn.getLightFor(LightType.BLOCK, pos) < 8)
                worldIn.setBlockState(pos, state.with(BLOOMING, true), 2);
        }
    }

    @Override
    protected void fillStateContainer(@Nonnull StateContainer.Builder<Block, BlockState> builder) {
        builder.add(BLOOMING);
    }
}
