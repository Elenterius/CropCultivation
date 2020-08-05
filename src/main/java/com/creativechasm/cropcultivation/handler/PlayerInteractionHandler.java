package com.creativechasm.cropcultivation.handler;

import com.creativechasm.cropcultivation.CropCultivationMod;
import com.creativechasm.cropcultivation.block.RaisedBedBlock;
import com.creativechasm.cropcultivation.block.SoilStateTileEntity;
import com.creativechasm.cropcultivation.init.CommonRegistry;
import com.creativechasm.cropcultivation.item.IMeasuringDevice;
import com.creativechasm.cropcultivation.util.BlockPropertyUtil;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
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
        else if (stack.getItem() instanceof BlockItem) {
            Block block = ((BlockItem) stack.getItem()).getBlock();
            BlockState curState = event.getWorld().getBlockState(event.getPos());
            if (curState.getBlock() == Blocks.COMPOSTER && CommonRegistry.RAISED_BED_LOOKUP.containsKey(block)) {
                int compostLevel = curState.get(ComposterBlock.LEVEL);
                if (compostLevel < 4) return;

                RaisedBedBlock targetBlock = CommonRegistry.RAISED_BED_LOOKUP.get(block);
                BlockState newState = targetBlock.getDefaultState();
                BlockPos pos = event.getPos();
                World world = event.getWorld();
                int organicMatter = Math.min(compostLevel / 2, 4);
                world.setBlockState(pos, newState.with(BlockPropertyUtil.ORGANIC_MATTER, organicMatter), Constants.BlockFlags.DEFAULT_AND_RERENDER);
                TileEntity tile = world.getTileEntity(pos);
                if (!world.isRemote && tile instanceof SoilStateTileEntity) {
                    SoilStateTileEntity soilTile = (SoilStateTileEntity) tile;
                    //make soil a bit more acidic due to compost
                    soilTile.setPH(targetBlock.soilTexture.pHType.randomPHAffectedByTemperature(world.rand, world.getBiome(pos).getTemperature(pos)) - organicMatter * 0.1f);
                    //add nutrient bonus from compost
                    soilTile.addNitrogen(world.rand.nextInt(Math.max(1, organicMatter - 1)) + 1);
                    soilTile.addPhosphorus(world.rand.nextInt(Math.max(1, organicMatter - 2)) + 1);
                    soilTile.addPotassium(world.rand.nextInt(Math.max(1, organicMatter - 1)) + 1);
                }
                SoundType soundtype = block.getDefaultState().getSoundType(world, pos, event.getPlayer());
                world.playSound(event.getPlayer(), pos, newState.getSoundType(world, pos, event.getPlayer()).getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
                if (!event.getPlayer().isCreative()) stack.shrink(1);
                event.getPlayer().swingArm(Hand.MAIN_HAND);

                event.setCanceled(true);
            }
        }
    }
}
