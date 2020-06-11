package com.creativechasm.blightbiome.client.renderer.entity;

import com.creativechasm.blightbiome.BlightBiomeMod;
import com.creativechasm.blightbiome.client.renderer.entity.model.BlightBroodModel;
import com.creativechasm.blightbiome.common.entity.BlightBroodEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class BlightBroodRenderer extends MobRenderer<BlightBroodEntity, BlightBroodModel<BlightBroodEntity>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(BlightBiomeMod.MOD_ID, "textures/entity/blight_brood.png");

    public BlightBroodRenderer(EntityRendererManager rendererManager) {
        super(rendererManager, new BlightBroodModel<>(), 0.8F);
    }

    @Override
    @Nonnull
    public ResourceLocation getEntityTexture(@Nonnull BlightBroodEntity entityLivingBaseIn) {
        return TEXTURE;
    }
}