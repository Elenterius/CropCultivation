package com.creativechasm.blightbiome.common.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.world.World;

public class BlobInsectEntity extends MonsterEntity
{
	protected BlobInsectEntity(EntityType<? extends MonsterEntity> type, World worldIn)
	{
		super(type, worldIn);
	}

	protected BlobInsectEntity(World worldIn)
	{
//		EntityType<SpiderEntity> SPIDER = register("spider", EntityType.Builder.create(SpiderEntity::new, EntityClassification.MONSTER).size(1.4F, 0.9F));
		super(EntityType.SPIDER, worldIn);
	}
}
