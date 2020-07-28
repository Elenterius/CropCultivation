package com.creativechasm.cropcultivation.handler;

import com.creativechasm.cropcultivation.CropCultivationMod;
import com.creativechasm.cropcultivation.api.block.BlockPropertyUtil;
import com.creativechasm.cropcultivation.api.plant.CropUtil;
import com.creativechasm.cropcultivation.api.plant.ICropEntry;
import com.creativechasm.cropcultivation.api.soil.SoilStateContext;
import com.creativechasm.cropcultivation.init.CommonRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.state.IntegerProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Optional;

@Mod.EventBusSubscriber(modid = CropCultivationMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public abstract class CropHandler
{

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onCropGrowth(final BlockEvent.CropGrowEvent.Pre event) {

        Optional<ICropEntry> optionalICrop = CommonRegistry.getCropRegistry().get(event.getState().getBlock().getRegistryName());
        if (optionalICrop.isPresent()) {
            World world = event.getWorld().getWorld();
            BlockPos pos = event.getPos();
            SoilStateContext soilContext = new SoilStateContext(world, pos.down());
            if (soilContext.isValid && !soilContext.isClient) {
                ICropEntry iCrop = optionalICrop.get();
                if (CropUtil.RegisteredCrop.canCropGrow(world, pos, event.getState(), iCrop, soilContext)) {
                    float growthChance = CropUtil.RegisteredCrop.getGrowthChance((ServerWorld) world, pos, event.getState(), iCrop, soilContext);
                    if (world.rand.nextFloat() < growthChance) {
                        event.setResult(Event.Result.ALLOW);
                        return;
                    }
                }
                event.setResult(Event.Result.DENY);
            }
        }
        else { //unknown crop, fallback to default behavior
            event.setResult(Event.Result.DEFAULT);
        }
    }

    @SubscribeEvent
    public static void onCropGrowth(final BlockEvent.CropGrowEvent.Post event) {
        World world = event.getWorld().getWorld();
        BlockState newCropState = event.getState();

        // consume moisture & nutrients of the soil

        Optional<ICropEntry> optionalICrop = CommonRegistry.getCropRegistry().get(newCropState.getBlock().getRegistryName());
        SoilStateContext soilContext = new SoilStateContext(world, event.getPos().down());
        if (optionalICrop.isPresent()) {
            if (soilContext.isValid && !soilContext.isClient) {
                CropUtil.RegisteredCrop.updateYield((ServerWorld) world, event.getPos(), event.getOriginalState(), newCropState, optionalICrop.get(), soilContext);
                CropUtil.RegisteredCrop.consumeSoilNutrients((ServerWorld) world, event.getPos(), event.getOriginalState(), newCropState, optionalICrop.get(), soilContext);
                CropUtil.RegisteredCrop.consumeSoilMoisture(event.getPos(), newCropState, soilContext);
                soilContext.update((ServerWorld) world); // update changes to world
            }
        }
        else { //fallback for not registered crops
            if (soilContext.isValid && !soilContext.isClient) {
                Optional<IntegerProperty> ageProperty = BlockPropertyUtil.getAgeProperty(newCropState);
                if (ageProperty.isPresent()) {
                    IntegerProperty age = ageProperty.get();
                    int prevCropAge = event.getOriginalState().get(age);
                    int newCropAge = newCropState.get(age);
                    int maxCropAge = BlockPropertyUtil.getMaxAge(age);
                    CropUtil.GenericCrop.updateYield(prevCropAge, newCropAge, soilContext);
                    CropUtil.GenericCrop.consumeSoilNutrients(world.rand, newCropAge, maxCropAge, soilContext);
                }
                else { //fallback, what crop/plant has no age property??
                    soilContext.getTileState().resetCropYield(); //reset yield to default!
                    //penalize the player for using "illegal" plant
                    if (world.rand.nextFloat() < 0.35f) soilContext.nitrogen -= 2;
                    if (world.rand.nextFloat() < 0.35f) soilContext.phosphorus -= 2;
                    if (world.rand.nextFloat() < 0.35f) soilContext.potassium -= 1;
                }
                CropUtil.GenericCrop.consumeSoilMoisture(event.getPos(), newCropState, soilContext);
                soilContext.update((ServerWorld) world); // update changes to world
            }
        }
    }

    public static void onHarvest(final BlockEvent.HarvestDropsEvent event) {
        //fucking forge waisting my time by not annotating HarvestDropsEvent as Deprecated 🤦
    }
}
