package com.creativechasm.cropcultivation.trigger;

import com.creativechasm.cropcultivation.init.ModTriggers;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.criterion.AbstractCriterionTrigger;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class DeadCropRottenDestroyedTrigger extends AbstractCriterionTrigger<DeadCropRottenDestroyedTrigger.Instance>
{
    private static final ResourceLocation ID = new ResourceLocation("dead_crop_rotten_destroyed");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public DeadCropRottenDestroyedTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
        return new DeadCropRottenDestroyedTrigger.Instance();
    }

    public void trigger(ServerPlayerEntity player) {
        ModTriggers.DEAD_CROP_DESTROYED.trigger(player);
        func_227070_a_(player.getAdvancements(), Instance::test);
    }

    public static class Instance extends CriterionInstance
    {
        public Instance() {
            super(DeadCropRottenDestroyedTrigger.ID);
        }

        public boolean test() {
            return true;
        }
    }
}
