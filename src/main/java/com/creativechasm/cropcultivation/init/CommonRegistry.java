package com.creativechasm.cropcultivation.init;

import com.creativechasm.cropcultivation.CropCultivationMod;
import com.creativechasm.cropcultivation.block.*;
import com.creativechasm.cropcultivation.environment.soil.SoilTexture;
import com.creativechasm.cropcultivation.handler.CropYieldModifier;
import com.creativechasm.cropcultivation.item.*;
import com.creativechasm.cropcultivation.registry.CropRegistry;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.logging.log4j.MarkerManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;


@Mod.EventBusSubscriber(modid = CropCultivationMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public abstract class CommonRegistry
{
    @ObjectHolder(CropCultivationMod.MOD_ID + ":farm_soil")
    public static TileEntityType<?> FARM_SOIL;

    private static CropRegistry CROP_REGISTRY = null;
    public static Map<Block, BlockState> HOE_LOOKUP;
    public static Map<Block, RaisedBedBlock> RAISED_BED_LOOKUP;

    public static VoxelShape SHAPE_RAISED_BED = VoxelShapes.combineAndSimplify(VoxelShapes.fullCube(), Block.makeCuboidShape(2.0D, 14.0D, 2.0D, 14.0D, 16.0D, 14.0D), IBooleanFunction.ONLY_FIRST);

    public static final ItemGroup ITEM_GROUP = new ItemGroup(-1, CropCultivationMod.MOD_ID)
    {
        @OnlyIn(Dist.CLIENT)
        public ItemStack createIcon() {
            return new ItemStack(ModBlocks.LOAMY_SOIL);
        }
    };

    public static CropRegistry getCropRegistry() {
        return CROP_REGISTRY;
    }

    public static void init() {
        CROP_REGISTRY = new CropRegistry("/data/cropcultivation/crop_registry/mappings.csv", "/data/cropcultivation/crop_registry/entries.csv");
        ModTriggers.register();
    }

    public static void setupFirst() {
        registerCrops();
    }

    public static void setupLast() {
        registerCompostableItems();
        modifyHoeLookup();
        modifyShovelLookup();
    }

    @SubscribeEvent
    public static void onBlocksRegistry(final RegistryEvent.Register<Block> registryEvent) {
        IForgeRegistry<Block> registry = registryEvent.getRegistry();

        registry.registerAll(
                new Block(Block.Properties.create(Material.EARTH).hardnessAndResistance(0.65F).harvestTool(ToolType.SHOVEL).sound(SoundType.GROUND)).setRegistryName("silt"),
                new Block(Block.Properties.create(Material.EARTH).hardnessAndResistance(0.6F).harvestTool(ToolType.SHOVEL).sound(SoundType.GROUND)).setRegistryName("loam"),
                new Block(Block.Properties.create(Material.SAND).hardnessAndResistance(0.5F).harvestTool(ToolType.SHOVEL).sound(SoundType.GROUND)).setRegistryName("sandy_dirt"),
                new Block(Block.Properties.create(Material.CLAY).hardnessAndResistance(0.8F).harvestTool(ToolType.SHOVEL).harvestLevel(ItemTier.IRON.getHarvestLevel()).sound(SoundType.GROUND)).setRegistryName("clayey_dirt"),
                new DeadCropBlock(Block.Properties.create(Material.TALL_PLANTS, MaterialColor.WOOD).doesNotBlockMovement().hardnessAndResistance(0.15F).sound(SoundType.PLANT)).setRegistryName("dead_crop_withered"),
                new DeadCropBlock(Block.Properties.create(Material.TALL_PLANTS, MaterialColor.WOOD).doesNotBlockMovement().hardnessAndResistance(0.15F).sound(SoundType.PLANT)).setRegistryName("dead_crop_rotten"),
                new WeedBlock(Block.Properties.create(Material.TALL_PLANTS, MaterialColor.WOOD).doesNotBlockMovement().hardnessAndResistance(0.15F).sound(SoundType.PLANT)).setRegistryName("weed")
        );

        createSoilBlock(registry, Block.Properties.create(Material.EARTH).hardnessAndResistance(0.65F).harvestTool(ToolType.SHOVEL).sound(SoundType.GROUND), SoilTexture.SILT, "silt_soil");
        createSoilBlock(registry, Block.Properties.create(Material.EARTH).hardnessAndResistance(0.6F).harvestTool(ToolType.SHOVEL).sound(SoundType.GROUND), SoilTexture.LOAM, "loam_soil");
        createSoilBlock(registry, Block.Properties.create(Material.SAND).hardnessAndResistance(0.5F).harvestTool(ToolType.SHOVEL).sound(SoundType.GROUND), SoilTexture.SAND, "sand_soil");
        createSoilBlock(registry, Block.Properties.create(Material.CLAY).hardnessAndResistance(0.8F).harvestTool(ToolType.SHOVEL).harvestLevel(ItemTier.IRON.getHarvestLevel()).sound(SoundType.GROUND), SoilTexture.CLAY, "clay_soil");
    }

    private static void createSoilBlock(IForgeRegistry<Block> registry, Block.Properties properties, SoilTexture texture, String registryName) {
        SoilBlock soilBlock = new SoilBlock(properties, texture)
        {
            @Override
            @Nullable
            public TileEntity createTileEntity(BlockState state, IBlockReader world) {
                return FARM_SOIL.create();
            }
        };
        soilBlock.setRegistryName(registryName);
        registry.register(soilBlock);

        RaisedBedBlock raisedBed = new RaisedBedBlock(Block.Properties.create(Material.WOOD).hardnessAndResistance(0.6F).harvestTool(ToolType.AXE).sound(SoundType.WOOD), texture)
        {
            @Override
            @ParametersAreNonnullByDefault
            @Nonnull
            public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
                return SHAPE_RAISED_BED;
            }

            @Override
            @Nullable
            public TileEntity createTileEntity(BlockState state, IBlockReader world) {
                return FARM_SOIL.create();
            }
        };
        raisedBed.setRegistryName(registryName + "_raised_bed");
        registry.register(raisedBed);
    }

    @SubscribeEvent
    public static void onTileEntityTypeRegistry(final RegistryEvent.Register<TileEntityType<?>> registryEvent) {
        //noinspection ConstantConditions
        registryEvent.getRegistry().register(TileEntityType.Builder.create(() -> new SoilStateTileEntity(FARM_SOIL), ModBlocks.LOAMY_SOIL, ModBlocks.SILTY_SOIL, ModBlocks.SANDY_SOIL, ModBlocks.CLAYEY_SOIL).build(null).setRegistryName("farm_soil"));
    }

    @SubscribeEvent
    public static void onItemsRegistry(final RegistryEvent.Register<Item> registryEvent) {
        RAISED_BED_LOOKUP = ImmutableMap.of(
                ModBlocks.SILT, ModBlocks.SILTY_SOIL_RAISED_BED,
                ModBlocks.LOAM, ModBlocks.LOAMY_SOIL_RAISED_BED,
                ModBlocks.SANDY_DIRT, ModBlocks.SANDY_SOIL_RAISED_BED,
                ModBlocks.CLAYEY_DIRT, ModBlocks.CLAYEY_SOIL_RAISED_BED
        );

        Item.Properties properties = new Item.Properties().group(ITEM_GROUP);
        registryEvent.getRegistry().registerAll(
                createItemForBlock(ModBlocks.SILT, properties),
                createItemForBlock(ModBlocks.SILTY_SOIL, properties),
                createItemForBlock(ModBlocks.LOAM, properties),
                createItemForBlock(ModBlocks.LOAMY_SOIL, properties),
                createItemForBlock(ModBlocks.SANDY_DIRT, properties),
                createItemForBlock(ModBlocks.SANDY_SOIL, properties),
                createItemForBlock(ModBlocks.CLAYEY_DIRT, properties),
                createItemForBlock(ModBlocks.CLAYEY_SOIL, properties),
                createItemForBlock(ModBlocks.SILTY_SOIL_RAISED_BED, properties),
                createItemForBlock(ModBlocks.LOAMY_SOIL_RAISED_BED, properties),
                createItemForBlock(ModBlocks.SANDY_SOIL_RAISED_BED, properties),
                createItemForBlock(ModBlocks.CLAYEY_SOIL_RAISED_BED, properties),

                createItemForBlock(ModBlocks.DEAD_CROP_WITHERED, properties),
                createItemForBlock(ModBlocks.DEAD_CROP_ROTTEN, properties),
                createItemForBlock(ModBlocks.WEED, properties),

                new Item(new Item.Properties().group(ITEM_GROUP)).setRegistryName("compost"),
                new MortarItem(new Item.Properties().maxStackSize(1).rarity(Rarity.RARE).group(ITEM_GROUP)).setRegistryName("mortar_pestle"), //mortar and pestle
                new Item(new Item.Properties().group(ITEM_GROUP)).setRegistryName("lime_dust"), //liming material
                new Item(new Item.Properties().rarity(Rarity.UNCOMMON).group(ITEM_GROUP)).setRegistryName("fertilizer"), //NPK fertilizer
                new Item(new Item.Properties().group(ITEM_GROUP)).setRegistryName("feather_meal"), //N fertilizer
                new Item(new Item.Properties().group(ITEM_GROUP)).setRegistryName("seaweed_meal"), //K fertilizer
                new Item(new Item.Properties().group(ITEM_GROUP)).setRegistryName("wood_ash"), //K fertilizer, liming material
                new Item(new Item.Properties().group(ITEM_GROUP)).setRegistryName("fish_meal"), //NP fertilizer
                new Item(new Item.Properties().group(ITEM_GROUP)).setRegistryName("granite_dust"), //P fertilizer

                new SoilTestKitItem(new Item.Properties().maxStackSize(1).rarity(Rarity.RARE).group(ITEM_GROUP)).setRegistryName("soil_test_kit"),
                new ThermoHygrometerItem(new Item.Properties().maxStackSize(1).rarity(Rarity.RARE).group(ITEM_GROUP)).setRegistryName("thermo_hygrometer"),
                new SoilSamplerItem(new Item.Properties().maxStackSize(1).rarity(Rarity.RARE).group(ITEM_GROUP)).setRegistryName("soil_sampler"),
                new SoilMeterItem(new Item.Properties().maxStackSize(1).rarity(Rarity.RARE).group(ITEM_GROUP)).setRegistryName("soil_meter"),
                new CropReaderItem(new Item.Properties().maxStackSize(1).rarity(Rarity.EPIC).group(ITEM_GROUP)).setRegistryName("crop_reader"),

                new TabletItem(new Item.Properties().maxStackSize(1).rarity(Rarity.EPIC).group(ITEM_GROUP)).setRegistryName("tablet"),

                new FoobarItem(new Item.Properties().maxStackSize(1).rarity(Rarity.EPIC).group(ITEM_GROUP)).setRegistryName("foobar")
        );
    }

    private static Item createItemForBlock(Block block, Item.Properties properties) {
        //noinspection ConstantConditions
        return new BlockItem(block, properties).setRegistryName(block.getRegistryName());
    }

    private static void registerCompostableItems() {
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
        ComposterBlock.CHANCES.put(ModItems.WOOD_ASH.getItem(), 0.3f);
    }

    private static void modifyShovelLookup() {
        CropCultivationMod.LOGGER.info(MarkerManager.getMarker("CommonRegistry"), "modifying Shovel Lookup Table...");
        Map<Block, BlockState> SHOVEL_LOOKUP = ObfuscationReflectionHelper.getPrivateValue(ShovelItem.class, (ShovelItem) Items.DIAMOND_SHOVEL, "field_195955_e");
        if (SHOVEL_LOOKUP != null) {
            SHOVEL_LOOKUP.put(ModBlocks.SILTY_SOIL, ModBlocks.SILT.getDefaultState());
            SHOVEL_LOOKUP.put(ModBlocks.LOAMY_SOIL, ModBlocks.LOAM.getDefaultState());
            SHOVEL_LOOKUP.put(ModBlocks.SANDY_SOIL, ModBlocks.SANDY_DIRT.getDefaultState());
            SHOVEL_LOOKUP.put(ModBlocks.CLAYEY_SOIL, ModBlocks.CLAYEY_DIRT.getDefaultState());
        }
        else {
            throw new RuntimeException("failed to modify shovel lookup table");
        }
    }

    private static void modifyHoeLookup() {
        CropCultivationMod.LOGGER.info(MarkerManager.getMarker("CommonRegistry"), "modifying Hoe Lookup Table...");
        Map<Block, BlockState> HOE_LOOKUP = ObfuscationReflectionHelper.getPrivateValue(HoeItem.class, (HoeItem) Items.DIAMOND_HOE, "field_195973_b");
        if (HOE_LOOKUP != null) {
            CommonRegistry.HOE_LOOKUP = HOE_LOOKUP;

            HOE_LOOKUP.remove(Blocks.DIRT);
            HOE_LOOKUP.replaceAll((block, state) -> {
                if (state.getBlock() == Blocks.FARMLAND) { //replace all farmland with dirt
                    return Blocks.DIRT.getDefaultState();
                }
                return state;
            });
            HOE_LOOKUP.put(ModBlocks.SILT, ModBlocks.SILTY_SOIL.getDefaultState());
            HOE_LOOKUP.put(ModBlocks.LOAM, ModBlocks.LOAMY_SOIL.getDefaultState());
            HOE_LOOKUP.put(ModBlocks.SANDY_DIRT, ModBlocks.SANDY_SOIL.getDefaultState());
            HOE_LOOKUP.put(ModBlocks.CLAYEY_DIRT, ModBlocks.CLAYEY_SOIL.getDefaultState());
        }
        else {
            throw new RuntimeException("failed to modify hoe lookup table");
        }
    }

    private static void registerCrops() {
        try {
            CROP_REGISTRY.buildRegistry();
        }
        catch (Exception e) {
            CropCultivationMod.LOGGER.error(CropRegistry.LOG_MARKER, "failed to populate registry", e);
            throw new RuntimeException();
        }
    }

    @SubscribeEvent
    public static void registerModifierSerializers(final RegistryEvent.Register<GlobalLootModifierSerializer<?>> event) {
        event.getRegistry().register(new CropYieldModifier.Serializer().setRegistryName(new ResourceLocation(CropCultivationMod.MOD_ID, "crop_yield")));
    }
}
