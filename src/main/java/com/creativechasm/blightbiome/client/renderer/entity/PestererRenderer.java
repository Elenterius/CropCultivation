package com.creativechasm.blightbiome.client.renderer.entity;

import com.creativechasm.blightbiome.BlightBiomeMod;
import com.creativechasm.blightbiome.client.renderer.entity.model.PestererModel;
import com.creativechasm.blightbiome.common.entity.PestererEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class PestererRenderer extends MobRenderer<PestererEntity, PestererModel> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(BlightBiomeMod.MOD_ID, "textures/entity/pesterer.png");

    public PestererRenderer(EntityRendererManager rendererManager) {
        super(rendererManager, new PestererModel(), 0.2F);
//		this.addLayer(new SpiderEyesLayer(this));
    }

    @Override
    @Nonnull
    public ResourceLocation getEntityTexture(@Nonnull PestererEntity entityLivingBaseIn) {
        return TEXTURE;
    }
}