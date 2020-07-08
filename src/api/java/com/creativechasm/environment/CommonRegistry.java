package com.creativechasm.environment;

import com.creativechasm.environment.api.block.LibBlocks;
import com.creativechasm.environment.api.block.SoilBlock;
import com.creativechasm.environment.api.block.SoilStateTileEntity;
import com.creativechasm.environment.api.soil.SoilTexture;
import com.creativechasm.environment.common.item.SoilTestKitItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;


@Mod.EventBusSubscriber(modid = EnvironmentLib.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonRegistry {

    @ObjectHolder(EnvironmentLib.MOD_ID + ":farm_soil")
    public static TileEntityType<?> FARM_SOIL;

    @SubscribeEvent
    public static void onBlocksRegistry(final RegistryEvent.Register<Block> registryEvent) {
        registryEvent.getRegistry().registerAll(
                new Block(Block.Properties.create(Material.EARTH).hardnessAndResistance(0.65F).sound(SoundType.GROUND)).setRegistryName("silt"),
                createSoilBlock(Block.Properties.create(Material.EARTH).hardnessAndResistance(0.6F).sound(SoundType.GROUND), SoilTexture.LOAM, "loam_soil"),
                createSoilBlock(Block.Properties.create(Material.EARTH).hardnessAndResistance(0.65F).sound(SoundType.GROUND), SoilTexture.SILT, "silt_soil"),
                createSoilBlock(Block.Properties.create(Material.SAND).hardnessAndResistance(0.5F).sound(SoundType.GROUND), SoilTexture.SAND, "sand_soil"),
                createSoilBlock(Block.Properties.create(Material.CLAY).hardnessAndResistance(0.8F).sound(SoundType.GROUND), SoilTexture.CLAY, "clay_soil")
        );
    }

    private static SoilBlock createSoilBlock(Block.Properties properties, SoilTexture texture, String registryName) {
        SoilBlock block = new SoilBlock(properties, texture) {
            @Override
            public TileEntity createTileEntity(BlockState state, IBlockReader world) {
                return FARM_SOIL.create();
            }
        };
        block.setRegistryName(registryName);
        return block;
    }

    @SubscribeEvent
    public static void onTileEntityTypeRegistry(final RegistryEvent.Register<TileEntityType<?>> registryEvent) {
        registryEvent.getRegistry().register(TileEntityType.Builder.create(() -> new SoilStateTileEntity(FARM_SOIL), LibBlocks.LOAM_SOIL, LibBlocks.SILT_SOIL, LibBlocks.SAND_SOIL, LibBlocks.CLAY_SOIL).build(null).setRegistryName("farm_soil"));
    }

    public static final ItemGroup ITEM_GROUP = new ItemGroup(-1, EnvironmentLib.MOD_ID) {
        @OnlyIn(Dist.CLIENT)
        public ItemStack createIcon() {
            return new ItemStack(LibBlocks.LOAM_SOIL);
        }
    };

    @SubscribeEvent
    public static void onItemsRegistry(final RegistryEvent.Register<Item> registryEvent) {
        Item.Properties properties = new Item.Properties().group(ITEM_GROUP);
        registryEvent.getRegistry().registerAll(
                createItemForBlock(LibBlocks.SILT, properties),
                createItemForBlock(LibBlocks.LOAM_SOIL, properties),
                createItemForBlock(LibBlocks.SILT_SOIL, properties),
                createItemForBlock(LibBlocks.SAND_SOIL, properties),
                createItemForBlock(LibBlocks.CLAY_SOIL, properties),
                new Item(new Item.Properties().group(ITEM_GROUP)).setRegistryName("compost"),
                new Item(new Item.Properties().group(ITEM_GROUP)).setRegistryName("lime_dust"),
                new Item(new Item.Properties().rarity(Rarity.RARE).group(ITEM_GROUP)).setRegistryName("fertilizer"),
                new Item(new Item.Properties().group(ITEM_GROUP)).setRegistryName("feather_meal"),
                new Item(new Item.Properties().group(ITEM_GROUP)).setRegistryName("seaweed_meal"),
                new Item(new Item.Properties().group(ITEM_GROUP)).setRegistryName("wood_ash"),
                new SoilTestKitItem(new Item.Properties().maxStackSize(1).rarity(Rarity.UNCOMMON).group(ITEM_GROUP)).setRegistryName("soil_test_kit")
        );
    }

    private static Item createItemForBlock(Block block, Item.Properties properties) {
        //noinspection ConstantConditions
        return new BlockItem(block, properties).setRegistryName(block.getRegistryName());
    }
}
