package com.creativechasm.cropcultivation.item;

import com.creativechasm.cropcultivation.CropCultivationMod;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import org.apache.logging.log4j.MarkerManager;

public class FoobarItem extends Item
{
    public FoobarItem(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {

        CropCultivationMod.LOGGER.warn(MarkerManager.getMarker("FoobarItem"),"I WAS USED!");

        return super.onItemUse(context);
    }
}
