package com.creativechasm.cropcultivation.handler;

import com.creativechasm.cropcultivation.CropCultivationMod;
import net.minecraft.util.IWorldPosCallable;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CropCultivationMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public abstract class CraftingHandler
{
    public interface IWorldPosCallableProvider {
        IWorldPosCallable getWorldPosCallable();
    }

    // we forgo to use events because we don't need the flexibility at the moment. we only spawn one item on crafting

//    @SubscribeEvent
//    public static void onStoneCut(StoneCutEvent event) {
//
//    }
//
//    public static void firePlayerStoneCutEvent(PlayerEntity player, ItemStack crafted, IWorldPosCallable callable) {
//        MinecraftForge.EVENT_BUS.post(new StoneCutEvent(player, crafted, callable));
//    }
//
//    public static class StoneCutEvent extends PlayerEvent
//    {
//        private final ItemStack crafted;
//        private final IWorldPosCallable callable;
//
//        public StoneCutEvent(PlayerEntity player, @Nonnull ItemStack crafting, @Nonnull IWorldPosCallable callable) {
//            super(player);
//            this.crafted = crafting;
//            this.callable = callable;
//        }
//
//        @Nonnull
//        public ItemStack getCrafted() {
//            return this.crafted;
//        }
//
//        @Nonnull
//        public IWorldPosCallable getWorldPosCallable() {
//            return callable;
//        }
//    }
}