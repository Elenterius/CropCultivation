package com.creativechasm.environment.api.soil;

import com.creativechasm.environment.api.block.SoilBlock;
import com.creativechasm.environment.api.block.SoilStateTileEntity;
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

    private BlockPos pos = null;
    private SoilStateTileEntity tileState = null;
    private BlockState blockState = null;
    public final boolean isClient;

    public SoilStateContext(World world, BlockPos pos) {
        this(world, pos, world.getBlockState(pos));
    }

    public SoilStateContext(World world, BlockPos pos, BlockState blockState) {
        this.pos = pos;
        this.blockState = blockState;
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
        }
        isClient = tileState == null;
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
