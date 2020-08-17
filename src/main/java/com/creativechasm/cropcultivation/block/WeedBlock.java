package com.creativechasm.cropcultivation.block;

import com.creativechasm.cropcultivation.environment.plant.WeedType;
import com.creativechasm.cropcultivation.trigger.ModTriggers;
import com.creativechasm.cropcultivation.util.BlockPropertyUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.BushBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class WeedBlock extends BushBlock
{
    public static final EnumProperty<WeedType> WEED_TYPE = BlockPropertyUtil.WEED_TYPE;

    public WeedBlock(Properties properties) {
        super(properties);
        setDefaultState(stateContainer.getBaseState().with(WEED_TYPE, WeedType.GRASS));
    }

    @Override
    protected boolean isValidGround(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return state.getBlock() != Blocks.GLASS && state.isSolidSide(worldIn, pos, Direction.UP); //weeds can "grow on any solid block" except glass
    }

    @Override
    public void harvestBlock(World worldIn, PlayerEntity player, BlockPos pos, BlockState state, @Nullable TileEntity te, ItemStack stack) {
        super.harvestBlock(worldIn, player, pos, state, te, stack);
        if (player instanceof ServerPlayerEntity) ModTriggers.WEED_DESTROYED.trigger((ServerPlayerEntity) player);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        Vec3d vec = state.getOffset(worldIn, pos);
        return state.get(WEED_TYPE).getShape().withOffset(vec.x, vec.y, vec.z);
    }

    @Override
    public Block.OffsetType getOffsetType() {
        return Block.OffsetType.XZ;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(WEED_TYPE);
    }

}
