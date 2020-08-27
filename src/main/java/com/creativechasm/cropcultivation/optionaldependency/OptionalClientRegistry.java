package com.creativechasm.cropcultivation.optionaldependency;

import com.creativechasm.cropcultivation.CropCultivationMod;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = CropCultivationMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class OptionalClientRegistry
{
    @SubscribeEvent
    public static void onItemColorRegistry(final ColorHandlerEvent.Item event) {
        if (OptionalCommonRegistry.isSoybeanAvailable()) {
            event.getItemColors().register((stack, index) -> 0x7E9739, OptionalCommonRegistry.Items.SOYBEAN_MEAL);
        }
    }
}
