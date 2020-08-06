package com.creativechasm.cropcultivation.trigger;

import net.minecraft.advancements.CriteriaTriggers;

public class ModTriggers
{
    public static final DeadCropDestroyedTrigger DEAD_CROP_DESTROYED = new DeadCropDestroyedTrigger();

    public static void register() {
        CriteriaTriggers.register(ModTriggers.DEAD_CROP_DESTROYED);
    }
}
