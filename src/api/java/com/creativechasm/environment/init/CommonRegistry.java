package com.creativechasm.environment.init;

import com.creativechasm.environment.EnvironmentLib;
import com.creativechasm.environment.api.block.LibBlocks;
import com.creativechasm.environment.api.block.SoilBlock;
import com.creativechasm.environment.api.block.SoilStateTileEntity;
import com.creativechasm.environment.api.item.LibItems;
import com.creativechasm.environment.api.soil.SoilTexture;
import com.creativechasm.environment.common.item.MortarItem;
import com.creativechasm.environment.common.item.SoilTestKitItem;
import com.creativechasm.environment.common.item.ThermoHygrometerItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ComposterBlock;
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
                new MortarItem(new Item.Properties().maxStackSize(1).rarity(Rarity.UNCOMMON).group(ITEM_GROUP)).setRegistryName("mortar_pestle"), //mortar and pestle
                new Item(new Item.Properties().group(ITEM_GROUP)).setRegistryName("lime_dust"), //liming material
                new Item(new Item.Properties().rarity(Rarity.RARE).group(ITEM_GROUP)).setRegistryName("fertilizer"), //NPK fertilizer
                new Item(new Item.Properties().group(ITEM_GROUP)).setRegistryName("feather_meal"), //N fertilizer
                new Item(new Item.Properties().group(ITEM_GROUP)).setRegistryName("seaweed_meal"), //K fertilizer
                new Item(new Item.Properties().group(ITEM_GROUP)).setRegistryName("wood_ash"), //K fertilizer, liming material
                new Item(new Item.Properties().group(ITEM_GROUP)).setRegistryName("fish_meal"), //NP fertilizer
                new SoilTestKitItem(new Item.Properties().maxStackSize(1).rarity(Rarity.UNCOMMON).group(ITEM_GROUP)).setRegistryName("soil_test_kit"),
                new ThermoHygrometerItem(new Item.Properties().maxStackSize(1).rarity(Rarity.UNCOMMON).group(ITEM_GROUP)).setRegistryName("thermo_hygrometer")
        );
    }

    private static Item createItemForBlock(Block block, Item.Properties properties) {
        //noinspection ConstantConditions
        return new BlockItem(block, properties).setRegistryName(block.getRegistryName());
    }

    public static void registerCompostableItems() {
        // add more vanilla items to compost
        ComposterBlock.CHANCES.putIfAbsent(Items.POISONOUS_POTATO.getItem(), 0.7f); //higher chance than potato (0.65) ;)
        ComposterBlock.CHANCES.putIfAbsent(Items.PAPER.getItem(), 0.1f);
        ComposterBlock.CHANCES.putIfAbsent(Items.FEATHER.getItem(), 0.25f);
        ComposterBlock.CHANCES.putIfAbsent(Items.EGG.getItem(), 0.25f);
        ComposterBlock.CHANCES.putIfAbsent(Items.TURTLE_EGG.getItem(), 0.3f); //shouldn't do this but who cares --> you can use chicken eggs to make cake
        ComposterBlock.CHANCES.putIfAbsent(Items.STRING.getItem(), 0.2f);
        //add fish
        ComposterBlock.CHANCES.putIfAbsent(Items.COD.getItem(), 0.65f);
        ComposterBlock.CHANCES.putIfAbsent(Items.COOKED_COD.getItem(), 0.85f);
        ComposterBlock.CHANCES.putIfAbsent(Items.SALMON.getItem(), 0.65f);
        ComposterBlock.CHANCES.putIfAbsent(Items.COOKED_SALMON.getItem(), 0.85f);
        ComposterBlock.CHANCES.putIfAbsent(Items.TROPICAL_FISH.getItem(), 0.65f);
        ComposterBlock.CHANCES.putIfAbsent(Items.PUFFERFISH.getItem(), 0.65f);

        // add our stuff
        ComposterBlock.CHANCES.put(LibItems.WOOD_ASH.getItem(), 0.3f);
    }
}
