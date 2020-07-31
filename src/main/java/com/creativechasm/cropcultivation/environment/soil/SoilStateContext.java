package com.creativechasm.cropcultivation.environment.soil;

import com.creativechasm.cropcultivation.block.SoilBlock;
import com.creativechasm.cropcultivation.block.SoilStateTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;

public class SoilStateContext {

    public int moisture;
    public int organicMatter;
    public float pH;
    public int nitrogen;
    public int phosphorus;
    public int potassium;

    private World world;
    private BlockPos pos;
    private SoilStateTileEntity tileState = null;
    private BlockState blockState;
    public final boolean isClient;
    public final boolean isValid;

    public SoilStateContext(World world, BlockPos pos) {
        this(world, pos, world.getBlockState(pos));
    }

    public SoilStateContext(World world, BlockPos pos, BlockState blockState) {
        this.world = world;
        this.pos = pos;
        this.blockState = blockState;
        boolean isValid_ = blockState.getBlock() instanceof SoilBlock;
        if (isValid_) {
            moisture = blockState.get(SoilBlock.MOISTURE);
            organicMatter = blockState.get(SoilBlock.ORGANIC_MATTER);
            if (!world.isRemote) {
                TileEntity tileEntity = world.getTileEntity(pos);
                if (tileEntity instanceof SoilStateTileEntity) {
                    tileState = (SoilStateTileEntity) tileEntity;
                    pH = tileState.getPH();
                    nitrogen = tileState.getNitrogen();
                    phosphorus = tileState.getPhosphorus();
                    potassium = tileState.getPotassium();
                }
                else {
                    isValid_ = false;
                }
            }
        }
        isValid = isValid_;
        isClient = world.isRemote;
    }

    public SoilStateTileEntity getTileState() {
        return tileState;
    }

    public BlockState getBlockState() {
        return blockState;
    }

    public BlockPos getBlockPos() {
        return pos;
    }

    public World getWorld() {
        return world;
    }

    public int getMaxNutrientAmount() {
        return tileState.getMaxNutrientAmount();
    }

    public int getMaxMoistureLevel() {
        return SoilMoisture.MAX_VALUE;
    }

    public int getMaxOrganicMatterLevel() {
        return 4;
    }

    public void update(ServerWorld world) {
        moisture = MathHelper.clamp(moisture, 0, SoilMoisture.MAX_VALUE);
        organicMatter = MathHelper.clamp(organicMatter, 0, 4);
        tileState.setPH(pH);
        tileState.setNitrogen(nitrogen);
        tileState.setPhosphorus(phosphorus);
        tileState.setPotassium(potassium);
        world.setBlockState(pos, blockState.with(SoilBlock.MOISTURE, moisture).with(SoilBlock.ORGANIC_MATTER, organicMatter), Constants.BlockFlags.BLOCK_UPDATE);
    }
}
