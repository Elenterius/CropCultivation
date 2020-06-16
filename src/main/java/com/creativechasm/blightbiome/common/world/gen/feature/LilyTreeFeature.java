package com.creativechasm.blightbiome.common.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.feature.AbstractSmallTreeFeature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

public class LilyTreeFeature extends AbstractSmallTreeFeature<TreeFeatureConfig> {

    public LilyTreeFeature(Function<Dynamic<?>, ? extends TreeFeatureConfig> dynamicFunction) {
        super(dynamicFunction);
    }

    @Override
    protected boolean place(@Nonnull IWorldGenerationReader generationReader, @Nonnull Random rand, @Nonnull BlockPos pos, @Nonnull Set<BlockPos> trunk, @Nonnull Set<BlockPos> foliage, @Nonnull MutableBoundingBox bb, @Nonnull TreeFeatureConfig config) {

        int baseHeight = config.baseHeight + rand.nextInt(config.heightRandA + 1) + rand.nextInt(config.heightRandB + 1);
        int trunkHeight = config.trunkHeight >= 0 ? config.trunkHeight + rand.nextInt(config.trunkHeightRandom + 1) : baseHeight - (config.foliageHeight + rand.nextInt(config.foliageHeightRandom + 1));
        int foliageWidth = config.foliagePlacer.func_225573_a_(rand, trunkHeight, baseHeight, config);
        Optional<BlockPos> optional = func_227212_a_(generationReader, baseHeight, trunkHeight, foliageWidth, pos, config);
        if (!optional.isPresent()) return false;

        BlockPos blockpos = optional.get();
        setDirtAt(generationReader, blockpos.down(), blockpos);
        config.foliagePlacer.func_225571_a_(generationReader, rand, config, baseHeight, trunkHeight, foliageWidth, blockpos, foliage);

        func_227213_a_(generationReader, rand, baseHeight, blockpos, config.trunkTopOffset + rand.nextInt(config.trunkTopOffsetRandom + 1), trunk, bb, config);

        blockpos = blockpos.up(baseHeight);
        BlockState state = config.leavesProvider.getBlockState(rand, blockpos);
//        if (state.getBlock() == BlockRegistry.BLIGHT_MOSS) {
//            state = state.with(LeavesBlock.DISTANCE, 1).with(LeavesBlock.PERSISTENT, true);
//        }
        int foo = Constants.BlockFlags.DEFAULT | Constants.BlockFlags.NO_NEIGHBOR_DROPS;
        generationReader.setBlockState(blockpos, state, foo);
        return true;
    }
}
