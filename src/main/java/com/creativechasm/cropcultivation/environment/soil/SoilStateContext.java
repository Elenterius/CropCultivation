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
    private final SoilTexture soilTexture;

    private final World world;
    private final BlockPos pos;
    private final SoilStateTileEntity tileState;
    private final BlockState blockState;
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
        soilTexture = isValid_ ? ((SoilBlock) blockState.getBlock()).soilTexture : null;
        SoilStateTileEntity tileState_ = null;
        if (isValid_) {
            moisture = blockState.get(SoilBlock.MOISTURE);
            organicMatter = blockState.get(SoilBlock.ORGANIC_MATTER);
            if (!world.isRemote) {
                TileEntity tileEntity = world.getTileEntity(pos);
                if (tileEntity instanceof SoilStateTileEntity) {
                    tileState_ = (SoilStateTileEntity) tileEntity;
                    pH = tileState_.getPH();
                    nitrogen = tileState_.getNitrogen();
                    phosphorus = tileState_.getPhosphorus();
                    potassium = tileState_.getPotassium();
                }
                else {
                    isValid_ = false;
                }
            }
        }
        tileState = tileState_;
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
        organicMatter = MathHelper.clamp(organicMatter, 0, 4);
        int maxMoisture = soilTexture.getMaxMoistureCapacity(organicMatter);
        moisture = MathHelper.clamp(moisture, soilTexture.getMinMoistureCapacity(organicMatter, maxMoisture), maxMoisture);
        tileState.setPH(pH);
        tileState.setNitrogen(nitrogen);
        tileState.setPhosphorus(phosphorus);
        tileState.setPotassium(potassium);
        world.setBlockState(pos, blockState.with(SoilBlock.MOISTURE, moisture).with(SoilBlock.ORGANIC_MATTER, organicMatter), Constants.BlockFlags.BLOCK_UPDATE);
    }

    public void update(ServerWorld world, boolean hasWaterSource) {
        organicMatter = MathHelper.clamp(organicMatter, 0, 4);
        int maxMoisture = soilTexture.getMaxMoistureCapacity(organicMatter);
        moisture = MathHelper.clamp(moisture, soilTexture.getMinMoistureCapacity(organicMatter, maxMoisture), hasWaterSource ? maxMoisture + 1 : maxMoisture);
        tileState.setPH(pH);
        tileState.setNitrogen(nitrogen);
        tileState.setPhosphorus(phosphorus);
        tileState.setPotassium(potassium);
        world.setBlockState(pos, blockState.with(SoilBlock.MOISTURE, moisture).with(SoilBlock.ORGANIC_MATTER, organicMatter), Constants.BlockFlags.BLOCK_UPDATE);
    }
}
