package com.creativechasm.cropcultivation.api.util;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IGrowable;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.FarmlandWaterManager;
import net.minecraftforge.common.Tags;

import java.util.Random;

import static com.creativechasm.cropcultivation.api.soil.SoilPH.*;

public abstract class AgricultureUtil {

    public static boolean doesSoilHaveWater(IWorldReader worldIn, BlockPos pos, int distance) {
        for (BlockPos blockpos : BlockPos.getAllInBoxMutable(pos.add(-distance, 0, -distance), pos.add(distance, 1, distance))) {
            if (worldIn.getFluidState(blockpos).isTagged(FluidTags.WATER)) return true;
        }
        return FarmlandWaterManager.hasBlockWaterTicket(worldIn, pos);
    }

//    public static float calculateMoistureAmbiance(IWorldReader worldIn, BlockPos pos, int distance) {
//        float score = 0f;
//
//        for (BlockPos blockpos : BlockPos.getAllInBoxMutable(pos.add(-distance, 0, -distance), pos.add(distance, 1, distance))) {
//            if (worldIn.getFluidState(blockpos).isTagged(FluidTags.WATER)) {
//                score += (distance - blockpos.manhattanDistance(pos) + 0.5f) / distance;
//            }
//        }
//        if (FarmlandWaterManager.hasBlockWaterTicket(worldIn, pos)) score += 1f;
//
//        float n = (distance + distance) * (distance + distance) - 1;
//        return score / n;
//    }

    public static boolean canGrow(World world, BlockPos pos, BlockState state) {
        return state.getBlock() instanceof IGrowable;
    }

    public static boolean grow(ServerWorld world, Random rand, BlockPos pos, BlockState state) {
        if (state.getBlock() instanceof IGrowable) {
            IGrowable iGrowable = (IGrowable) state.getBlock();
            iGrowable.grow(world, rand, pos, state);
            return true;
        }
        return false;
    }

    public static float getPHForNonSoilBlockInWorld(ServerWorld world, BlockPos pos, BlockState state) {
        float temperature = world.getBiome(pos).getTemperature(pos);
        if (state.getBlock() == Blocks.PODZOL) return STRONGLY_ACIDIC.randomPHAffectedByTemperature(world.rand, temperature);
        if (state.getBlock() == Blocks.MYCELIUM) return MODERATELY_ACIDIC.randomPHAffectedByTemperature(world.rand, temperature);
        if (state.getBlock() == Blocks.CLAY) return SLIGHTLY_ALKALINE.randomPHAffectedByTemperature(world.rand, temperature);
        if (Tags.Blocks.SAND.contains(state.getBlock())) return NEUTRAL.randomPHAffectedByTemperature(world.rand, temperature);
        if (Tags.Blocks.DIRT.contains(state.getBlock())) return SLIGHTLY_ACIDIC.randomPHAffectedByTemperature(world.rand, temperature);
        return NEUTRAL.randomPHAffectedByTemperature(world.rand, temperature);
    }
}
