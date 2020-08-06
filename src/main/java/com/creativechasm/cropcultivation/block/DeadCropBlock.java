package com.creativechasm.cropcultivation.block;

import com.creativechasm.cropcultivation.trigger.ModTriggers;
import net.minecraft.block.BlockState;
import net.minecraft.block.DeadBushBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class DeadCropBlock extends DeadBushBlock
{
    public DeadCropBlock(Properties builder) {
        super(builder);
    }

    @Override
    public void harvestBlock(World worldIn, PlayerEntity player, BlockPos pos, BlockState state, @Nullable TileEntity te, ItemStack stack) {
        super.harvestBlock(worldIn, player, pos, state, te, stack);
        if (player instanceof ServerPlayerEntity) ModTriggers.DEAD_CROP_DESTROYED.trigger((ServerPlayerEntity) player);
    }
}
