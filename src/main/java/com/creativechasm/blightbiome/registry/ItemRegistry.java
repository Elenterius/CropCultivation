package com.creativechasm.blightbiome.registry;

import com.creativechasm.blightbiome.BlightBiomeMod;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BlightBiomeMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ItemRegistry {

    public static final ItemGroup ITEM_GROUP = new ItemGroup(-1, BlightBiomeMod.MOD_ID) {
        @OnlyIn(Dist.CLIENT)
        public ItemStack createIcon() {
            return new ItemStack(BlockRegistry.BLIGHT_SOIL);
        }
    };

    @SubscribeEvent
    public static void onItemsRegistry(final RegistryEvent.Register<Item> registryEvent) {
        Item.Properties properties = new Item.Properties().group(ITEM_GROUP);
        registryEvent.getRegistry().registerAll(
                createItemForBlock(BlockRegistry.BLIGHT_SOIL, properties),
                createItemForBlock(BlockRegistry.BLIGHT_WEED, properties),
                createItemForBlock(BlockRegistry.BLIGHT_SOIL_SLAB, properties),
                createItemForBlock(BlockRegistry.BLIGHT_MOSS, properties),
                createItemForBlock(BlockRegistry.BLIGHT_MUSHROOM_TALL, properties),
                createItemForBlock(BlockRegistry.BLIGHT_MAIZE, properties),
                createItemForBlock(BlockRegistry.BLIGHT_SPROUT, properties),
                createItemForBlock(BlockRegistry.BLIGHT_SPROUT_SMALL, properties),
                createItemForBlock(BlockRegistry.LILY_TREE_SAPLING, properties),

                createItemForBlock(BlockRegistry.BLOOMING_FLOWER_TEST, properties)
        );

//        properties = new Item.Properties().group(ItemGroup.MISC);
        registryEvent.getRegistry().registerAll(
                createSpawnEggItem(EntityRegistry.BLOB_INSECT, 0x4B2277, 0xAF27E0, properties),
                createSpawnEggItem(EntityRegistry.BROOD_MOTHER, 0x4B2277, 0xCF7DEC, properties),
                createSpawnEggItem(EntityRegistry.BLIGHT_BROOD, 0x4B2277, 0xE7BEF5, properties),
                createSpawnEggItem(EntityRegistry.PESTERER, 0x6e4e92, 0xBF52E6, properties),
                createSpawnEggItem(EntityRegistry.BELL_SLIME, 0x5c7515, 0x00cc00, properties)
        );
    }

    private static Item createItemForBlock(Block block, Item.Properties properties) {
        //noinspection ConstantConditions
        return new BlockItem(block, properties).setRegistryName(block.getRegistryName());
    }

    private static <T extends Entity> Item createSpawnEggItem(EntityType<T> entityType, int primaryColor, int secondaryColor, Item.Properties properties) {
        //noinspection ConstantConditions
        return new SpawnEggItem(entityType, primaryColor, secondaryColor, properties).setRegistryName("spawn_egg_" + entityType.getRegistryName().getPath());
    }
}
