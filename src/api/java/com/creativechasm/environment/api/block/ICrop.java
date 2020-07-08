package com.creativechasm.environment.api.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.IGrowable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

public interface ICrop extends IGrowable {

    void applyFertilizerBurn();

    @Override
    @ParametersAreNonnullByDefault
    default boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, BlockState state) {
        return false;
    }
}
