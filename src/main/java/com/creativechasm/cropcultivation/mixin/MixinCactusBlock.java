package com.creativechasm.cropcultivation.mixin;

import com.creativechasm.cropcultivation.api.block.BlockPropertyUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CactusBlock;
import net.minecraft.block.IGrowable;
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

@Mixin(CactusBlock.class)
public class MixinCactusBlock extends Block implements IGrowable
{
    @Shadow @Final public static IntegerProperty AGE;
    public final int MAX_AGE = BlockPropertyUtil.getMaxAge(AGE);

    public MixinCactusBlock(Properties properties) {
        super(properties);
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean canGrow(IBlockReader worldIn, BlockPos pos, BlockState state, boolean isClient) {
        BlockPos upPos = pos.up();
        BlockState upState = worldIn.getBlockState(upPos);
        if (upState.isAir(worldIn, upPos)) {
            int n = getNumOfCactusBlocksBelow(worldIn, pos);
            return n + 1 < 3 && worldIn.getBlockState(pos).get(AGE) < MAX_AGE;
        }
        else if (upState.getBlock() == this) {
            int nUp = getNumOfCactusBlocksAbove(worldIn, pos);
            int nDown = getNumOfCactusBlocksBelow(worldIn, pos);
            return nUp + nDown + 1 < 3 & worldIn.getBlockState(pos.up(nUp)).get(AGE) < MAX_AGE;
        }
        return false;
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void grow(ServerWorld worldIn, Random rand, BlockPos pos, BlockState state) {
        BlockPos upPos = pos.up();
        BlockState upState = worldIn.getBlockState(upPos);
        if (upState.isAir(worldIn, upPos)) {
            grow(worldIn, pos, state, rand);
        }
        else {
            int nUp = getNumOfCactusBlocksAbove(worldIn, pos);
            BlockPos topPos = pos.up(nUp);
            BlockState topState = worldIn.getBlockState(topPos);
            grow(worldIn, topPos, topState, rand);
        }
    }

    protected int getNumOfCactusBlocksAbove(IBlockReader worldIn, BlockPos pos) {
        int i = 0;
        while (i < 3 && worldIn.getBlockState(pos.up(i + 1)).getBlock() == this) i++;
        return i;
    }

    protected int getNumOfCactusBlocksBelow(IBlockReader worldIn, BlockPos pos) {
        int i = 0;
        while (i < 3 && worldIn.getBlockState(pos.down(i + 1)).getBlock() == this) i++;
        return i;
    }

    private void grow(ServerWorld worldIn, BlockPos pos, BlockState state, Random rand) {
        int age = Math.min(MAX_AGE, state.get(AGE) + rand.nextInt(3) + 2);
        if (age == MAX_AGE) {
            BlockPos upPos = pos.up();
            worldIn.setBlockState(upPos, getDefaultState());

            BlockState newState = state.with(AGE, 0);
            worldIn.setBlockState(pos, newState, Constants.BlockFlags.NO_RERENDER);
            newState.neighborChanged(worldIn, upPos, this, pos, false);
        }
        else {
            worldIn.setBlockState(pos, state.with(AGE, age), Constants.BlockFlags.NO_RERENDER);
        }
    }
}
