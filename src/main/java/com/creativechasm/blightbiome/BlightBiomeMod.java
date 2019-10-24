package com.creativechasm.blightbiome;

import com.creativechasm.blightbiome.client.renderer.entity.BlobInsectRenderer;
import com.creativechasm.blightbiome.client.renderer.entity.BroodmotherRenderer;
import com.creativechasm.blightbiome.common.block.BlightsoilBlock;
import com.creativechasm.blightbiome.common.block.BlightweedBlock;
import com.creativechasm.blightbiome.common.block.ModBlocks;
import com.creativechasm.blightbiome.common.entity.BlobInsectEntity;
import com.creativechasm.blightbiome.common.entity.BroodmotherEntity;
import net.minecraft.block.Block;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.stream.Collectors;

@Mod("blightbiome")
public class BlightBiomeMod
{
	private static final Logger LOGGER = LogManager.getLogger();

	public BlightBiomeMod()
	{
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

		// Register ourselves for server and other game events we are interested in
		MinecraftForge.EVENT_BUS.register(this);
	}

	private void setup(final FMLCommonSetupEvent event) { /* pre-init stuff */ }

	private void doClientStuff(final FMLClientSetupEvent event)
	{
		LOGGER.info("Got game settings {}", event.getMinecraftSupplier().get().gameSettings);

		RenderingRegistry.registerEntityRenderingHandler(BlobInsectEntity.class, BlobInsectRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(BroodmotherEntity.class, BroodmotherRenderer::new);
	}

	private void enqueueIMC(final InterModEnqueueEvent event)
	{
		InterModComms.sendTo("blightbiome", "helloworld", () -> {
			LOGGER.info("Hello world from the MDK");
			return "Hello world";
		});
	}

	private void processIMC(final InterModProcessEvent event)
	{
		LOGGER.info("Got IMC {}", event.getIMCStream().
				map(m -> m.getMessageSupplier().get()).
				collect(Collectors.toList()));
	}

	// You can use SubscribeEvent and let the Event Bus discover methods to call
	@SubscribeEvent
	public void onServerStarting(FMLServerStartingEvent event)
	{
		LOGGER.info("HELLO from server starting");
	}

	// You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
	// Event bus for receiving Registry Events)
	@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
	public static class RegistryEvents
	{
		public static final ItemGroup ITEM_GROUP = new ItemGroup(-1, "blightbiome")
		{
			@OnlyIn(Dist.CLIENT)
			public ItemStack createIcon()
			{
				return new ItemStack(ModBlocks.BLIGHT_SOIL);
			}
		};

		@SubscribeEvent
		public static void onBlocksRegistry(final RegistryEvent.Register<Block> registryEvent)
		{
			registryEvent.getRegistry().registerAll(
					new BlightsoilBlock().setRegistryName("blightsoil"),
					new BlightweedBlock().setRegistryName("blightweeds"),
					new SlabBlock(Block.Properties.create(Material.EARTH, MaterialColor.DIRT).hardnessAndResistance(0.5F).sound(SoundType.GROUND)).setRegistryName("blightsoil_slab")
			);
		}

		@SubscribeEvent
		public static void onTileEntityTypeRegistry(final RegistryEvent.Register<TileEntityType<?>> registryEvent)
		{
//			registryEvent.getRegistry().register(TileEntityType.Builder.create(WormholeTileEntity::new, ModBlocks.WORMHOLE).build(null).setRegistryName("wormhole"));
		}

		@SubscribeEvent
		public static void onItemsRegistry(final RegistryEvent.Register<Item> registryEvent)
		{
			Item.Properties properties = new Item.Properties().group(ITEM_GROUP);

			registryEvent.getRegistry().register(new BlockItem(ModBlocks.BLIGHT_SOIL, properties).setRegistryName("blightsoil"));
			registryEvent.getRegistry().register(new BlockItem(ModBlocks.BLIGHT_WEED, properties).setRegistryName("blightweeds"));
			registryEvent.getRegistry().register(new BlockItem(ModBlocks.BLIGHT_SOIL_SLAB, properties).setRegistryName("blightsoil_slab"));
		}

		@SubscribeEvent
		public static void onEntityTypeRegistry(final RegistryEvent.Register<EntityType<?>> registryEvent)
		{
			registryEvent.getRegistry().register(EntityType.Builder.create(BlobInsectEntity::new, EntityClassification.MONSTER).size(0.4F, 0.35F).build("blob_insect").setRegistryName("blob_insect"));
			registryEvent.getRegistry().register(EntityType.Builder.create(BroodmotherEntity::new, EntityClassification.MONSTER).size(1.6F, 0.7F).build("brood_mother").setRegistryName("brood_mother"));
		}

		@SubscribeEvent
		public static void onModelRegistry(final ModelRegistryEvent registryEvent)
		{
//			ClientRegistry.bindTileEntitySpecialRenderer(WormholeTileEntity.class, new WormholeTileEntityRenderer());
		}
	}
}
