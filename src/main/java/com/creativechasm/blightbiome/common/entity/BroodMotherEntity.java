package com.creativechasm.blightbiome.common.entity;

import com.creativechasm.blightbiome.registry.EntityRegistry;
import net.minecraft.entity.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class BroodMotherEntity extends SwarmGroupEntity {

    private byte delay = 8;
    private byte timer;

    public BroodMotherEntity(EntityType<? extends BroodMotherEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(16D);
        getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
        getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(3.0D);
    }

    @Override
    public boolean isLeader() {
        return true;
    }

    @Override
    public void livingTick() {
        if (!world.isRemote() && ticksExisted % 20 == 0) {
            if (canGroupGrow() && getHealth() < getMaxHealth() * 0.75f) {
                if (timer++ - delay > 0 && getAttackTarget() != null) {
                    timer = 0;
                    spawnBlobInsect();
                }
            }
        }
        super.livingTick();
    }

    private void spawnBlobInsect() {
        BlockPos pos = (new BlockPos(this)).add(-1 + rand.nextInt(4), 0.5, -1 + rand.nextInt(4));
        BlobInsectEntity entity = EntityRegistry.BLOB_INSECT.create(world);
        if (entity != null) {
            entity.moveToBlockPosAndAngles(pos, 0.0F, 0.0F);
            entity.onInitialSpawn(world, world.getDifficultyForLocation(pos), SpawnReason.MOB_SUMMONED, new GroupData(this), null);
            world.addEntity(entity);
        }
    }

    @Override
    protected float getStandingEyeHeight(@Nonnull Pose poseIn, @Nonnull EntitySize sizeIn) {
        return 0.28F;
    }
}
