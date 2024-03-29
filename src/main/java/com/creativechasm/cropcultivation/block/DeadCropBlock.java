package com.creativechasm.cropcultivation.block;

import com.creativechasm.cropcultivation.init.ModBlocks;
import com.creativechasm.cropcultivation.init.ModTags;
import com.creativechasm.cropcultivation.init.ModTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DeadBushBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.Tags;

import javax.annotation.Nullable;

public class DeadCropBlock extends DeadBushBlock
{
    public DeadCropBlock(Properties builder) {
        super(builder);
    }

    @Override
    public void harvestBlock(World worldIn, PlayerEntity player, BlockPos pos, BlockState state, @Nullable TileEntity te, ItemStack stack) {
        super.harvestBlock(worldIn, player, pos, state, te, stack);
        if (player instanceof ServerPlayerEntity) {
            if (this == ModBlocks.DEAD_CROP_WITHERED) {
                ModTriggers.DEAD_CROP_WITHERED_DESTROYED.trigger((ServerPlayerEntity) player);
            }
            else if (this == ModBlocks.DEAD_CROP_ROTTEN) {
                ModTriggers.DEAD_CROP_ROTTEN_DESTROYED.trigger((ServerPlayerEntity) player);
            }
        }
    }

    @Override
    protected boolean isValidGround(BlockState state, IBlockReader worldIn, BlockPos pos) {
        Block block = state.getBlock();
        return ModTags.Forge.FARMLAND_BLOCK.contains(block) || Tags.Blocks.DIRT.contains(block);
    }
}
