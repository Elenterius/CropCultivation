package com.creativechasm.cropcultivation.trigger;

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
public class DeadCropDestroyedTrigger extends AbstractCriterionTrigger<DeadCropDestroyedTrigger.Instance>
{
    private static final ResourceLocation ID = new ResourceLocation("dead_crop_destroyed");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public DeadCropDestroyedTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
        return new DeadCropDestroyedTrigger.Instance();
    }

    public void trigger(ServerPlayerEntity player) {
        func_227070_a_(player.getAdvancements(), Instance::test);
    }

    public static class Instance extends CriterionInstance
    {
        public Instance() {
            super(DeadCropDestroyedTrigger.ID);
        }

        public boolean test() {
            return true;
        }
    }
}
