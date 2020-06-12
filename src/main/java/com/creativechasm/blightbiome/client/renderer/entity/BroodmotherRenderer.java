package com.creativechasm.blightbiome.client.renderer.entity;

import com.creativechasm.blightbiome.BlightBiomeMod;
import com.creativechasm.blightbiome.client.renderer.entity.model.BroodMotherModel;
import com.creativechasm.blightbiome.common.entity.BroodMotherEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.AbstractEyesLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class BroodmotherRenderer extends MobRenderer<BroodMotherEntity, BroodMotherModel> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(BlightBiomeMod.MOD_ID, "textures/entity/broodmother.png");
    private static final ResourceLocation EMISSION_TEXTURE = new ResourceLocation(BlightBiomeMod.MOD_ID, "textures/entity/broodmother_emission.png");
    private static final RenderType EYE_RENDER_TYPE = RenderType.getEyes(EMISSION_TEXTURE);

    public BroodmotherRenderer(EntityRendererManager rendererManager) {
        super(rendererManager, new BroodMotherModel(), 0.8F);
        addLayer(new AbstractEyesLayer<BroodMotherEntity, BroodMotherModel>(this) {
            @Override
            @Nonnull
            public RenderType getRenderType() {
                return EYE_RENDER_TYPE;
            }
        });
    }

    @Override
    @Nonnull
    public ResourceLocation getEntityTexture(@Nonnull BroodMotherEntity entityLivingBaseIn) {
        return TEXTURE;
    }
}