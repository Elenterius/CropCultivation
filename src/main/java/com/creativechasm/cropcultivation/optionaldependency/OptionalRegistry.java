package com.creativechasm.cropcultivation.optionaldependency;

import com.creativechasm.cropcultivation.CropCultivationMod;
import com.creativechasm.cropcultivation.init.CommonRegistry;
import net.minecraft.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.OptionalMod;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.logging.log4j.MarkerManager;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class OptionalRegistry
{
    public static boolean isSoybeanAvailable() {
        return Mods.SIMPLE_FARMING.isPresent() || Mods.HARVEST_CRAFT_2_CROPS.isPresent();
    }

    public static class Mods {
        static final Map<OptionalMod<Object>, Class<? extends IOptionalModHandler>> OPTIONAL_HANDLERS = new HashMap<>();

        //Class.forName(className, false, OptionalMod.class.getClassLoader())
        static final OptionalMod<Object> FARMING_FOR_BLOCKHEADS = register("farmingforblockheads", FarmingForBlockHeadsHandler.class);
        static final OptionalMod<Object> HARVEST_CRAFT_2_CROPS = register("pamhc2crops", HarvestCraftHandler.class);
        static final OptionalMod<Object> SIMPLE_FARMING = register("simplefarming", SimpleFarmingHandler.class);
        static final OptionalMod<Object> XL_FOOD = register("xlfoodmod", XLFoodHandler.class);

        private static OptionalMod<Object> register(String modId, Class<? extends IOptionalModHandler> handler) {
            OptionalMod<Object> optionalMod = OptionalMod.of(modId);
            OPTIONAL_HANDLERS.put(optionalMod, handler);
            return optionalMod;
        }
    }

    @Mod.EventBusSubscriber(modid = CropCultivationMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class Common
    {
        @SubscribeEvent
        public static void onItemsRegistry(final RegistryEvent.Register<Item> registryEvent) {
            if (isSoybeanAvailable()) {
                registryEvent.getRegistry().register(new Item(new Item.Properties().group(CommonRegistry.ITEM_GROUP)).setRegistryName("soybean_meal")); //NK fertilizer
            }
        }

        public static void onSetup() {
            Mods.OPTIONAL_HANDLERS.forEach((optionalMod, clazz) -> {
                optionalMod.ifPresent(o -> {
                    try {
                        clazz.newInstance().onSetup();
                    }
                    catch (InstantiationException | IllegalAccessException e) {
                        CropCultivationMod.LOGGER.error(MarkerManager.getMarker("ModCompat"), "failed to setup mod compat for " + optionalMod.getModId(), e);
                    }
                });
            });
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Mod.EventBusSubscriber(modid = CropCultivationMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class Client
    {
        @SubscribeEvent
        public static void onItemColorRegistry(final ColorHandlerEvent.Item event) {
            if (isSoybeanAvailable()) {
                event.getItemColors().register((stack, index) -> 0x7E9739, Items.SOYBEAN_MEAL);
            }
        }
    }

    @ObjectHolder(CropCultivationMod.MOD_ID)
    public static class Items
    {
        @Nullable
        @ObjectHolder("soybean_meal")
        public static Item SOYBEAN_MEAL;
    }
}
