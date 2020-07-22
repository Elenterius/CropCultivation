package com.creativechasm.environment.handler;

import com.creativechasm.environment.EnvironmentLib;
import com.creativechasm.environment.api.plant.CropRegistry;
import com.creativechasm.environment.api.plant.ICrop;
import com.creativechasm.environment.api.soil.SoilStateContext;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Optional;

@Mod.EventBusSubscriber(modid = EnvironmentLib.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public abstract class CropHandler {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onCropGrowth(final BlockEvent.CropGrowEvent.Pre event) {

        Optional<ICrop> optionalICrop = CropRegistry.getInstance().get(event.getState().getBlock().getRegistryName());
        if (optionalICrop.isPresent()) {
            World world = event.getWorld().getWorld();
            BlockPos pos = event.getPos();
            SoilStateContext soilContext = new SoilStateContext(world, pos.down());
            if (soilContext.isValid && !soilContext.isClient) {
                ICrop iCrop = optionalICrop.get();
                if (ICrop.canCropGrow(world, pos, event.getState(), iCrop, soilContext)) {
                    float growthChance = ICrop.getGrowthChance((ServerWorld) world, pos, event.getState(), iCrop, soilContext);
                    if (world.rand.nextFloat() < growthChance) {
                        event.setResult(Event.Result.ALLOW);
                        return;
                    }
                }
                event.setResult(Event.Result.DENY);
            }
        }
        else { //unknown crop, fallback to minecraft behavior
            event.setResult(Event.Result.DEFAULT);
        }
    }

    @SubscribeEvent
    public static void onCropGrowth(final BlockEvent.CropGrowEvent.Post event) {
        World world = event.getWorld().getWorld();
        BlockState cropState = event.getState();

        // consume moisture & nutrients of the soil
        Optional<ICrop> optionalICrop = CropRegistry.getInstance().get(cropState.getBlock().getRegistryName());
        if (optionalICrop.isPresent()) {
            SoilStateContext soilContext = new SoilStateContext(world, event.getPos().down());
            if (soilContext.isValid && !soilContext.isClient) {
                ICrop.updateYield((ServerWorld) world, event.getPos(), event.getOriginalState(), cropState, optionalICrop.get(), soilContext);
                ICrop.consumeSoilMoistureAndNutrients((ServerWorld) world, event.getPos(), event.getOriginalState(), cropState, optionalICrop.get(), soilContext);
                soilContext.update((ServerWorld) world); // update changes to world
            }
        }
    }

    public static void onHarvest(final BlockEvent.HarvestDropsEvent event) {
        //fucking forge not annotating HarvestDropsEvent as Deprecated 🤦
    }
}
