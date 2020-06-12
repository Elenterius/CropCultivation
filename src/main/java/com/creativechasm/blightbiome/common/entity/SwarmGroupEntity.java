package com.creativechasm.blightbiome.common.entity;

import com.creativechasm.blightbiome.common.entity.ai.goal.FollowSwarmLeaderGoal;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.monster.SpiderEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Stream;

public abstract class SwarmGroupEntity extends SpiderEntity {
    private SwarmGroupEntity leader;
    private int groupSize = 1;

    public static class GroupData implements ILivingEntityData {
        public final SwarmGroupEntity leader;

        public GroupData(SwarmGroupEntity leader) {
            this.leader = leader;
        }
    }

    public SwarmGroupEntity(EntityType<? extends SpiderEntity> type, World worldIn) {
        super(type, worldIn);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(6, new FollowSwarmLeaderGoal(this));
    }

    @Nullable
    @Override
    public ILivingEntityData onInitialSpawn(@Nonnull IWorld worldIn, @Nonnull DifficultyInstance difficultyIn, @Nonnull SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).applyModifier(new AttributeModifier("Random spawn bonus", rand.nextGaussian() * 0.05D, AttributeModifier.Operation.MULTIPLY_BASE));

        if (spawnDataIn == null) {
            spawnDataIn = new SwarmGroupEntity.GroupData(this);
        } else {
            setLeader(((SwarmGroupEntity.GroupData) spawnDataIn).leader);
        }

        return spawnDataIn;
    }

    @Override
    public int getMaxSpawnedInChunk() {
        return getMaxGroupSize();
    }

    public int getMaxGroupSize() {
        return 8;
    }

    protected boolean hasNoLeader() {
        return !this.hasLeader();
    }

    public boolean hasLeader() {
        return leader != null && leader.isAlive();
    }

    public SwarmGroupEntity getLeader() {
        return leader;
    }

    public boolean isLeader() {
        return groupSize > 1;
    }

    public SwarmGroupEntity setLeader(SwarmGroupEntity leader) {
        this.leader = leader;
        leader.increaseGroupSize();
        return leader;
    }

    public void leaveGroup() {
        leader.decreaseGroupSize();
        leader = null;
    }

    private void increaseGroupSize() {
        ++groupSize;
    }

    private void decreaseGroupSize() {
        --groupSize;
    }

    public boolean canGroupGrow() {
        return isLeader() && groupSize < getMaxGroupSize();
    }

    @Override
    public void tick() {
        super.tick();
        if (isLeader() && world.rand.nextInt(200) == 1) {
            List<SwarmGroupEntity> list = world.getEntitiesWithinAABB(getClass(), getBoundingBox().grow(8.0D, 8.0D, 8.0D));
            if (list.size() <= 1) groupSize = 1;
        }
    }

    public boolean inRangeOfLeader() {
        return getDistanceSq(leader) <= 256D;
    }

    public void moveToLeader() {
        if (hasLeader()) getNavigator().tryMoveToEntityLiving(leader, 1.0D);
    }

    public void addGroupMembers(Stream<SwarmGroupEntity> entityStream) {
        entityStream
                .limit(getMaxGroupSize() - groupSize)
                .filter((entity) -> entity != this)
                .forEach((entity) -> entity.setLeader(this));
    }
}