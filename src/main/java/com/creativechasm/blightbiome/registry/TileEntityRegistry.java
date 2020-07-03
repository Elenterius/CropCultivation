package com.creativechasm.blightbiome.registry;

import com.creativechasm.blightbiome.BlightBiomeMod;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(BlightBiomeMod.MOD_ID)
@Mod.EventBusSubscriber(modid = BlightBiomeMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class TileEntityRegistry {

//    @ObjectHolder("farm_soil")
//    public static TileEntityType<?> FARM_SOIL;

    @SubscribeEvent
    public static void onTileEntityTypeRegistry(final RegistryEvent.Register<TileEntityType<?>> registryEvent) {
//        registryEvent.getRegistry().register(TileEntityType.Builder.create(() -> new SoilStateTileEntity(FARM_SOIL), BlockRegistry.LOAM_SOIL).build(null).setRegistryName("farm_soil"));
    }
}
