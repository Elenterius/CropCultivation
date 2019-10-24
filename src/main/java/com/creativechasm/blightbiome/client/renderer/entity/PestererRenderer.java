package com.creativechasm.blightbiome.client.renderer.entity;

import com.creativechasm.blightbiome.client.renderer.entity.model.PestererModel;
import com.creativechasm.blightbiome.common.entity.PestererEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PestererRenderer<T extends PestererEntity> extends MobRenderer<T, PestererModel<T>>
{
	private static final ResourceLocation TEXTURE = new ResourceLocation("blightbiome", "textures/entity/pesterer.png");

	public PestererRenderer(EntityRendererManager rendererManager)
	{
		super(rendererManager, new PestererModel<>(), 0.2F);
//		this.addLayer(new SpiderEyesLayer(this));
	}

	protected float getDeathMaxRotation(T entityLivingBaseIn)
	{
		return 180.0F;
	}

	protected ResourceLocation getEntityTexture(T entityLivingBaseIn)
	{
		return TEXTURE;
	}
}