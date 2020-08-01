package com.creativechasm.cropcultivation.handler;

import com.creativechasm.cropcultivation.CropCultivationMod;
import com.creativechasm.cropcultivation.environment.CropUtil;
import com.creativechasm.cropcultivation.environment.soil.SoilStateContext;
import com.creativechasm.cropcultivation.init.CommonRegistry;
import com.creativechasm.cropcultivation.registry.ICropEntry;
import com.creativechasm.cropcultivation.util.BlockPropertyUtil;
import com.creativechasm.cropcultivation.util.ModTags;
import net.minecraft.block.BlockState;
import net.minecraft.state.IntegerProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.MarkerManager;

import java.util.Optional;

@Mod.EventBusSubscriber(modid = CropCultivationMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public abstract class CropHandler
{

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPreCropGrowth(final BlockEvent.CropGrowEvent.Pre event) {

        boolean useDefaultGrowth = ModTags.Blocks.USE_DEFAULT_GROWTH.contains(event.getState().getBlock());
        if (useDefaultGrowth) {
            event.setResult(Event.Result.DEFAULT);
            return;
        }

        World world = event.getWorld().getWorld();
        BlockPos pos = event.getPos();
        SoilStateContext soilContext = new SoilStateContext(world, pos.down());
        if (!soilContext.isClient) {
            Optional<ICropEntry> optionalICrop = CommonRegistry.getCropRegistry().get(event.getState().getBlock().getRegistryName());
            ICropEntry iCrop = optionalICrop.orElse(CropUtil.GENERIC_CROP); // if the crop is unknown use a generic fallback

            if (soilContext.isValid && CropUtil.RegisteredCrop.canCropGrow(world, pos, event.getState(), iCrop, soilContext)) { //pre-conditions
                float growthChance = CropUtil.RegisteredCrop.getGrowthChance((ServerWorld) world, pos, event.getState(), iCrop, soilContext);
                if (world.rand.nextFloat() < growthChance) {
                    event.setResult(Event.Result.ALLOW);
                    return;
                }
            }
            event.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public static void onPostCropGrowth(final BlockEvent.CropGrowEvent.Post event) {

        boolean useDefaultGrowth = ModTags.Blocks.USE_DEFAULT_GROWTH.contains(event.getState().getBlock());

        World world = event.getWorld().getWorld();
        BlockState newCropState = event.getState();
        SoilStateContext soilContext = new SoilStateContext(world, event.getPos().down());

        // consume moisture & nutrients of the soil, this applies to all plants. DEFAULT_GROWTH crops are not excluded!
        if (soilContext.isValid && !soilContext.isClient) {
            world.playEvent(Constants.WorldEvents.BONEMEAL_PARTICLES, event.getPos(), 5);

            // update changes to world
            if (!useDefaultGrowth) {
                Optional<ICropEntry> optionalICrop = CommonRegistry.getCropRegistry().get(newCropState.getBlock().getRegistryName());
                ICropEntry iCrop = optionalICrop.orElse(CropUtil.GENERIC_CROP); // if the crop is unknown use a generic fallback

                Optional<IntegerProperty> ageProperty = BlockPropertyUtil.getAgeProperty(newCropState);
                if (ageProperty.isPresent()) {
                    IntegerProperty age = ageProperty.get();
                    int prevCropAge = event.getOriginalState().get(age);
                    int newCropAge = newCropState.get(age);
                    int maxCropAge = BlockPropertyUtil.getMaxAge(age);
                    CropUtil.RegisteredCrop.updateYield(prevCropAge, newCropAge, soilContext);
                    CropUtil.RegisteredCrop.consumeSoilNutrients(world.rand, newCropAge, maxCropAge, iCrop, soilContext);
                }
                else {  //fallback, what crop has no age property?
                    CropCultivationMod.LOGGER.warn(MarkerManager.getMarker("CropHandler"), newCropState.getBlock() + " has no age property!");

                    soilContext.getTileState().resetCropYield(); //reset yield to default!
                    //penalize the player for using "illegal" plant
                    if (CropUtil.RegisteredCrop.canConsumeNutrient(world.rand, iCrop.getNitrogenNeed())) soilContext.nitrogen -= 2;
                    if (CropUtil.RegisteredCrop.canConsumeNutrient(world.rand, iCrop.getPhosphorusNeed())) soilContext.phosphorus -= 2;
                    if (CropUtil.RegisteredCrop.canConsumeNutrient(world.rand, iCrop.getPotassiumNeed())) soilContext.potassium -= 1;
                }
                CropUtil.RegisteredCrop.consumeSoilMoisture(event.getPos(), newCropState, soilContext);
            }
            else { //handle DEFAULT_GROWTH crops
                Optional<IntegerProperty> ageProperty = BlockPropertyUtil.getAgeProperty(newCropState);
                if (ageProperty.isPresent()) {
                    IntegerProperty age = ageProperty.get();
                    int prevCropAge = event.getOriginalState().get(age);
                    int newCropAge = newCropState.get(age);
                    int maxCropAge = BlockPropertyUtil.getMaxAge(age);
                    CropUtil.GenericCrop.updateYield(prevCropAge, newCropAge, soilContext);
                    CropUtil.GenericCrop.consumeSoilNutrients(world.rand, newCropAge, maxCropAge, soilContext);
                }
                else { //fallback, what crop has no age property??
                    CropCultivationMod.LOGGER.warn(MarkerManager.getMarker("CropHandler"), newCropState.getBlock() + " has no age property!");

                    soilContext.getTileState().resetCropYield(); //reset yield to default!
                    //penalize the player for using "illegal" plant
                    if (world.rand.nextFloat() < 0.35f) soilContext.nitrogen -= 2;
                    if (world.rand.nextFloat() < 0.35f) soilContext.phosphorus -= 2;
                    if (world.rand.nextFloat() < 0.35f) soilContext.potassium -= 1;
                }
                CropUtil.GenericCrop.consumeSoilMoisture(event.getPos(), newCropState, soilContext);
            }
            soilContext.update((ServerWorld) world); // update changes to world
        }
    }

    public static void onHarvest(@SuppressWarnings("unused") final BlockEvent.HarvestDropsEvent event) {
        //ffs, forge waisting my time by not annotating HarvestDropsEvent as Deprecated 🤦
    }
}
