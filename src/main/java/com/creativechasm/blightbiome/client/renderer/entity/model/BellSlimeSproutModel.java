package com.creativechasm.blightbiome.client.renderer.entity.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class BellSlimeSproutModel<T extends Entity> extends EntityModel<T> {

    private final ModelRenderer plant;
    private final ModelRenderer bud;

    public BellSlimeSproutModel() {
        textureWidth = 64;
        textureHeight = 32;

        plant = new ModelRenderer(this);
        plant.setRotationPoint(0.0F, 24.0F, 0.0F);
        setRotationAngle(plant, 0.0F, -0.7854F, 0.0F);
        plant.setTextureOffset(0, 0).addBox(-8.0F, -16.0F, 0.0F, 16.0F, 16.0F, 0.0F, 0.0F, true);
        plant.setTextureOffset(0, 0).addBox(0.0F, -16.0F, -8.0F, 0.0F, 16.0F, 16.0F, 0.0F, true);

        bud = new ModelRenderer(this);
        bud.setRotationPoint(6.5F, -8.0F, 0.0F);
        plant.addChild(bud);
        bud.setTextureOffset(32, 0).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 3.0F, 3.0F, 0.0F, false);
    }

    @Override
    public void setRotationAngles(@Nonnull Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStack, @Nonnull IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        plant.render(matrixStack, buffer, packedLight, packedOverlay);
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}