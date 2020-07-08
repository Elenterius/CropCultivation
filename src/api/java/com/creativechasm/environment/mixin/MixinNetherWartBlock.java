package com.creativechasm.environment.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.BushBlock;
import net.minecraft.block.IGrowable;
import net.minecraft.block.NetherWartBlock;
import net.minecraft.state.IntegerProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

@Mixin(NetherWartBlock.class)
public abstract class MixinNetherWartBlock extends BushBlock implements IGrowable {

    @Shadow @Final public static IntegerProperty AGE;

    protected MixinNetherWartBlock(Properties properties) {
        super(properties);
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean canGrow(IBlockReader worldIn, BlockPos pos, BlockState state, boolean isClient) {
        return state.get(AGE) < 3;
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, BlockState state) {
        return false; //don't allow use of bone meal on this plant
    }

    @Override
    @ParametersAreNonnullByDefault
    public void grow(ServerWorld worldIn, Random rand, BlockPos pos, BlockState state) {
        int i = Math.min(3, state.get(AGE) + 1);
        worldIn.setBlockState(pos, state.with(AGE, i), Constants.BlockFlags.BLOCK_UPDATE);
    }
}
