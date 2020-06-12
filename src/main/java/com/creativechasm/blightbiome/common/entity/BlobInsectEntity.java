package com.creativechasm.blightbiome.common.entity;

import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.DamageSource;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class BlobInsectEntity extends SwarmGroupEntity {

    public BlobInsectEntity(EntityType<? extends BlobInsectEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(5D);
        getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.2D);
        getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(1.0D);
    }

    @Override
    public boolean inRangeOfLeader() {
        double dist = getDistanceSq(getLeader());
        return dist >= 9D && dist <= 256D;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        return super.attackEntityFrom(source, amount);
    }

    @Override
    public void onDeath(@Nonnull DamageSource cause) {
        super.onDeath(cause);
        if (!world.isRemote && !cause.isUnblockable()) {
            Explosion.Mode mode = net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(world, this) ? Explosion.Mode.DESTROY : Explosion.Mode.NONE;
            world.createExplosion(this, this.getPosX(), this.getPosY(), this.getPosZ(), 0.75f, mode);
        }
    }

    @Override
    protected float getStandingEyeHeight(@Nonnull Pose poseIn, @Nonnull EntitySize sizeIn) {
        return 0.16F;
    }
}
