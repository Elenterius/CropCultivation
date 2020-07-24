package com.creativechasm.cropcultivation.api.plant;

import com.creativechasm.cropcultivation.api.util.Neighborhood;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction8;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;

/**
 * use cellular automata logic to determine if the plant has enough space & friends to grow
 */
public interface IPlantGrowthCA {

    int getMinPlantNeighbors();

    int getMaxPlantNeighbors();

    Neighborhood getNeighborhood();

    /**
     * Checks if the provided BlockPos is a IPlantable or solid Block neighbor.<br>
     * Incompatible plant neighbors (e.g. plants disliking each other) should return a value greater then 1, which will increase the likelihood of the plant being overcrowded.
     * @return 0 - if no neighbor is present<br>
     * 1 - a neighbor is present
     */
    default int getNeighborScoreFor(World world, Direction8 direction, BlockPos.Mutable neighborPos, BlockState neighborState) {
        return (neighborState.getBlock() instanceof IPlantable || neighborState.getMaterial().isSolid()) ? 1 : 0;
    }

    default int countPlantNeighbors(World world, BlockPos pos) {
        int count = 0;
        BlockPos.Mutable neighborPos = new BlockPos.Mutable();

        //check direct neighbors
        neighborPos.setPos(pos).move(Direction.NORTH);
        count += getNeighborScoreFor(world, Direction8.NORTH, neighborPos, world.getBlockState(neighborPos));
        neighborPos.setPos(pos).move(Direction.EAST);
        count += getNeighborScoreFor(world, Direction8.EAST, neighborPos, world.getBlockState(neighborPos));
        neighborPos.setPos(pos).move(Direction.SOUTH);
        count += getNeighborScoreFor(world, Direction8.SOUTH, neighborPos, world.getBlockState(neighborPos));
        neighborPos.setPos(pos).move(Direction.WEST);
        count += getNeighborScoreFor(world, Direction8.WEST, neighborPos, world.getBlockState(neighborPos));

        if (getNeighborhood() == Neighborhood.INDIRECT_NEIGHBOR) {
            neighborPos.setPos(pos).move(Direction.NORTH).move(Direction.EAST);
            count += getNeighborScoreFor(world, Direction8.NORTH_EAST, neighborPos, world.getBlockState(neighborPos));
            neighborPos.setPos(pos).move(Direction.SOUTH).move(Direction.EAST);
            count += getNeighborScoreFor(world, Direction8.SOUTH_EAST, neighborPos, world.getBlockState(neighborPos));
            neighborPos.setPos(pos).move(Direction.SOUTH).move(Direction.WEST);
            count += getNeighborScoreFor(world, Direction8.SOUTH_WEST, neighborPos, world.getBlockState(neighborPos));
            neighborPos.setPos(pos).move(Direction.NORTH).move(Direction.WEST);
            count += getNeighborScoreFor(world, Direction8.NORTH_WEST, neighborPos, world.getBlockState(neighborPos));
        }

        return count;
    }

    default boolean isPlantOvercrowded(int neighborCount) {
        return neighborCount > getMaxPlantNeighbors();
    }

    default boolean isPlantLonely(int neighborCount) {
        return neighborCount < getMinPlantNeighbors();
    }
}
