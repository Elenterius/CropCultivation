package com.creativechasm.blightbiome.common.block;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nonnull;
import java.util.Random;

public class MoonFlowerBlock extends BloomingPlantBlock {

    public MoonFlowerBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void randomTick(@Nonnull BlockState state, @Nonnull ServerWorld worldIn, @Nonnull BlockPos pos, @Nonnull Random rand) {
        boolean isBlooming = state.get(BLOOMING);
        if (!isBlooming) {
            if (!worldIn.isDaytime() && worldIn.getCurrentMoonPhaseFactor() > 0.5f && worldIn.canSeeSky(pos)) {
                worldIn.setBlockState(pos, state.with(BLOOMING, true), 2);
            }
        } else {
            if (worldIn.isDaytime())
                worldIn.setBlockState(pos, state.with(BLOOMING, false), 2);
        }
    }
}
