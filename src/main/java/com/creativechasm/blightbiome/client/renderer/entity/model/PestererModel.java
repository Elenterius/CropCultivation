package com.creativechasm.blightbiome.client.renderer.entity.model;
//Made by Elenterius

import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.client.renderer.model.ModelBox;
import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


@OnlyIn(Dist.CLIENT)
public class PestererModel<T extends Entity> extends EntityModel<T>
{
	private final RendererModel body;
	private final RendererModel head;
	private final RendererModel topHead;
	private final RendererModel leftLeg;
	private final RendererModel spore;
	private final RendererModel leftArm;
	private final RendererModel rightLeg;
	private final RendererModel rightArm;

	public PestererModel()
	{
		textureWidth = 64;
		textureHeight = 64;

		body = new RendererModel(this);
		body.setRotationPoint(0.0F, -1.6F, 0.0F);
		body.cubeList.add(new ModelBox(body, 32, 0, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.0F, true));

		head = new RendererModel(this);
		head.setRotationPoint(0.0F, 0.0F, 0.0F);
		body.addChild(head);
		head.cubeList.add(new ModelBox(head, 0, 33, -4.0F, -1.0F, -4.0F, 8, 1, 8, 0.0F, true));
		head.cubeList.add(new ModelBox(head, 0, 21, -4.0F, -6.0F, -4.0F, 8, 5, 7, 0.0F, true));
		head.cubeList.add(new ModelBox(head, 0, 15, -4.0F, -6.0F, 3.0F, 8, 5, 1, 0.0F, true));

		topHead = new RendererModel(this);
		topHead.setRotationPoint(0.0F, 0.0F, 0.0F);
		head.addChild(topHead);
		topHead.cubeList.add(new ModelBox(topHead, 0, 0, -4.0F, -13.0F, -4.0F, 8, 7, 8, 0.0F, true));

		leftLeg = new RendererModel(this);
		leftLeg.setRotationPoint(-2.0F, 12.0F, 0.0F);
		body.addChild(leftLeg);
		leftLeg.cubeList.add(new ModelBox(leftLeg, 56, 0, -1.0F, 0.0F, -1.0F, 2, 14, 2, 0.0F, false));

		spore = new RendererModel(this);
		spore.setRotationPoint(0.0F, 7.0F, -2.5F);
		body.addChild(spore);
		spore.cubeList.add(new ModelBox(spore, 32, 16, -3.0F, -4.0F, -1.5F, 6, 8, 3, 0.0F, true));

		leftArm = new RendererModel(this);
		leftArm.setRotationPoint(-5.0F, 2.5F, 0.0F);
		setRotationAngle(leftArm, -2.3562F, 0.9599F, 0.0873F);
		body.addChild(leftArm);
		leftArm.cubeList.add(new ModelBox(leftArm, 56, 0, -1.0F, -2.0F, -1.0F, 2, 25, 2, 0.0F, false));

		rightLeg = new RendererModel(this);
		rightLeg.setRotationPoint(2.0F, 12.0F, 0.0F);
		body.addChild(rightLeg);
		rightLeg.cubeList.add(new ModelBox(rightLeg, 56, 0, -1.0F, 0.0F, -1.0F, 2, 14, 2, 0.0F, true));

		rightArm = new RendererModel(this);
		rightArm.setRotationPoint(5.0F, 2.5F, 0.0F);
		setRotationAngle(rightArm, -2.618F, -0.6981F, 0.0F);
		body.addChild(rightArm);
		rightArm.cubeList.add(new ModelBox(rightArm, 56, 0, -1.0F, -2.0F, -1.0F, 2, 25, 2, 0.0F, true));
	}

	@Override
	public void render(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale)
	{
		body.render(scale);
	}

	public void setRotationAngle(RendererModel RendererModel, float x, float y, float z)
	{
		RendererModel.rotateAngleX = x;
		RendererModel.rotateAngleY = y;
		RendererModel.rotateAngleZ = z;
	}
}