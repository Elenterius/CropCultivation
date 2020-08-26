package com.creativechasm.cropcultivation.util;

import net.minecraft.block.CropsBlock;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.IItemProvider;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public abstract class MiscUtil
{
    public static int getLuckLevel(LivingEntity livingEntity) {
        EffectInstance effect = livingEntity.getActivePotionEffect(Effects.LUCK);
        return effect != null ? effect.getAmplifier() + 1 : 0;
    }

    public static int getUnluckLevel(LivingEntity livingEntity) {
        EffectInstance effect = livingEntity.getActivePotionEffect(Effects.UNLUCK);
        return effect != null ? effect.getAmplifier() + 1 : 0;
    }

    public static int getLuckModifier(LivingEntity livingEntity) {
        return getLuckLevel(livingEntity) - getUnluckLevel(livingEntity);
    }

    public static int getFortuneLevel(@Nullable Entity entity) {
        return entity instanceof LivingEntity ? EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.FORTUNE, (LivingEntity) entity) : 0;
    }

    private static final Method GET_SEED_ITEM_METHOD = ObfuscationReflectionHelper.findMethod(CropsBlock.class, "func_199772_f");

    static {
        GET_SEED_ITEM_METHOD.setAccessible(true);
    }

    @Nullable
    public static Item getSeedItem(CropsBlock block) {
        IItemProvider item;
        try {
            item = (IItemProvider) GET_SEED_ITEM_METHOD.invoke(block);
            return item.asItem();
        }
        catch (IllegalAccessException | InvocationTargetException ignored) {}
        return null;
    }
}
