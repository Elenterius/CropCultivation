package com.creativechasm.cropcultivation.block;

import com.creativechasm.cropcultivation.trigger.ModTriggers;
import mcp.MethodsReturnNonnullByDefault;
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
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class WeedBlock extends BushBlock
{
    public static final EnumProperty<WeedType> WEED_TYPE = EnumProperty.create("plant", WeedType.class);
    public static final VoxelShape BUSH_SHAPE = Block.makeCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 13.0D, 14.0D);
    public static final VoxelShape FLOWER_SHAPE = Block.makeCuboidShape(5.0D, 0.0D, 5.0D, 11.0D, 10.0D, 11.0D);

    public WeedBlock(Properties properties) {
        super(properties);
        setDefaultState(stateContainer.getBaseState().with(WEED_TYPE, WeedType.GRASS));
    }

    @Override
    protected boolean isValidGround(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return state.getBlock() != Blocks.GLASS; //weeds can grow on anything
    }

    @Override
    public void harvestBlock(World worldIn, PlayerEntity player, BlockPos pos, BlockState state, @Nullable TileEntity te, ItemStack stack) {
        super.harvestBlock(worldIn, player, pos, state, te, stack);
        if (player instanceof ServerPlayerEntity) ModTriggers.WEED_DESTROYED.trigger((ServerPlayerEntity) player);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        VoxelShape shape = state.get(WEED_TYPE) == WeedType.GRASS ? BUSH_SHAPE : FLOWER_SHAPE;
        Vec3d vec = state.getOffset(worldIn, pos);
        return shape.withOffset(vec.x, vec.y, vec.z);
    }

    @Override
    public Block.OffsetType getOffsetType() {
        return Block.OffsetType.XZ;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(WEED_TYPE);
    }

    public enum WeedType implements IStringSerializable
    {
        GRASS("grass"),
        TALL_GRASS("tall_grass"),
        SOWTHISTLE("sowthistle");

        private final String name;

        WeedType(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}
