package com.creativechasm.cropcultivation.optionaldependency;

import com.creativechasm.cropcultivation.CropCultivationMod;
import com.creativechasm.cropcultivation.init.CommonRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.OptionalMod;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.logging.log4j.MarkerManager;

import javax.annotation.Nullable;
import java.util.*;

public class OptionalCommonRegistry
{
    public static boolean isSoybeanAvailable() {
        return Mods.SIMPLE_FARMING.isPresent() || Mods.HARVEST_CRAFT_2_CROPS.isPresent();
    }

    public static boolean isSimpleFarmingCrop(Object owner) {
        return Mods.isSimpleFarmingPresent && SimpleFarmingHandler.isCrop(owner);
    }

    public static boolean isSimpleFarmingDoubleCrop(Block block) {
        return Mods.isSimpleFarmingPresent && SimpleFarmingHandler.isDoubleCrop(block);
    }

    public static class Mods
    {
        public static boolean isSimpleFarmingPresent; //we do this because mixins are applied before the Mod list is loaded

        static {
            try {
                Class.forName("enemeez.simplefarming.SimpleFarming", false, Mods.class.getClassLoader());
                isSimpleFarmingPresent = true;
            }
            catch (Exception e) {
                isSimpleFarmingPresent = false;
            }
        }

        static final Map<OptionalMod<Object>, Class<? extends IOptionalModHandler>> OPTIONAL_HANDLERS = new HashMap<>();
        static final Map<OptionalMod<Object>, IOptionalModHandler> LOADED_MOD_HANDLERS = new HashMap<>();
        static final Set<String> LOADED_MOD_IDS = new HashSet<>();

        static final OptionalMod<Object> FARMING_FOR_BLOCKHEADS = register("farmingforblockheads", FarmingForBlockHeadsHandler.class);
        static final OptionalMod<Object> HARVEST_CRAFT_2_CROPS = register("pamhc2crops", HarvestCraftHandler.class);
        static final OptionalMod<Object> SIMPLE_FARMING = register("simplefarming", SimpleFarmingHandler.class);
        static final OptionalMod<Object> XL_FOOD = register("xlfoodmod", XLFoodHandler.class);

        private static OptionalMod<Object> register(String modId, Class<? extends IOptionalModHandler> handler) {
            OptionalMod<Object> optionalMod = OptionalMod.of(modId);
            OPTIONAL_HANDLERS.put(optionalMod, handler);
            return optionalMod;
        }

        public static boolean isModLoaded(String modId) {
            return LOADED_MOD_IDS.contains(modId);
        }

        public static Optional<IOptionalModHandler> getModHandler(OptionalMod<Object> optionalMod) {
            return Optional.ofNullable(LOADED_MOD_HANDLERS.get(optionalMod));
        }

        public static void onSetup() {
            Mods.OPTIONAL_HANDLERS.forEach((optionalMod, clazz) -> optionalMod.ifPresent(o -> {
                        try {
                            IOptionalModHandler modHandler = clazz.newInstance();
                            modHandler.onSetup();
                            LOADED_MOD_HANDLERS.put(optionalMod, modHandler);
                            LOADED_MOD_IDS.add(optionalMod.getModId());
                        }
                        catch (InstantiationException | IllegalAccessException e) {
                            CropCultivationMod.LOGGER.error(MarkerManager.getMarker("ModCompat"), "failed to setup mod compat for " + optionalMod.getModId(), e);
                        }
                    })
            );
        }
    }

    @Mod.EventBusSubscriber(modid = CropCultivationMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class Main
    {
        @SubscribeEvent
        public static void onItemsRegistry(final RegistryEvent.Register<Item> registryEvent) {
            if (isSoybeanAvailable()) {
                registryEvent.getRegistry().register(new Item(new Item.Properties().group(CommonRegistry.ITEM_GROUP)).setRegistryName("soybean_meal")); //NK fertilizer
            }
        }

        public static void onSetup() {}

        @SubscribeEvent
        public static void registerModifierSerializers(final RegistryEvent.Register<GlobalLootModifierSerializer<?>> event) {}
    }

    @ObjectHolder(CropCultivationMod.MOD_ID)
    public static class Items
    {
        @Nullable
        @ObjectHolder("soybean_meal")
        public static Item SOYBEAN_MEAL;
    }
}
