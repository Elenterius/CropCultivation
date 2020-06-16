package com.creativechasm.blightbiome.common.block.tree;

import com.creativechasm.blightbiome.common.registry.BlockRegistry;
import com.creativechasm.blightbiome.common.world.gen.feature.LilyTreeFeature;
import com.google.common.collect.ImmutableList;
import net.minecraft.block.trees.Tree;
import net.minecraft.world.gen.blockstateprovider.BlockStateProvider;
import net.minecraft.world.gen.blockstateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliageplacer.BlobFoliagePlacer;
import net.minecraft.world.gen.foliageplacer.FoliagePlacer;
import net.minecraft.world.gen.treedecorator.BeehiveTreeDecorator;
import net.minecraft.world.gen.treedecorator.TreeDecorator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class LilyTree extends Tree {

    public static LilyTreeFeature FEATURE = new LilyTreeFeature(TreeFeatureConfig::deserializeFoliage);

    @Nullable
    @Override
    protected ConfiguredFeature<TreeFeatureConfig, ?> getTreeFeature(@Nonnull Random randomIn, boolean hasFlowerTag) {
        List<TreeDecorator> decorators = ImmutableList.of(new BeehiveTreeDecorator(0.05F));
        BlockStateProvider leavesProvider = new SimpleBlockStateProvider(BlockRegistry.BLIGHT_MOSS.getDefaultState());
        BlockStateProvider logProvider = new SimpleBlockStateProvider(BlockRegistry.BLIGHT_SOIL.getDefaultState());
        FoliagePlacer foliagePlacer = new BlobFoliagePlacer(2, 0);

//        hasFlowerTag ? DefaultBiomeFeatures.field_230136_s_ : DefaultBiomeFeatures.BIRCH_TREE_CONFIG

        return FEATURE.withConfiguration(
                (new TreeFeatureConfig.Builder(logProvider, leavesProvider, foliagePlacer))
                        .baseHeight(4).heightRandA(2).foliageHeight(3).ignoreVines()
                        .setSapling(BlockRegistry.LILY_TREE_SAPLING)
                        .decorators(decorators)
                        .build()
        );
    }
}
