package com.creativechasm.blightbiome.common.entity.ai.goal;

import com.creativechasm.blightbiome.common.entity.SwarmGroupEntity;
import net.minecraft.entity.ai.goal.Goal;

import java.util.List;
import java.util.function.Predicate;

public class FollowSwarmLeaderGoal extends Goal {
    private final SwarmGroupEntity taskOwner;
    private int navigateTimer;
    private int joinGroupDelay;

    public FollowSwarmLeaderGoal(SwarmGroupEntity taskOwner) {
        this.taskOwner = taskOwner;
        joinGroupDelay = rndDelay(taskOwner);
    }

    protected int rndDelay(SwarmGroupEntity taskOwner) {
        return 200 + taskOwner.getRNG().nextInt(200) % 20;
    }

    @Override
    public boolean shouldExecute() {
        if (taskOwner.isLeader()) {
            return false;
        } else if (taskOwner.hasLeader()) {
            return true;
        } else if (joinGroupDelay > 0) {
            --joinGroupDelay;
            return false;
        } else {
            joinGroupDelay = rndDelay(taskOwner);
            Predicate<SwarmGroupEntity> predicate = (entity) -> entity.canGroupGrow() || !entity.hasLeader();
            List<SwarmGroupEntity> list = taskOwner.world.getEntitiesWithinAABB(SwarmGroupEntity.class, taskOwner.getBoundingBox().grow(8.0D, 8.0D, 8.0D), predicate);
            SwarmGroupEntity groupLeader = list.stream().filter(SwarmGroupEntity::canGroupGrow).findAny().orElse(taskOwner);
            groupLeader.addGroupMembers(list.stream().filter((entity) -> !entity.hasLeader()));
            return taskOwner.hasLeader();
        }
    }

    @Override
    public boolean shouldContinueExecuting() {
        return taskOwner.hasLeader() && taskOwner.inRangeOfLeader();
    }

    @Override
    public void startExecuting() {
        navigateTimer = 0;
    }

    @Override
    public void resetTask() {
        taskOwner.leaveGroup();
    }

    public void tick() {
        if (--navigateTimer <= 0) {
            navigateTimer = 10;
            taskOwner.moveToLeader();
        }
    }
}
