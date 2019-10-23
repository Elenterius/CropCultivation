package com.creativechasm.blightbiome.client.renderer.entity;

import com.creativechasm.blightbiome.client.renderer.entity.model.BlobInsectModel;
import com.creativechasm.blightbiome.common.entity.BlobInsectEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BlobInsectRenderer<T extends BlobInsectEntity> extends MobRenderer<T, BlobInsectModel<T>>
{
	private static final ResourceLocation TEXTURE = new ResourceLocation("blightbiome", "textures/entity/blob_insect.png");

	public BlobInsectRenderer(EntityRendererManager rendererManager)
	{
		super(rendererManager, new BlobInsectModel<>(), 0.2F);
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