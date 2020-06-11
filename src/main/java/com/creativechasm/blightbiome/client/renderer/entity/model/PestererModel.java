package com.creativechasm.blightbiome.client.renderer.entity.model;
//Made by Elenterius

import com.creativechasm.blightbiome.common.entity.PestererEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;


@OnlyIn(Dist.CLIENT)
public class PestererModel extends EntityModel<PestererEntity> {
    private final ModelRenderer body;
    private final ModelRenderer head;
    private final ModelRenderer topHead;
    private final ModelRenderer leftLeg;
    private final ModelRenderer spore;
    private final ModelRenderer leftArm;
    private final ModelRenderer rightLeg;
    private final ModelRenderer rightArm;

    public PestererModel() {
        textureWidth = 64;
        textureHeight = 64;

        body = new ModelRenderer(this);
        body.setRotationPoint(0.0F, -1.6F, 0.0F);
        body.setTextureOffset(32, 0).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, 0.0F, true);

        head = new ModelRenderer(this);
        head.setRotationPoint(0.0F, 0.0F, 0.0F);
        body.addChild(head);
        head.setTextureOffset(0, 33).addBox(-4.0F, -1.0F, -4.0F, 8.0F, 1.0F, 8.0F, 0.0F, true);
        head.setTextureOffset(0, 21).addBox(-4.0F, -6.0F, -4.0F, 8.0F, 5.0F, 7.0F, 0.0F, true);
        head.setTextureOffset(0, 15).addBox(-4.0F, -6.0F, 3.0F, 8.0F, 5.0F, 1.0F, 0.0F, true);

        topHead = new ModelRenderer(this);
        topHead.setRotationPoint(0.0F, 0.0F, 0.0F);
        head.addChild(topHead);
        topHead.setTextureOffset(0, 0).addBox(-4.0F, -13.0F, -4.0F, 8.0F, 7.0F, 8.0F, 0.0F, true);

        leftLeg = new ModelRenderer(this);
        leftLeg.setRotationPoint(-2.0F, 12.0F, 0.0F);
        body.addChild(leftLeg);
        leftLeg.setTextureOffset(56, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 14.0F, 2.0F, 0.0F, false);

        spore = new ModelRenderer(this);
        spore.setRotationPoint(0.0F, 7.0F, -2.5F);
        body.addChild(spore);
        spore.setTextureOffset(32, 16).addBox(-3.0F, -4.0F, -1.5F, 6.0F, 8.0F, 3.0F, 0.0F, true);

        leftArm = new ModelRenderer(this);
        leftArm.setRotationPoint(-5.0F, 2.5F, 0.0F);
        body.addChild(leftArm);
        setRotationAngle(leftArm, -2.3562F, 0.9599F, 0.0873F);
        leftArm.setTextureOffset(56, 0).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 25.0F, 2.0F, 0.0F, false);

        rightLeg = new ModelRenderer(this);
        rightLeg.setRotationPoint(2.0F, 12.0F, 0.0F);
        body.addChild(rightLeg);
        rightLeg.setTextureOffset(56, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 14.0F, 2.0F, 0.0F, true);

        rightArm = new ModelRenderer(this);
        rightArm.setRotationPoint(5.0F, 2.5F, 0.0F);
        body.addChild(rightArm);
        setRotationAngle(rightArm, -2.618F, -0.6981F, 0.0F);
        rightArm.setTextureOffset(56, 0).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 25.0F, 2.0F, 0.0F, true);
    }

    @Override
    public void setRotationAngles(@Nonnull PestererEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStack, @Nonnull IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        body.render(matrixStack, buffer, packedLight, packedOverlay);
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}