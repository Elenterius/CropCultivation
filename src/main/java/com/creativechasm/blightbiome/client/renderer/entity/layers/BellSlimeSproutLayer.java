package com.creativechasm.blightbiome.client.renderer.entity.layers;

import com.creativechasm.blightbiome.BlightBiomeMod;
import com.creativechasm.blightbiome.client.renderer.entity.model.BellSlimeSproutModel;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.SlimeModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class BellSlimeSproutLayer<T extends LivingEntity> extends LayerRenderer<T, SlimeModel<T>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(BlightBiomeMod.MOD_ID, "textures/entity/bellslime_sprout.png");
    private final EntityModel<T> sproutModel = new BellSlimeSproutModel<>();
    public static final Quaternion eulerYaw90 = new Quaternion(0, 90, 0, true);

    public BellSlimeSproutLayer(IEntityRenderer<T, SlimeModel<T>> entityRendererIn) {
        super(entityRendererIn);
    }

    public void render(@Nonnull MatrixStack matrixStackIn, @Nonnull IRenderTypeBuffer bufferIn, int packedLightIn, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!entity.isInvisible()) {
            getEntityModel().copyModelAttributesTo(sproutModel);
            matrixStackIn.push();
            matrixStackIn.scale(0.9f, 0.8f, 0.8f);
            matrixStackIn.translate(0D, -0.25D, 0D);
            matrixStackIn.rotate(eulerYaw90);

            sproutModel.setLivingAnimations(entity, limbSwing, limbSwingAmount, partialTicks);
            sproutModel.setRotationAngles(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            IVertexBuilder ivertexbuilder = bufferIn.getBuffer(RenderType.getEntityCutoutNoCull(TEXTURE));
            sproutModel.render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            matrixStackIn.pop();
        }
    }
}