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
public class WeedDestroyedTrigger extends AbstractCriterionTrigger<WeedDestroyedTrigger.Instance>
{
    private static final ResourceLocation ID = new ResourceLocation("weed_destroyed");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public WeedDestroyedTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
        return new WeedDestroyedTrigger.Instance();
    }

    public void trigger(ServerPlayerEntity player) {
        func_227070_a_(player.getAdvancements(), Instance::test);
    }

    public static class Instance extends CriterionInstance
    {
        public Instance() {
            super(WeedDestroyedTrigger.ID);
        }

        public boolean test() {
            return true;
        }
    }
}
