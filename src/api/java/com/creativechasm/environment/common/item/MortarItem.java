package com.creativechasm.environment.common.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class MortarItem extends Item {
    public MortarItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean hasContainerItem() {
        return true;
    }

    @Override
    public ItemStack getContainerItem(ItemStack itemStack) {
        return new ItemStack(this);
    }
}
