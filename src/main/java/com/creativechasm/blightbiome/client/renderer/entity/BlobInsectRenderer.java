package com.creativechasm.blightbiome.client.renderer.entity;

import com.creativechasm.blightbiome.BlightBiomeMod;
import com.creativechasm.blightbiome.client.renderer.entity.model.BlobInsectModel;
import com.creativechasm.blightbiome.common.entity.BlobInsectEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.AbstractEyesLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class BlobInsectRenderer extends MobRenderer<BlobInsectEntity, BlobInsectModel<BlobInsectEntity>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(BlightBiomeMod.MOD_ID, "textures/entity/blob_insect.png");
    private static final ResourceLocation EMISSION_TEXTURE = new ResourceLocation(BlightBiomeMod.MOD_ID, "textures/entity/blob_insect_eyes.png");
    private static final RenderType EYE_RENDER_TYPE = RenderType.getEyes(EMISSION_TEXTURE);

    public BlobInsectRenderer(EntityRendererManager rendererManager) {
        super(rendererManager, new BlobInsectModel<>(), 0.2F);
        addLayer(new AbstractEyesLayer<BlobInsectEntity, BlobInsectModel<BlobInsectEntity>>(this) {
            @Override
            @Nonnull
            public RenderType getRenderType() {
                return EYE_RENDER_TYPE;
            }

            @Override
            public void render(@Nonnull MatrixStack matrixStackIn, @Nonnull IRenderTypeBuffer bufferIn, int packedLightIn, @Nonnull BlobInsectEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
                IVertexBuilder ivertexbuilder = bufferIn.getBuffer(getRenderType());
                getEntityModel().render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            }
        });
    }

    @Override
    protected float getDeathMaxRotation(@Nonnull BlobInsectEntity entity) {
        return 180.0F;
    }

    @Override
    @Nonnull
    public ResourceLocation getEntityTexture(@Nonnull BlobInsectEntity entity) {
        return TEXTURE;
    }
}