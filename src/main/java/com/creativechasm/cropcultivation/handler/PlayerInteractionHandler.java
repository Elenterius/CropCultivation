package com.creativechasm.cropcultivation.handler;

import com.creativechasm.cropcultivation.CropCultivationMod;
import com.creativechasm.cropcultivation.item.IMeasuringDevice;
import net.minecraft.item.ItemStack;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CropCultivationMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerInteractionHandler
{
    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        ItemStack stack = event.getItemStack();
        if (stack.getItem() instanceof IMeasuringDevice) {
            if (event.getWorld() instanceof ServerWorld) {
                ((IMeasuringDevice)stack.getItem()).gatherData((ServerWorld) event.getWorld(), event.getPlayer(), event.getPos(), stack);
            }
            event.setCanceled(true); //other event subscribers don't know of this :(
            // TODO: create a pull requests for SimpleFarming which changes the right-click harvest handlers to uses item tags to determine if the held item disallows the harvest
        }
    }
}
