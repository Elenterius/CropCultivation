package com.creativechasm.cropcultivation.optionaldependency;

import com.creativechasm.cropcultivation.CropCultivationMod;
import com.creativechasm.cropcultivation.init.CommonRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.OptionalMod;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.logging.log4j.MarkerManager;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class OptionalRegistry
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

        static final OptionalMod<Object> FARMING_FOR_BLOCKHEADS = register("farmingforblockheads", FarmingForBlockHeadsHandler.class);
        static final OptionalMod<Object> HARVEST_CRAFT_2_CROPS = register("pamhc2crops", HarvestCraftHandler.class);
        static final OptionalMod<Object> SIMPLE_FARMING = register("simplefarming", SimpleFarmingHandler.class);
        static final OptionalMod<Object> XL_FOOD = register("xlfoodmod", XLFoodHandler.class);

        private static OptionalMod<Object> register(String modId, Class<? extends IOptionalModHandler> handler) {
            OptionalMod<Object> optionalMod = OptionalMod.of(modId);
            OPTIONAL_HANDLERS.put(optionalMod, handler);
            return optionalMod;
        }

        public static Optional<IOptionalModHandler> getModHandler(OptionalMod<Object> optionalMod) {
            return Optional.ofNullable(LOADED_MOD_HANDLERS.get(optionalMod));
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
            Mods.OPTIONAL_HANDLERS.forEach((optionalMod, clazz) -> optionalMod.ifPresent(o -> {
                        try {
                            IOptionalModHandler modHandler = clazz.newInstance();
                            modHandler.onSetup();
                            Mods.LOADED_MOD_HANDLERS.put(optionalMod, modHandler);
                        }
                        catch (InstantiationException | IllegalAccessException e) {
                            CropCultivationMod.LOGGER.error(MarkerManager.getMarker("ModCompat"), "failed to setup mod compat for " + optionalMod.getModId(), e);
                        }
                    })
            );
        }

        @SubscribeEvent
        public static void registerModifierSerializers(final RegistryEvent.Register<GlobalLootModifierSerializer<?>> event) {

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
