package com.creativechasm.cropcultivation.trigger;

import net.minecraft.advancements.CriteriaTriggers;

public class ModTriggers
{
    public static final DeadCropDestroyedTrigger DEAD_CROP_DESTROYED = new DeadCropDestroyedTrigger();
    public static final DeadCropWitheredDestroyedTrigger DEAD_CROP_WITHERED_DESTROYED = new DeadCropWitheredDestroyedTrigger();
    public static final DeadCropRottenDestroyedTrigger DEAD_CROP_ROTTEN_DESTROYED = new DeadCropRottenDestroyedTrigger();
    public static final WeedDestroyedTrigger WEED_DESTROYED = new WeedDestroyedTrigger();
    public static final PotassiumExplosionTrigger POTASSIUM_EXPLOSION = new PotassiumExplosionTrigger();

    public static void register() {
        CriteriaTriggers.register(ModTriggers.DEAD_CROP_DESTROYED);
        CriteriaTriggers.register(ModTriggers.WEED_DESTROYED);
        CriteriaTriggers.register(ModTriggers.DEAD_CROP_ROTTEN_DESTROYED);
        CriteriaTriggers.register(ModTriggers.DEAD_CROP_WITHERED_DESTROYED);
        CriteriaTriggers.register(ModTriggers.POTASSIUM_EXPLOSION);
    }
}
