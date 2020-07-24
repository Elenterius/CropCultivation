package com.creativechasm.environment.init;

import com.creativechasm.environment.EnvironmentLib;
import com.creativechasm.environment.api.block.BlockPropertyUtil;
import com.creativechasm.environment.api.block.LibBlocks;
import com.creativechasm.environment.api.block.SoilBlock;
import com.creativechasm.environment.api.block.SoilStateTileEntity;
import com.creativechasm.environment.api.item.LibItems;
import com.creativechasm.environment.api.soil.SoilTexture;
import com.creativechasm.environment.api.util.MiscUtil;
import com.creativechasm.environment.common.item.MortarItem;
import com.creativechasm.environment.common.item.SoilSamplerItem;
import com.creativechasm.environment.common.item.SoilTestKitItem;
import com.creativechasm.environment.common.item.ThermoHygrometerItem;
import com.creativechasm.environment.registry.CropRegistry;
import com.google.gson.JsonObject;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.item.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.logging.log4j.MarkerManager;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


@Mod.EventBusSubscriber(modid = EnvironmentLib.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public abstract class CommonRegistry
{
    public static final CropRegistry CROP_REGISTRY = new CropRegistry("/data/envirlib/crop_registry/mappings.csv", "/data/envirlib/crop_registry/entries.csv");

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

    public static final ItemGroup ITEM_GROUP = new ItemGroup(-1, EnvironmentLib.MOD_ID)
    {
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
                new MortarItem(new Item.Properties().maxStackSize(1).rarity(Rarity.RARE).group(ITEM_GROUP)).setRegistryName("mortar_pestle"), //mortar and pestle
                new Item(new Item.Properties().group(ITEM_GROUP)).setRegistryName("lime_dust"), //liming material
                new Item(new Item.Properties().rarity(Rarity.UNCOMMON).group(ITEM_GROUP)).setRegistryName("fertilizer"), //NPK fertilizer
                new Item(new Item.Properties().group(ITEM_GROUP)).setRegistryName("feather_meal"), //N fertilizer
                new Item(new Item.Properties().group(ITEM_GROUP)).setRegistryName("seaweed_meal"), //K fertilizer
                new Item(new Item.Properties().group(ITEM_GROUP)).setRegistryName("wood_ash"), //K fertilizer, liming material
                new Item(new Item.Properties().group(ITEM_GROUP)).setRegistryName("fish_meal"), //NP fertilizer
                new SoilTestKitItem(new Item.Properties().maxStackSize(1).rarity(Rarity.RARE).group(ITEM_GROUP)).setRegistryName("soil_test_kit"),
                new ThermoHygrometerItem(new Item.Properties().maxStackSize(1).rarity(Rarity.RARE).group(ITEM_GROUP)).setRegistryName("thermo_hygrometer"),
                new SoilSamplerItem(new Item.Properties().maxStackSize(1).rarity(Rarity.RARE).group(ITEM_GROUP)).setRegistryName("soil_sampler")
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

    public static void registerCrops() {
        try {
            CROP_REGISTRY.buildRegistry();
        }
        catch (Exception e) {
            EnvironmentLib.LOGGER.error(CropRegistry.LOG_MARKER, "failed to populate registry", e);
            throw new RuntimeException();
        }
    }

    @SubscribeEvent
    public static void registerModifierSerializers(final RegistryEvent.Register<GlobalLootModifierSerializer<?>> event) {
        event.getRegistry().register(new CropYieldModifier.Serializer().setRegistryName(new ResourceLocation(EnvironmentLib.MOD_ID,"crop_yield")));
    }

    public static class CropYieldModifier extends LootModifier
    {
        public CropYieldModifier(ILootCondition[] conditionsIn) {
            super(conditionsIn);
        }

        @Nonnull
        @Override
        protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {

            EnvironmentLib.LOGGER.debug(MarkerManager.getMarker("CropYieldModifier"), "loot: " + generatedLoot.toString());
            BlockState state = context.get(LootParameters.BLOCK_STATE);
            BlockPos pos = context.get(LootParameters.POSITION);

            ItemStack toolStack = context.get(LootParameters.TOOL);
            Entity entity = context.get(LootParameters.THIS_ENTITY);
//            float luck = context.getLuck(); //applies only to loot from chests and fishing loot, hmm...
            EnvironmentLib.LOGGER.debug(MarkerManager.getMarker("CropYieldModifier"), String.format("tool: %s, fortuneModifier: %s", toolStack, MiscUtil.getFortuneLevel(entity)));

            if (state != null && pos != null) {
                World world = context.getWorld();
                Map<Item, Integer> lootCountMap = new HashMap<>();

                generatedLoot.forEach(stack -> {
                    int count = lootCountMap.computeIfAbsent(stack.getItem(), item -> 0);
                    lootCountMap.put(stack.getItem(), count + stack.getCount());
                });

                TileEntity tileEntity = world.getTileEntity(pos.down());
                if (lootCountMap.size() > 0 && tileEntity instanceof SoilStateTileEntity) {
                    SoilStateTileEntity soil = (SoilStateTileEntity) tileEntity;
                    float yieldMultiplier = soil.getCropYieldAveraged(BlockPropertyUtil.getAge(state)); //get crop yield averaged by crop age

                    lootCountMap.forEach((item, count) -> {
                        int yieldAmount = MathHelper.ceil(count * yieldMultiplier);
                        EnvironmentLib.LOGGER.debug(MarkerManager.getMarker("CropYieldModifier"), String.format("item: %s, count: %s, multiplier: %s, yield: %s", item, count, yieldMultiplier, yieldAmount));
                        modifyGeneratedLoot(generatedLoot, item, count, yieldAmount, context.getRandom());
                    });
                }
            }

            EnvironmentLib.LOGGER.debug(MarkerManager.getMarker("CropYieldModifier"), "out: " + generatedLoot.toString());
            return generatedLoot;
        }

        @ParametersAreNonnullByDefault
        private void modifyGeneratedLoot(List<ItemStack> generatedLoot, Item targetItem, int lootAmount, int yieldAmount, Random rand) {
            if (lootAmount != yieldAmount) {
                int n = 0;
                generatedLoot.removeIf(stack -> stack.getItem() == targetItem);

                //get the loot as multiple ItemStacks
                while (n < yieldAmount) {
                    if (yieldAmount - n > 3) {
                        int amount = rand.nextInt(3) + 1; // 1-3
                        generatedLoot.add(new ItemStack(targetItem, amount));
                        n += amount;
                    }
                    else { //remainder
                        int amount = yieldAmount - n;
                        generatedLoot.add(new ItemStack(targetItem, amount));
                        break;
                    }
                }
            }
        }


        private static class LootCondition implements ILootCondition
        {
            @Override
            public boolean test(LootContext context) {
                BlockState state = context.get(LootParameters.BLOCK_STATE);
                BlockPos pos = context.get(LootParameters.POSITION);
                if (state != null && pos != null) {
                    return state.getBlock() instanceof CropsBlock && context.getWorld().getBlockState(pos.down()).getBlock() instanceof SoilBlock;
                }
                return false;
            }
        }

        private static class Serializer extends GlobalLootModifierSerializer<CropYieldModifier>
        {
            @Override
            public CropYieldModifier read(ResourceLocation name, JsonObject object, ILootCondition[] conditionsIn) {
                //we build our own conditions array
                ILootCondition[] conditions = new ILootCondition[]{new LootCondition()}; //TODO: add conditions for other non-standard mod crops
                return new CropYieldModifier(conditions);
            }
        }
    }
}
