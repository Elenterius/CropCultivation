package com.creativechasm.environment.api.plant;

import net.minecraft.block.BlockState;
import net.minecraft.block.IGrowable;
import net.minecraft.state.IntegerProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

public interface ICrop extends IGrowable, IPlantable {

    @Override
    @ParametersAreNonnullByDefault
    default boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, BlockState state) {
        return false;
    }

    @Override
    default PlantType getPlantType(IBlockReader world, BlockPos pos) {
        return PlantType.Crop;
    }

    void applyFertilizerBurn();

    float[] getNutrientConsumption();

    boolean consumeSoilMoistureAndNutrients(ServerWorld worldIn, BlockPos pos, BlockState blockState);

    boolean isGrowthSeason(World world);

    /**
     * @return true if the plant is healthy and can grow
     */
    boolean isHealthy(World world, BlockPos pos, BlockState state);

    /**
     * @return true when important nutrients are insufficient
     */
    boolean isWilting(World world, BlockPos pos, BlockState state);

    boolean isMature(World world, BlockPos pos, BlockState state);

    /**
     * @return true if the plant can be "pollinated" (e.g. bees, wind, manual)
     */
    boolean isFlowering(World world, BlockPos pos, BlockState state);

    /**
     * @return true if the plant has fruit/seeds
     */
    boolean hasFruit(World world, BlockPos pos, BlockState state);

    /**
     * fruit is ripe for "picking"
     */
    default boolean canBeHarvested(World world, BlockPos pos, BlockState state) {
        return isMature(world, pos, state) && hasFruit(world, pos, state);
    }

    /**
     * if the fruit can be raked from the individual branches using hand rakes or vibrating rakes (tools)
     */
    default boolean canBeRaked(World world, BlockPos pos, BlockState state) {
        return hasFruit(world, pos, state);
    }

    IntegerProperty getCropAgeProperty();

    int getMaxCropAge();

    default int getCropAge(BlockState state) {
        return state.get(getCropAgeProperty());
    }

    default boolean isMaxCropAge(BlockState state) {
        return state.get(getCropAgeProperty()) >= getMaxCropAge();
    }
}
