package com.creativechasm.cropcultivation.item;

import com.creativechasm.cropcultivation.CropCultivationMod;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class TabletItem extends DeviceItem
{
    public TabletItem(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        CropCultivationMod.PROXY.openTabletScreen();
        playerIn.addStat(Stats.ITEM_USED.get(this));
        return ActionResult.resultSuccess(itemstack);
    }
}
