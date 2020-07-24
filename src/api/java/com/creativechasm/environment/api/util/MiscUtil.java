package com.creativechasm.environment.api.util;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
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
}
