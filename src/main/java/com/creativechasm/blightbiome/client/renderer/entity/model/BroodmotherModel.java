package com.creativechasm.blightbiome.client.renderer.entity.model;
//Made by Elenterius

import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.client.renderer.model.ModelBox;
import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BroodmotherModel<T extends Entity> extends EntityModel<T>
{
	private final RendererModel abdomen;
	private final RendererModel body;
	private final RendererModel leftLeg0;
	private final RendererModel head;
	private final RendererModel bottomJaw;
	private final RendererModel topJaw;
	private final RendererModel leftLeg1;
	private final RendererModel leftLeg2;
	private final RendererModel leftLeg3;
	private final RendererModel rightLeg0;
	private final RendererModel rightLeg1;
	private final RendererModel rightLeg2;
	private final RendererModel rightLeg3;

	public BroodmotherModel()
	{
		textureWidth = 64;
		textureHeight = 64;

		abdomen = new RendererModel(this);
		abdomen.setRotationPoint(0.0F, 19.0F, 3.0F);
		setRotationAngle(abdomen, 0.3491F, 0.0F, 0.0F);
		abdomen.cubeList.add(new ModelBox(abdomen, 0, 14, -5.0F, -4.0F, 0.0F, 10, 9, 12, 0.0F, true));
		abdomen.cubeList.add(new ModelBox(abdomen, 8, 35, -3.5F, -3.0F, 12.0F, 7, 7, 2, 0.0F, true));

		body = new RendererModel(this);
		body.setRotationPoint(0.0F, 20.0F, 0.0F);
		setRotationAngle(body, 0.1396F, 0.0F, 0.0F);
		body.cubeList.add(new ModelBox(body, 0, 0, -3.0F, -3.0F, -3.0F, 6, 6, 8, 0.0F, true));

		leftLeg0 = new RendererModel(this);
		leftLeg0.setRotationPoint(4.0F, 20.0F, -1.0F);
		setRotationAngle(leftLeg0, 0.0F, 0.7854F, 0.4363F);
		leftLeg0.cubeList.add(new ModelBox(leftLeg0, 21, 0, -1.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F, true));

		head = new RendererModel(this);
		head.setRotationPoint(0.0F, 20.0F, -3.0F);
		setRotationAngle(head, 0.0698F, 0.0F, 0.0F);
		head.cubeList.add(new ModelBox(head, 32, 4, -2.5F, -4.0F, -5.0F, 5, 6, 6, 0.0F, true));
		head.cubeList.add(new ModelBox(head, 1, 37, -3.0F, -2.75F, -4.2F, 1, 1, 1, 0.0F, true));
		head.cubeList.add(new ModelBox(head, 1, 37, 2.0F, -2.75F, -4.2F, 1, 1, 1, 0.0F, true));
		head.cubeList.add(new ModelBox(head, 1, 37, -3.0F, -1.7F, -2.6F, 1, 1, 1, 0.0F, true));
		head.cubeList.add(new ModelBox(head, 1, 37, 2.0F, -1.7F, -2.6F, 1, 1, 1, 0.0F, false));
		head.cubeList.add(new ModelBox(head, 1, 37, -3.0F, -3.35F, -2.65F, 1, 1, 1, 0.0F, true));
		head.cubeList.add(new ModelBox(head, 1, 37, 2.0F, -3.35F, -2.65F, 1, 1, 1, 0.0F, false));

		bottomJaw = new RendererModel(this);
		bottomJaw.setRotationPoint(0.0F, 0.5F, -5.0F);
		head.addChild(bottomJaw);
		bottomJaw.cubeList.add(new ModelBox(bottomJaw, 42, 43, -1.5F, -0.7989F, -6.964F, 3, 2, 8, 0.0F, false));

		topJaw = new RendererModel(this);
		topJaw.setRotationPoint(0.0F, -2.5F, -5.0F);
		head.addChild(topJaw);
		topJaw.cubeList.add(new ModelBox(topJaw, 32, 20, -2.0F, -0.55F, -4.0F, 4, 2, 4, 0.0F, true));
		topJaw.cubeList.add(new ModelBox(topJaw, 48, 21, -1.5F, -0.55F, -7.0F, 3, 2, 3, 0.0F, true));
		topJaw.cubeList.add(new ModelBox(topJaw, 46, 35, -1.0F, -1.05F, -7.0F, 2, 1, 7, 0.0F, true));
		topJaw.cubeList.add(new ModelBox(topJaw, 46, 35, -1.0F, -1.55F, -8.0F, 2, 4, 1, 0.0F, true));

		leftLeg1 = new RendererModel(this);
		leftLeg1.setRotationPoint(4.0F, 20.0F, 0.0F);
		setRotationAngle(leftLeg1, 0.0F, 0.2618F, 0.4363F);
		leftLeg1.cubeList.add(new ModelBox(leftLeg1, 21, 0, -1.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F, true));

		leftLeg2 = new RendererModel(this);
		leftLeg2.setRotationPoint(4.0F, 20.0F, 1.0F);
		setRotationAngle(leftLeg2, 0.0F, -0.0873F, 0.2618F);
		leftLeg2.cubeList.add(new ModelBox(leftLeg2, 21, 0, -1.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F, true));

		leftLeg3 = new RendererModel(this);
		leftLeg3.setRotationPoint(4.0F, 20.0F, 2.0F);
		setRotationAngle(leftLeg3, 0.0F, -0.4363F, 0.2618F);
		leftLeg3.cubeList.add(new ModelBox(leftLeg3, 21, 0, -1.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F, true));

		rightLeg0 = new RendererModel(this);
		rightLeg0.setRotationPoint(-4.0F, 20.0F, -1.0F);
		setRotationAngle(rightLeg0, 0.0F, -0.8727F, -0.3491F);
		rightLeg0.cubeList.add(new ModelBox(rightLeg0, 21, 0, -15.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F, false));

		rightLeg1 = new RendererModel(this);
		rightLeg1.setRotationPoint(-4.0F, 20.0F, 0.0F);
		setRotationAngle(rightLeg1, 0.0F, -0.3491F, -0.3491F);
		rightLeg1.cubeList.add(new ModelBox(rightLeg1, 21, 0, -15.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F, false));

		rightLeg2 = new RendererModel(this);
		rightLeg2.setRotationPoint(-4.0F, 20.0F, 1.0F);
		setRotationAngle(rightLeg2, 0.0F, 0.0F, -0.3491F);
		rightLeg2.cubeList.add(new ModelBox(rightLeg2, 21, 0, -15.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F, false));

		rightLeg3 = new RendererModel(this);
		rightLeg3.setRotationPoint(-4.0F, 20.0F, 2.0F);
		setRotationAngle(rightLeg3, 0.0F, 0.5236F, -0.4363F);
		rightLeg3.cubeList.add(new ModelBox(rightLeg3, 21, 0, -15.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F, false));
	}

	@Override
	public void render(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale)
	{
		abdomen.render(scale);
		body.render(scale);
		leftLeg0.render(scale);
		head.render(scale);
		leftLeg1.render(scale);
		leftLeg2.render(scale);
		leftLeg3.render(scale);
		rightLeg0.render(scale);
		rightLeg1.render(scale);
		rightLeg2.render(scale);
		rightLeg3.render(scale);
	}

	public void setRotationAngle(RendererModel RendererModel, float x, float y, float z)
	{
		RendererModel.rotateAngleX = x;
		RendererModel.rotateAngleY = y;
		RendererModel.rotateAngleZ = z;
	}
}