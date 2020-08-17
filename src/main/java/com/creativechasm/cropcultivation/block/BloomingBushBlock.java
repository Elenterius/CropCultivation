package com.creativechasm.cropcultivation.block;

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

import javax.annotation.Nullable;
import java.util.Random;

public class BloomingBushBlock extends BushBlock {

    public static final BooleanProperty BLOOMING = BooleanProperty.create("blooming");

    public BloomingBushBlock(Properties properties) {
        super(properties.tickRandomly());
        setDefaultState(stateContainer.getBaseState().with(BLOOMING, true));
    }

    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
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
    public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
        randomTick(state, worldIn, pos, rand);
    }

    @Override
    public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
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
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(BLOOMING);
    }
}
