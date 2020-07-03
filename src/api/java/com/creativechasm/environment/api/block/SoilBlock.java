package com.creativechasm.environment.api.block;

import com.creativechasm.environment.util.AgricultureUtil;
import com.creativechasm.environment.util.ClimateUtil;
import com.creativechasm.environment.util.MoistureType;
import com.creativechasm.environment.util.SoilTexture;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.Random;

public abstract class SoilBlock extends FarmlandBlock {

    public static final IntegerProperty MOISTURE = IntegerProperty.create("moisture", 0, 10);
    public static final IntegerProperty ORGANIC_MATTER = IntegerProperty.create("organic_matter", 0, 4);

    public final SoilTexture soilTexture;

    public SoilBlock(Properties properties, SoilTexture soilTexture) {
        super(properties.tickRandomly()); //farmland adds property MOISTURE_0_7 to stateContainer, we don't want that!

        // build new stateContainer without MOISTURE_0_7
        StateContainer.Builder<Block, BlockState> builder = new StateContainer.Builder<>(this);
        builder.add(MOISTURE, ORGANIC_MATTER); // normally we would call fillStateContainer(builder)
        StateContainer<Block, BlockState> container = builder.create(BlockState::new); //replace stateContainer

        try {
            Field field = ObfuscationReflectionHelper.findField(Block.class, "field_176227_L");// stateContainer
            field.setAccessible(true);
            field.set(this, container);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to modify field_176227_L");
        }

        setDefaultState(stateContainer.getBaseState().with(MOISTURE, MoistureType.AVERAGE_0.getMoistureLevel()));
        this.soilTexture = soilTexture;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public abstract TileEntity createTileEntity(BlockState state, IBlockReader world);

    public int getMaxMoistureLevel() {
        return 10;
    }

    public int getMaxMoistureCapacity(int organicMatter) {
        int capacity = MathHelper.floor(getMaxMoistureLevel() * (soilTexture.getWaterHoldingCapacity() + organicMatter * SoilTexture.ORGANIC_MATTER_MODIFIER + soilTexture.getWaterHoldingCapacity()));
        return MathHelper.clamp(capacity, 0, getMaxMoistureLevel() - 1);
    }

    @Override
    public void tick(@Nonnull BlockState state, @Nonnull ServerWorld worldIn, @Nonnull BlockPos pos, @Nonnull Random rand) {

        if (!state.isValidPosition(worldIn, pos)) {
            // condition: there is an solid block above this block
            // vanilla farmland: turn to dirt
            // return --> terminate method
        }

        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (!(tileEntity instanceof SoilStateTileEntity)) return;
        SoilStateTileEntity tileState = (SoilStateTileEntity) tileEntity;

        int moisture = state.get(MOISTURE);
        int nitrogen = tileState.getNitrogen();
        int phosphorous = tileState.getPhosphorus();
        int potassium = tileState.getPotassium();
        int organicMatter = state.get(ORGANIC_MATTER);

        int maxMoistureCapacity = getMaxMoistureCapacity(organicMatter);
        boolean hasCrops = hasCrops(worldIn, pos);
        Biome biome = worldIn.getBiome(pos);
        boolean isRaining = worldIn.isRainingAt(pos.up());

        // decrease moisture
        float directMoistureLoss = 0f;
        if (!AgricultureUtil.doesSoilHaveWater(worldIn, pos, soilTexture.getMaxWaterDistance()) && !isRaining) {
            directMoistureLoss = 0.5f;
        }
        // increase moisture (infiltration)
        else if (moisture < getMaxMoistureLevel()) {
            moisture++;
            if (isRaining && worldIn.getRainStrength(1f) > 0.8) {
                moisture++;
            }
        }

        //evaporation loss in arid biomes
        float evaporationLoss = 0f;
        if (ClimateUtil.doesWaterEvaporate(biome.getDefaultTemperature(), biome.getDownfall())) {
            evaporationLoss += 1f;
            if (worldIn.isDaytime() && worldIn.canBlockSeeSky(pos)) evaporationLoss += 0.25f;
        }

        //drainage loss
        //TODO: rework this!
        float drainageLoss = 0f;
        if (moisture >= maxMoistureCapacity - 1) {
            float waterRetentionModifier = organicMatter * SoilTexture.ORGANIC_MATTER_MODIFIER;
            drainageLoss = soilTexture.getDrainageLoss() - waterRetentionModifier + 0.025f;
            BlockState bottomState = worldIn.getBlockState(pos.down());
            Block bottom = bottomState.getBlock();
            if (bottom == Blocks.CLAY) drainageLoss -= 1f - SoilTexture.CLAY.getDrainageLoss();
            else if (Tags.Blocks.STONE.contains(bottom)) drainageLoss -= 0.5f;
            else if (bottom == Blocks.SPONGE) drainageLoss += 1.35f;  // dry sponge drains water "proactively"
            else if (Tags.Blocks.GRAVEL.contains(bottom)) drainageLoss += 0.68f;
            else if (Tags.Blocks.SAND.contains(bottom)) drainageLoss += 0.35f;
//            System.out.println("drainage: " + drainageLoss);
        }

        //calculate total moisture loss
        moisture -= MathHelper.clamp(Math.round(directMoistureLoss + evaporationLoss + drainageLoss), 0, 3);

        //boost plant growth by consuming water with nutrients
        if (moisture > MoistureType.AVERAGE_0.getMoistureLevel() && nitrogen > 0 && phosphorous > 0 && potassium > 0 && worldIn.rand.nextFloat() < 0.5f) {
            BlockState upState = worldIn.getBlockState(pos.up());
            if (upState.getBlock() instanceof IGrowable) {
                IGrowable growable = (IGrowable) upState.getBlock();
                if (growable.canGrow(worldIn, pos, upState, false)) {
                    if (growable.canUseBonemeal(worldIn, worldIn.rand, pos, upState)) {

                        int[] ages = AgricultureUtil.getCurrentAgeAndMaxAge(upState);
                        int currAge = ages[0], maxAge = ages[1];
                        if (currAge < maxAge * 0.333f) { //root growth phase
                            potassium--;
                        }
                        else if (currAge < maxAge * 0.666f) { //foliage growth phase
                            nitrogen--;
                        }
                        else if (currAge < maxAge) { //flower/fruit growth phase
                            phosphorous--;
                        }
                        else { //fallback, what Growable has no age property? penalize the player for using "illegal" plant
                            nitrogen -= 2;
                            phosphorous -= 2;
                            potassium -= 2;
                        }
                        moisture--;

                        growable.grow(worldIn, worldIn.rand, pos, upState);
                    }
                }
            }
        }

        //decompose organic matter into nutrients
        if (worldIn.rand.nextFloat() < 0.025f) { // 1/40
            if (organicMatter > 0) {
                organicMatter--;
                nitrogen++;
                phosphorous++;
                potassium++;
            }
        }

        if (hasCrops && moisture < MoistureType.MOIST.getMoistureLevel()) {
            float fertilizerBurnProbability = 0;
            if (nitrogen > 8) fertilizerBurnProbability += 1f / 3f;
            if (phosphorous > 8) fertilizerBurnProbability += 1f / 3f;
            if (potassium > 8) fertilizerBurnProbability += 1f / 3f;
            if (worldIn.rand.nextFloat() <= fertilizerBurnProbability) {
                //TODO: apply fertilizer burn to plant
            }
        }

        if (moisture > maxMoistureCapacity) {
//            moisture = maxMoistureCapacity;
//            moisture = MoistureType.STANDING_WATER.getMoistureLevel(); //TODO: handle waterlogged state
        }

        updateState(worldIn, pos, state, tileState, moisture, nitrogen, phosphorous, potassium, organicMatter);
    }

    protected boolean hasCrops(IBlockReader worldIn, BlockPos pos) {
        BlockState state = worldIn.getBlockState(pos.up());
        return state.getBlock() instanceof IPlantable && canSustainPlant(state, worldIn, pos, Direction.UP, (IPlantable) state.getBlock());
    }

    @Override
    public boolean canSustainPlant(@Nonnull BlockState state, @Nonnull IBlockReader world, @Nonnull BlockPos pos, @Nonnull Direction facing, @Nonnull IPlantable plantable) {
        return true;
    }

    @Override
    public boolean isFertile(BlockState state, IBlockReader world, BlockPos pos) {
        //TODO: improve this
        return state.get(MOISTURE) > 0;
    }

    @Override
    public void fillWithRain(@Nonnull World worldIn, @Nonnull BlockPos pos) {
        if (worldIn instanceof ServerWorld && worldIn.rand.nextInt(20) == 0) {
            if (worldIn.getBiome(pos).getTemperature(pos) >= 0.15F) {
                TileEntity tileEntity = worldIn.getTileEntity(pos);
                if (!(tileEntity instanceof SoilStateTileEntity)) return;
                SoilStateTileEntity tileState = (SoilStateTileEntity) tileEntity;
                BlockState state = worldIn.getBlockState(pos);
                int moisture = state.get(MOISTURE);
                moisture++; //increase moisture

                //wash away nutrients
                int organicMatter = state.get(ORGANIC_MATTER);
                if (moisture > getMaxMoistureCapacity(organicMatter) && worldIn.getRainStrength(1f) > 0.75f) {
                    float nutrientsRetention = organicMatter * SoilTexture.ORGANIC_MATTER_MODIFIER; // max = 0.5f
                    if (worldIn.rand.nextFloat() < 0.9f - nutrientsRetention) {
                        int nitrogen = tileState.getNitrogen() - 1;
                        int phosphorous = tileState.getPhosphorus() - 1;
                        int potassium = tileState.getPotassium() - 1;
                        updateState((ServerWorld) worldIn, pos, state, tileState, moisture, nitrogen, phosphorous, potassium, organicMatter);
                        return;
                    }
                }

                worldIn.setBlockState(pos, state.with(MOISTURE, moisture), Constants.BlockFlags.BLOCK_UPDATE);
            }
        }
    }

    @Override
    @Nonnull
    public ActionResultType onBlockActivated(BlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos, PlayerEntity player, @Nonnull Hand handIn, @Nonnull BlockRayTraceResult hit) {
        int organic = state.get(ORGANIC_MATTER);
        ItemStack stack = player.getHeldItem(handIn);
        if (organic < 4 && ComposterBlock.CHANCES.containsKey(stack.getItem())) {
            if (!worldIn.isRemote) {
                float f = ComposterBlock.CHANCES.getFloat(stack.getItem());
                if (f > 0.0F && worldIn.getRandom().nextFloat() < f) {
                    worldIn.setBlockState(pos, state.with(ORGANIC_MATTER, organic + 1), Constants.BlockFlags.BLOCK_UPDATE);
                }
                if (!player.abilities.isCreativeMode) {
                    stack.shrink(1);
                }
            }
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.PASS;
    }

    public static void updateState(ServerWorld worldIn, BlockPos pos, BlockState state, SoilStateTileEntity tileState, int moisture, int nitrogen, int phosphorous, int potassium, int organicMatter) {
        moisture = MathHelper.clamp(moisture, 0, 10);
        organicMatter = MathHelper.clamp(organicMatter, 0, 4);
        tileState.setNitrogen(nitrogen);
        tileState.setPhosphorus(phosphorous);
        tileState.setPotassium(potassium);
        worldIn.setBlockState(pos, state.with(MOISTURE, moisture).with(ORGANIC_MATTER, organicMatter), Constants.BlockFlags.BLOCK_UPDATE);
    }

    @Override
    public BlockState getStateForPlacement(@Nonnull BlockItemUseContext context) {
        return getDefaultState();
    }

    @Override
    public void onFallenUpon(@Nonnull World worldIn, @Nonnull BlockPos pos, Entity entityIn, float fallDistance) {
        //removed farmland trampling!
        entityIn.onLivingFall(fallDistance, 1.0F);
    }

}
