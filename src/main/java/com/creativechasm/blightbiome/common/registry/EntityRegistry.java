package com.creativechasm.blightbiome.common.registry;

import com.creativechasm.blightbiome.BlightBiomeMod;
import com.creativechasm.blightbiome.common.entity.BlobInsectEntity;
import com.creativechasm.blightbiome.common.entity.BroodMotherEntity;
import com.creativechasm.blightbiome.common.entity.BroodlingEntity;
import com.creativechasm.blightbiome.common.entity.PestererEntity;
import net.minecraft.entity.*;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

@Mod.EventBusSubscriber(modid = BlightBiomeMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EntityRegistry {
    public final static EntityType<BlobInsectEntity> BLOB_INSECT = registerEntity(EntityType.Builder.create(BlobInsectEntity::new, EntityClassification.MONSTER).size(0.4F, 0.35F), "blob_insect");
    public final static EntityType<BroodMotherEntity> BROOD_MOTHER = registerEntity(EntityType.Builder.create(BroodMotherEntity::new, EntityClassification.MONSTER).size(1.6F, 0.7F), "brood_mother");
    public final static EntityType<BroodlingEntity> BLIGHT_BROOD = registerEntity(EntityType.Builder.create(BroodlingEntity::new, EntityClassification.MONSTER).size(1.6F, 0.7F), "blight_brood");
    public final static EntityType<PestererEntity> PESTERER = registerEntity(EntityType.Builder.create(PestererEntity::new, EntityClassification.MONSTER).size(1.0F, 1.5F), "pesterer");

    public final static EntityType<SlimeEntity> BELL_SLIME = registerEntity(EntityType.Builder.create(SlimeEntity::new, EntityClassification.MONSTER).size(2.04F, 2.04F), "bell_slime");

    private static <T extends Entity> EntityType<T> registerEntity(EntityType.Builder<T> builder, String entityName) {
        EntityType<T> entityType = builder.build(entityName);
        entityType.setRegistryName(BlightBiomeMod.MOD_ID, entityName);
        return entityType;
    }

    @SubscribeEvent
    public static void onEntityTypeRegistry(final RegistryEvent.Register<EntityType<?>> registryEvent) {
        registryEvent.getRegistry().register(BLOB_INSECT);
        registryEvent.getRegistry().register(BROOD_MOTHER);
        registryEvent.getRegistry().register(BLIGHT_BROOD);
        registryEvent.getRegistry().register(PESTERER);

        registryEvent.getRegistry().register(BELL_SLIME);

        EntitySpawnPlacementRegistry.register(BLOB_INSECT, EntitySpawnPlacementRegistry.PlacementType.NO_RESTRICTIONS, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EntityRegistry::monsterCondition);
    }

    private static boolean monsterCondition(EntityType<? extends MonsterEntity> entityType, IWorld world, SpawnReason spawnReason, BlockPos pos, Random random) {
        return world.getDifficulty() != Difficulty.PEACEFUL && mobCondition(entityType, world, spawnReason, pos, random); // && lightCondition(world, pos, random)
    }

    private static boolean mobCondition(EntityType<? extends MobEntity> entityType, IWorld world, SpawnReason spawnReason, BlockPos pos, Random random) {
        BlockPos blockpos = pos.down();
        return spawnReason == SpawnReason.SPAWNER || world.getWorld().getBlockState(blockpos).canEntitySpawn(world, blockpos, entityType);
    }

}
