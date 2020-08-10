package com.creativechasm.cropcultivation.handler;

import com.creativechasm.cropcultivation.CropCultivationMod;
import com.creativechasm.cropcultivation.init.CommonRegistry;
import com.creativechasm.cropcultivation.registry.ICropEntry;
import net.minecraft.block.Blocks;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Optional;

@Mod.EventBusSubscriber(modid = CropCultivationMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public abstract class FertilizerHandler
{
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onBonemealUse(final BonemealEvent event) {

        if (event.getBlock().getBlock() == Blocks.NETHER_WART) { //fix to prevent bone meal particle spawning
            event.setCanceled(true);
        }

        if (event.getPlayer().isCreative()) return; //allow creative players to bone meal plants directly

        //disable bone meal for supported crops/plants
        Optional<ICropEntry> optionalICrop = CommonRegistry.getCropRegistry().get(event.getBlock().getBlock().getRegistryName());
        if (optionalICrop.isPresent()) {
            event.setCanceled(true);
        }
    }
}
