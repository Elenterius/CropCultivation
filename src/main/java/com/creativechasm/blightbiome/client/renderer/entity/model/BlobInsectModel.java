package com.creativechasm.blightbiome.client.renderer.entity.model;

import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.client.renderer.model.ModelBox;
import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BlobInsectModel<T extends Entity> extends EntityModel<T>
{
	private final RendererModel body;
	private final RendererModel blob;
	private final RendererModel head;
	private final RendererModel leftLeg0;
	private final RendererModel leftLeg2;
	private final RendererModel leftLeg3;
	private final RendererModel rightLeg0;
	private final RendererModel rightLeg1;
	private final RendererModel rightLeg2;

	public BlobInsectModel()
	{
		textureWidth = 16;
		textureHeight = 16;

		body = new RendererModel(this);
		body.setRotationPoint(0.0F, 23.0F, 0.0F);
		body.cubeList.add(new ModelBox(body, 0, 12, -1.5F, -1.0F, -1.5F, 3, 1, 3, 0.0F, false));

		blob = new RendererModel(this);
		blob.setRotationPoint(0.5F, -1.0F, -0.5F);
		setRotationAngle(blob, -0.0873F, 0.0F, 0.0F);
		body.addChild(blob);
		blob.cubeList.add(new ModelBox(blob, 0, 0, -2.5F, -3.8F, -1.0F, 4, 4, 4, 0.0F, false));

		head = new RendererModel(this);
		head.setRotationPoint(0.0F, -1.0F, -2.0F);
		body.addChild(head);
		head.cubeList.add(new ModelBox(head, 0, 8, -1.0F, -1.0F, -1.5F, 2, 2, 2, 0.0F, false));
		head.cubeList.add(new ModelBox(head, 10, 5, -1.09F, -0.33F, -1.75F, 0, 0, 0, 0.0F, true));
		head.cubeList.add(new ModelBox(head, 10, 5, 0.61F, -0.33F, -1.75F, 0, 0, 0, 0.0F, true));
		head.cubeList.add(new ModelBox(head, 10, 5, 0.1625F, 0.3875F, -1.7125F, 0, 0, 0, 0.0F, true));
		head.cubeList.add(new ModelBox(head, 10, 5, -0.775F, 0.3875F, -1.7125F, 0, 0, 0, 0.0F, true));

		leftLeg0 = new RendererModel(this);
		leftLeg0.setRotationPoint(1.5F, 0.0F, -1.25F);
		setRotationAngle(leftLeg0, -0.4363F, 0.0F, -0.7854F);
		body.addChild(leftLeg0);
		leftLeg0.cubeList.add(new ModelBox(leftLeg0, 0, 0, -0.5F, -0.25F, -0.5F, 1, 2, 1, 0.0F, false));

		leftLeg2 = new RendererModel(this);
		leftLeg2.setRotationPoint(1.5F, 0.0F, -0.25F);
		setRotationAngle(leftLeg2, 0.0F, 0.0F, -0.7854F);
		body.addChild(leftLeg2);
		leftLeg2.cubeList.add(new ModelBox(leftLeg2, 0, 0, -0.5F, -0.25F, -0.5F, 1, 2, 1, 0.0F, true));

		leftLeg3 = new RendererModel(this);
		leftLeg3.setRotationPoint(1.5F, 0.0F, 0.75F);
		setRotationAngle(leftLeg3, 0.4363F, 0.0F, -0.7854F);
		body.addChild(leftLeg3);
		leftLeg3.cubeList.add(new ModelBox(leftLeg3, 0, 0, -0.5F, -0.25F, -0.5F, 1, 2, 1, 0.0F, false));

		rightLeg0 = new RendererModel(this);
		rightLeg0.setRotationPoint(-1.5F, 0.0F, -1.25F);
		setRotationAngle(rightLeg0, -0.4363F, 0.0F, 0.7854F);
		body.addChild(rightLeg0);
		rightLeg0.cubeList.add(new ModelBox(rightLeg0, 0, 0, -0.5F, -0.25F, -0.5F, 1, 2, 1, 0.0F, false));

		rightLeg1 = new RendererModel(this);
		rightLeg1.setRotationPoint(-1.5F, 0.0F, -0.25F);
		setRotationAngle(rightLeg1, 0.0F, 0.0F, 0.7854F);
		body.addChild(rightLeg1);
		rightLeg1.cubeList.add(new ModelBox(rightLeg1, 0, 0, -0.5F, -0.25F, -0.5F, 1, 2, 1, 0.0F, true));

		rightLeg2 = new RendererModel(this);
		rightLeg2.setRotationPoint(-1.5F, 0.0F, 0.75F);
		setRotationAngle(rightLeg2, 0.4363F, 0.0F, 0.7854F);
		body.addChild(rightLeg2);
		rightLeg2.cubeList.add(new ModelBox(rightLeg2, 0, 0, -0.5F, -0.25F, -0.5F, 1, 2, 1, 0.0F, false));
	}

	@Override
	public void render(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale)
	{
		body.render(scale);
	}

	@Override
	public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor)
	{
		super.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
	}

	@Override
	public void setLivingAnimations(T entityIn, float limbSwing, float limbSwingAmount, float partialTick)
	{
		super.setLivingAnimations(entityIn, limbSwing, limbSwingAmount, partialTick);
	}

	@Override
	public void setModelAttributes(EntityModel<T> entityModel)
	{
		super.setModelAttributes(entityModel);
	}

	public void setRotationAngle(RendererModel modelRenderer, float x, float y, float z)
	{
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}