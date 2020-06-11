package com.creativechasm.blightbiome.common.registry;

import com.creativechasm.blightbiome.BlightBiomeMod;
import com.creativechasm.blightbiome.common.block.BlightCropBlock;
import com.creativechasm.blightbiome.common.block.BlightShroom;
import com.creativechasm.blightbiome.common.block.BlightWeedBlock;
import com.creativechasm.blightbiome.common.block.BlightsoilBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(BlightBiomeMod.MOD_ID)
@Mod.EventBusSubscriber(modid = BlightBiomeMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class BlockRegistry {

    @ObjectHolder("blightsoil")
    public static BlightsoilBlock BLIGHT_SOIL;

    @ObjectHolder("blightsoil_slab")
    public static SlabBlock BLIGHT_SOIL_SLAB;

    @ObjectHolder("blightmoss")
    public static Block BLIGHT_MOSS;

    @ObjectHolder("blightweeds")
    public static BlightWeedBlock BLIGHT_WEED;

    @ObjectHolder("blight_sprout")
    public static BlightWeedBlock BLIGHT_SPROUT;

    @ObjectHolder("blight_sprout_small")
    public static BlightWeedBlock BLIGHT_SPROUT_SMALL;

    @ObjectHolder("blight_shroom_tall")
    public static BlightShroom BLIGHT_MUSHROOM_TALL;

    @ObjectHolder("blight_maize")
    public static BlightCropBlock BLIGHT_MAIZE;

    @SubscribeEvent
    public static void onBlocksRegistry(final RegistryEvent.Register<Block> registryEvent) {
        registryEvent.getRegistry().registerAll(
                new BlightsoilBlock().setRegistryName("blightsoil"),
                new BlightWeedBlock().setRegistryName("blightweeds"),
                new SlabBlock(Block.Properties.create(Material.EARTH, MaterialColor.DIRT).hardnessAndResistance(0.5F).sound(SoundType.GROUND)).setRegistryName("blightsoil_slab"),
                new Block(Block.Properties.create(Material.EARTH, MaterialColor.DIRT).hardnessAndResistance(0.5F).sound(SoundType.GROUND)).setRegistryName("blightmoss"),
                new BlightShroom().setRegistryName("blight_shroom_tall"),
                new BlightCropBlock().setRegistryName("blight_maize"),
                new BlightWeedBlock().setRegistryName("blight_sprout"),
                new BlightWeedBlock().setRegistryName("blight_sprout_small")
        );
    }

    @SubscribeEvent
    public static void onTileEntityTypeRegistry(final RegistryEvent.Register<TileEntityType<?>> registryEvent) {
//			registryEvent.getRegistry().register(TileEntityType.Builder.create(WormholeTileEntity::new, ModBlocks.WORMHOLE).build(null).setRegistryName("wormhole"));
    }
}
