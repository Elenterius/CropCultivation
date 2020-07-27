package com.creativechasm.cropcultivation.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

@FunctionalInterface
public interface IMeasuringDevice
{
    void gatherData(ServerWorld world, PlayerEntity player, BlockPos pos, ItemStack stack);
}
