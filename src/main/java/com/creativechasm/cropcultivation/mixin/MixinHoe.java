package com.creativechasm.cropcultivation.mixin;

import com.creativechasm.cropcultivation.init.MixinHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.HoeItem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(HoeItem.class)
public class MixinHoe
{
    @Shadow @Final @Mutable protected static Map<Block, BlockState> HOE_LOOKUP;

    static {
        MixinHelper.HOE_LOOKUP = HOE_LOOKUP;
    }
}
