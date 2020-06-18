package com.creativechasm.blightbiome.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.entity.model.SegmentedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class BlightBroodModel<T extends Entity> extends SegmentedModel<T> {
    public ModelRenderer Head;
    public ModelRenderer Body;
    public ModelRenderer RearEnd;
    public ModelRenderer Leg8;
    public ModelRenderer Leg6;
    public ModelRenderer Leg4;
    public ModelRenderer Leg2;
    public ModelRenderer Leg7;
    public ModelRenderer Leg5;
    public ModelRenderer Leg3;
    public ModelRenderer Leg1;
    public ModelRenderer RearEnd2;
    public ModelRenderer HeadClaws;
    public ModelRenderer HeadClaws2;
    public ModelRenderer HeadEye;
    public ModelRenderer HeadEye2;
    public ModelRenderer RearEnd3;

    public BlightBroodModel() {
        textureWidth = 64;
        textureHeight = 64;

        Head = new ModelRenderer(this, 32, 4);
        Head.addBox(-4F, -4F, -6F, 8, 5, 6);
        Head.setRotationPoint(0F, 20F, -3F);
        Head.setTextureSize(64, 64);
        Head.mirror = true;
        setRotationAngle(Head, 0F, 0F, 0F);
        Body = new ModelRenderer(this, 0, 0);
        Body.addBox(-3F, -3F, -3F, 6, 6, 6);
        Body.setRotationPoint(0F, 20F, 0F);
        Body.setTextureSize(64, 64);
        Body.mirror = true;
        setRotationAngle(Body, 0F, 0F, 0F);
        RearEnd = new ModelRenderer(this, 0, 12);
        RearEnd.addBox(-5F, -4F, -6F, 8, 7, 6);
        RearEnd.setRotationPoint(1F, 20F, 9F);
        RearEnd.setTextureSize(64, 64);
        RearEnd.mirror = true;
        setRotationAngle(RearEnd, 0F, 0F, 0F);
        Leg8 = new ModelRenderer(this, 18, 0);
        Leg8.addBox(-1F, -1F, -1F, 16, 2, 2);
        Leg8.setRotationPoint(3F, 20F, 0F);
        Leg8.setTextureSize(64, 64);
        Leg8.mirror = true;
        setRotationAngle(Leg8, 0F, 0.5759587F, 0.1919862F);
        Leg6 = new ModelRenderer(this, 18, 0);
        Leg6.addBox(-1F, -1F, -1F, 16, 2, 2);
        Leg6.setRotationPoint(4F, 20F, 4F);
        Leg6.setTextureSize(64, 64);
        Leg6.mirror = true;
        setRotationAngle(Leg6, 0F, 0.2792527F, 0.1919862F);
        Leg4 = new ModelRenderer(this, 18, 0);
        Leg4.addBox(-1F, -1F, -1F, 16, 2, 2);
        Leg4.setRotationPoint(4F, 20F, 8F);
        Leg4.setTextureSize(64, 64);
        Leg4.mirror = true;
        setRotationAngle(Leg4, 0F, -0.2792527F, 0.1919862F);
        Leg2 = new ModelRenderer(this, 18, 0);
        Leg2.addBox(-1F, -1F, -1F, 16, 2, 2);
        Leg2.setRotationPoint(3F, 20F, 12F);
        Leg2.setTextureSize(64, 64);
        Leg2.mirror = true;
        setRotationAngle(Leg2, 0F, -0.5759587F, 0.1919862F);
        Leg7 = new ModelRenderer(this, 18, 0);
        Leg7.addBox(-15F, -1F, -1F, 16, 2, 2);
        Leg7.setRotationPoint(-3F, 20F, 0F);
        Leg7.setTextureSize(64, 64);
        Leg7.mirror = true;
        setRotationAngle(Leg7, 0F, -0.5759587F, -0.1919862F);
        Leg5 = new ModelRenderer(this, 18, 0);
        Leg5.addBox(-15F, -1F, -1F, 16, 2, 2);
        Leg5.setRotationPoint(-4F, 20F, 4F);
        Leg5.setTextureSize(64, 64);
        Leg5.mirror = true;
        setRotationAngle(Leg5, 0F, -0.2792527F, -0.1919862F);
        Leg3 = new ModelRenderer(this, 18, 0);
        Leg3.addBox(-15F, -1F, -1F, 16, 2, 2);
        Leg3.setRotationPoint(-4F, 20F, 8F);
        Leg3.setTextureSize(64, 64);
        Leg3.mirror = true;
        setRotationAngle(Leg3, 0F, 0.2792527F, -0.1919862F);
        Leg1 = new ModelRenderer(this, 18, 0);
        Leg1.addBox(-15F, -1F, -1F, 16, 2, 2);
        Leg1.setRotationPoint(-3F, 20F, 12F);
        Leg1.setTextureSize(64, 64);
        Leg1.mirror = true;
        setRotationAngle(Leg1, 0F, 0.5759587F, -0.1919862F);
        RearEnd2 = new ModelRenderer(this, 0, 25);
        RearEnd2.addBox(-5F, -4F, -6F, 6, 6, 6);
        RearEnd2.setRotationPoint(2F, 21F, 15F);
        RearEnd2.setTextureSize(64, 64);
        RearEnd2.mirror = true;
        setRotationAngle(RearEnd2, 0F, 0F, 0F);
        HeadClaws = new ModelRenderer(this, 32, 15);
        HeadClaws.addBox(-5F, -2F, -8F, 2, 2, 4);
        HeadClaws.setRotationPoint(0F, 20F, -3F);
        HeadClaws.setTextureSize(64, 64);
        HeadClaws.mirror = true;
        setRotationAngle(HeadClaws, 0F, -0.3141593F, 0F);
        HeadClaws2 = new ModelRenderer(this, 32, 15);
        HeadClaws2.addBox(3F, -2F, -8F, 2, 2, 4);
        HeadClaws2.setRotationPoint(0F, 20F, -3F);
        HeadClaws2.setTextureSize(64, 64);
        HeadClaws2.mirror = true;
        setRotationAngle(HeadClaws2, 0F, 0.3141593F, 0F);
        HeadEye = new ModelRenderer(this, 32, 21);
        HeadEye.addBox(-5F, -5F, -5F, 3, 3, 3);
        HeadEye.setRotationPoint(0F, 20F, -2F);
        HeadEye.setTextureSize(64, 64);
        HeadEye.mirror = true;
        setRotationAngle(HeadEye, 0F, 0F, 0F);
        HeadEye2 = new ModelRenderer(this, 32, 21);
        HeadEye2.addBox(2F, -5F, -5F, 3, 3, 3);
        HeadEye2.setRotationPoint(0F, 20F, -2F);
        HeadEye2.setTextureSize(64, 64);
        HeadEye2.mirror = true;
        setRotationAngle(HeadEye2, 0F, 0F, 0F);
        RearEnd3 = new ModelRenderer(this, 24, 27);
        RearEnd3.addBox(-5F, -4F, -6F, 5, 4, 6);
        RearEnd3.setRotationPoint(2.5F, 23F, 21F);
        RearEnd3.setTextureSize(64, 64);
        RearEnd3.mirror = true;
        setRotationAngle(RearEnd3, 0F, 0F, 0F);
    }

    @Override
    public void setRotationAngles(@Nonnull T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

    }

    private void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }

    @Nonnull
    @Override
    public Iterable<ModelRenderer> getParts() {
        return ImmutableList.of(Head, Body, RearEnd, Leg8, Leg6, Leg4, Leg2, Leg7, Leg5, Leg3, Leg1, RearEnd2, HeadClaws, HeadClaws2, HeadEye, HeadEye2, RearEnd3);
    }

}
