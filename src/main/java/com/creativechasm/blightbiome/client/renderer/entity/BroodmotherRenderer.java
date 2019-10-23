package com.creativechasm.blightbiome.client.renderer.entity;

import com.creativechasm.blightbiome.client.renderer.entity.model.BroodmotherModel;
import com.creativechasm.blightbiome.common.entity.BroodmotherEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BroodmotherRenderer<T extends BroodmotherEntity> extends MobRenderer<T, BroodmotherModel<T>>
{
	private static final ResourceLocation TEXTURE = new ResourceLocation("blightbiome", "textures/entity/broodmother.png");

	public BroodmotherRenderer(EntityRendererManager rendererManager)
	{
		super(rendererManager, new BroodmotherModel<>(), 0.8F);
//		this.addLayer(new SpiderEyesLayer(this));
	}

	protected float getDeathMaxRotation(T entityLivingBaseIn)
	{
		return 180.0F;
	}

	protected ResourceLocation getEntityTexture(@SuppressWarnings("NullableProblems") T entityLivingBaseIn)
	{
		return TEXTURE;
	}
}