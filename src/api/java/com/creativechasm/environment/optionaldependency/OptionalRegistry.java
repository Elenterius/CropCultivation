package com.creativechasm.environment.optionaldependency;

import com.creativechasm.environment.EnvironmentLib;
import com.creativechasm.environment.init.CommonRegistry;
import net.minecraft.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nullable;

public class OptionalRegistry {

    public static boolean isSoybeanAvailable() {
        return HarvestCraftAddon.getInstance().isModPresent() || SimpleFarmingAddon.getInstance().isModPresent();
    }

    @Mod.EventBusSubscriber(modid = EnvironmentLib.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class Common {
        @SubscribeEvent
        public static void onItemsRegistry(final RegistryEvent.Register<Item> registryEvent) {
            if (isSoybeanAvailable()) {
                registryEvent.getRegistry().register(new Item(new Item.Properties().group(CommonRegistry.ITEM_GROUP)).setRegistryName("soybean_meal")); //NK fertilizer
            }
        }

        public static void onSetup() {
            if (isSoybeanAvailable() && Items.SOYBEAN_MEAL != null) {

            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Mod.EventBusSubscriber(modid = EnvironmentLib.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class Client {
        @SubscribeEvent
        public static void onItemColorRegistry(final ColorHandlerEvent.Item event) {
            if (isSoybeanAvailable()) {
                event.getItemColors().register((stack, index) -> 0x7E9739, Items.SOYBEAN_MEAL);
            }
        }
    }

    @ObjectHolder(EnvironmentLib.MOD_ID)
    public static class Items {

        @Nullable
        @ObjectHolder("soybean_meal")
        public static Item SOYBEAN_MEAL;
    }
}
