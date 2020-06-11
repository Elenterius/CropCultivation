package com.creativechasm.blightbiome.common.registry;

import com.creativechasm.blightbiome.BlightBiomeMod;
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
        registryEvent.getRegistry().register(new BlockItem(BlockRegistry.BLIGHT_SOIL, properties).setRegistryName("blightsoil"));
        registryEvent.getRegistry().register(new BlockItem(BlockRegistry.BLIGHT_WEED, properties).setRegistryName("blightweeds"));
        registryEvent.getRegistry().register(new BlockItem(BlockRegistry.BLIGHT_SOIL_SLAB, properties).setRegistryName("blightsoil_slab"));
        registryEvent.getRegistry().register(new BlockItem(BlockRegistry.BLIGHT_MOSS, properties).setRegistryName("blightmoss"));
        registryEvent.getRegistry().register(new BlockItem(BlockRegistry.BLIGHT_MUSHROOM_TALL, new Item.Properties().group(ITEM_GROUP)).setRegistryName("blight_shroom_tall"));
        registryEvent.getRegistry().register(new BlockItem(BlockRegistry.BLIGHT_MAIZE, new Item.Properties().group(ITEM_GROUP)).setRegistryName("blight_maize"));
        registryEvent.getRegistry().register(new BlockItem(BlockRegistry.BLIGHT_SPROUT, new Item.Properties().group(ITEM_GROUP)).setRegistryName("blight_sprout"));
        registryEvent.getRegistry().register(new BlockItem(BlockRegistry.BLIGHT_SPROUT_SMALL, new Item.Properties().group(ITEM_GROUP)).setRegistryName("blight_sprout_small"));

//        properties = new Item.Properties().group(ItemGroup.MISC);
        registryEvent.getRegistry().register(new SpawnEggItem(EntityRegistry.BLOB_INSECT, 0x4B2277, 0xAF27E0, properties).setRegistryName("spawn_egg_blob_insect"));
        registryEvent.getRegistry().register(new SpawnEggItem(EntityRegistry.BROOD_MOTHER, 0x932C3B, 0x47415E, properties).setRegistryName("spawn_egg_broodmother"));
        registryEvent.getRegistry().register(new SpawnEggItem(EntityRegistry.BLIGHT_BROOD, 0x932C3B, 0x47415E, properties).setRegistryName("spawn_egg_blight_brood"));
        registryEvent.getRegistry().register(new SpawnEggItem(EntityRegistry.PESTERER, 0x384740, 0x5E8C6C, properties).setRegistryName("spawn_egg_pesterer"));
    }
}
