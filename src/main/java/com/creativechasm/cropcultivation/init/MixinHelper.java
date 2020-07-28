package com.creativechasm.cropcultivation.init;

import com.creativechasm.cropcultivation.CropCultivationMod;
import com.creativechasm.cropcultivation.api.block.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import org.apache.logging.log4j.MarkerManager;

import java.util.Map;

public abstract class MixinHelper
{
    public static Map<Block, BlockState> HOE_LOOKUP;

    protected static void modifyHoeLookup() {
        CropCultivationMod.LOGGER.info(MarkerManager.getMarker("CommonRegistry"), "modifying Hoe Lookup Map...");
        HOE_LOOKUP.remove(Blocks.DIRT);
        HOE_LOOKUP.replaceAll((block, state) -> {
            if (state.getBlock() == Blocks.FARMLAND) { //replace all farmland with dirt
                return Blocks.DIRT.getDefaultState();
            }
            return state;
        });
        HOE_LOOKUP.put(ModBlocks.SILT, ModBlocks.SILTY_SOIL.getDefaultState());
        HOE_LOOKUP.put(ModBlocks.LOAM, ModBlocks.LOAMY_SOIL.getDefaultState());
        HOE_LOOKUP.put(ModBlocks.SANDY_DIRT, ModBlocks.SANDY_SOIL.getDefaultState());
        HOE_LOOKUP.put(ModBlocks.CLAYEY_DIRT, ModBlocks.CLAYEY_SOIL.getDefaultState());

        //TODO: properly update soil state on creation

        HOE_LOOKUP = null;
    }

    protected static void modifyShovelLookup() {

    }
}
