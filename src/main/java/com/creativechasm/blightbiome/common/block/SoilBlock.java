package com.creativechasm.blightbiome.common.block;

import com.creativechasm.blightbiome.common.util.MoistureType;
import com.creativechasm.blightbiome.common.util.NatureUtil;
import com.creativechasm.blightbiome.common.util.SoilTexture;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import java.util.Random;

public class SoilBlock extends FarmlandBlock {

    public static final IntegerProperty MOISTURE = IntegerProperty.create("moisture", 0, 10);
    public static final IntegerProperty NITROGEN = IntegerProperty.create("nitrogen", 0, 10);
    public static final IntegerProperty PHOSPHORUS = IntegerProperty.create("phosphorus", 0, 10);
    public static final IntegerProperty POTASSIUM = IntegerProperty.create("potassium", 0, 10);
    public static final IntegerProperty NUTRIENTS = IntegerProperty.create("nutrients", 0, 63); // won't work, need tile entity!
    public static final IntegerProperty ORGANIC_MATTER = IntegerProperty.create("organic_matter", 0, 4);

    protected final StateContainer<Block, BlockState> stateContainer; //"hides" StateContainer in Block class
    public final SoilTexture soilTexture;

    public SoilBlock(Properties properties, SoilTexture soilTexture) {
        super(properties.tickRandomly()); //farmland adds property MOISTURE_0_7 to stateContainer, we don't want that!

        // build new stateContainer without MOISTURE_0_7
        // hacky solution because the old stateContainer with 8 blockStates for MOISTURE_0_7 still remains :(
        StateContainer.Builder<Block, BlockState> builder = new StateContainer.Builder<>(this);
//        builder.add(MOISTURE, NITROGEN, PHOSPHORUS, POTASSIUM, ORGANIC_MATTER); // normally we would call fillStateContainer(builder)
        builder.add(MOISTURE, NUTRIENTS, ORGANIC_MATTER);
        stateContainer = builder.create(BlockState::new);

        setDefaultState(stateContainer.getBaseState().with(MOISTURE, MoistureType.AVERAGE_0.getMoistureLevel()));
        this.soilTexture = soilTexture;
    }

    @Override
    @Nonnull
    public StateContainer<Block, BlockState> getStateContainer() {
        return stateContainer; //return our stateContainer :D
    }

    public int getMaxMoistureLevel() {
        return 10;
    }

    public int getMaxMoistureCapacity(int organicMatter) {
        int capacity = MathHelper.floor(getMaxMoistureLevel() * (soilTexture.getWaterHoldingCapacity() + organicMatter * SoilTexture.ORGANIC_MATTER_MODIFIER));
        return MathHelper.clamp(capacity, 0, getMaxMoistureLevel() - 1);
    }

    @Override
    public void tick(@Nonnull BlockState state, @Nonnull ServerWorld worldIn, @Nonnull BlockPos pos, @Nonnull Random rand) {

        if (!state.isValidPosition(worldIn, pos)) {
            // condition: there is an solid block above this block
            // vanilla farmland: turn to dirt
            // return --> terminate method
        }

        int moisture = state.get(MOISTURE);
        int nitrogen = state.get(NITROGEN);
        int phosphorous = state.get(PHOSPHORUS);
        int potassium = state.get(POTASSIUM);
        int organicMatter = state.get(ORGANIC_MATTER);

        int maxMoistureCapacity = getMaxMoistureCapacity(organicMatter);
        boolean hasCrops = hasCrops(worldIn, pos);
        Biome biome = worldIn.getBiome(pos);
        boolean isRaining = worldIn.isRainingAt(pos.up());

        // decrease moisture
        if (!NatureUtil.doesSoilHaveWater(worldIn, pos, soilTexture.getMaxWaterDistance()) && !isRaining) {

            //evaporation in arid biomes
            float evaporationLoss = 0f;
            if (NatureUtil.doesWaterEvaporate(biome.getDefaultTemperature(), biome.getDownfall())) {
                evaporationLoss += 1f;
                if (worldIn.isDaytime() && worldIn.canBlockSeeSky(pos)) evaporationLoss += 0.25f;
            }

            //seepage loss
            float waterRetentionModifier = organicMatter * SoilTexture.ORGANIC_MATTER_MODIFIER;
            float seepageLoss = soilTexture.getSeepageLoss() - waterRetentionModifier;
            BlockState bottomState = worldIn.getBlockState(pos.down());
            Block bottom = bottomState.getBlock();
            if (bottom == Blocks.CLAY) seepageLoss -= 1f - SoilTexture.CLAY.getSeepageLoss();
            else if (Tags.Blocks.STONE.contains(bottom)) seepageLoss -= 0.5f;
            else if (bottom instanceof SpongeBlock) seepageLoss += 1f;  // dry sponge drains water proactive
            else if (Tags.Blocks.GRAVEL.contains(bottom)) seepageLoss += 0.35f;
            else if (Tags.Blocks.SAND.contains(bottom)) seepageLoss += 0.25f;
            seepageLoss = MathHelper.clamp(seepageLoss, 0f, 2f);

            moisture -= MathHelper.clamp(MathHelper.floor(evaporationLoss + seepageLoss), 0, 2);
        }
        // increase moisture
        else if (moisture < getMaxMoistureLevel()) {
            moisture++;
            if (isRaining && worldIn.getRainStrength(1f) > 0.8) {
                moisture++;
            }
        }

        //boost plant growth by consuming water with nutrients
        if (moisture > MoistureType.AVERAGE_0.getMoistureLevel() && worldIn.rand.nextFloat() < 0.7f) {
            BlockState upState = worldIn.getBlockState(pos.up());
            if (upState.getBlock() instanceof IGrowable) {
                IGrowable growable = (IGrowable) upState.getBlock();
                if (growable.canGrow(worldIn, pos, upState, false)) {
                    if (growable.canUseBonemeal(worldIn, worldIn.rand, pos, upState)) {
                        growable.grow(worldIn, worldIn.rand, pos, upState);
                        moisture--;
                        nitrogen--;
                        phosphorous--;
                        potassium--;
                    }
                }
            }
        }

        //decompose organic matter into nutrients
        if (worldIn.rand.nextFloat() < 0.05f) { // 1/20
            if (organicMatter > 0) {
                organicMatter--;
                nitrogen++;
                phosphorous++;
                potassium++;
            }
        }

        float fertilizerBurnProbability = 0;
        if (phosphorous > 8) fertilizerBurnProbability += 1f / 3f;
        if (potassium > 8) fertilizerBurnProbability += 1f / 3f;
        if (hasCrops && moisture < MoistureType.MOIST.getMoistureLevel() && worldIn.rand.nextFloat() <= fertilizerBurnProbability) {
            //TODO: apply fertilizer burn to plant
        }

        if (moisture > maxMoistureCapacity) {
            moisture = MoistureType.STANDING_WATER.getMoistureLevel();
        }

        updateState(worldIn, pos, state, moisture, nitrogen, phosphorous, potassium, organicMatter);
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
                BlockState state = worldIn.getBlockState(pos);
                int moisture = state.get(MOISTURE);
                moisture++; //increase moisture

                //wash away nutrients
                int organicMatter = state.get(ORGANIC_MATTER);
                if (moisture > getMaxMoistureCapacity(organicMatter) && worldIn.getRainStrength(1f) > 0.75f) {
                    float nutrientsRetention = organicMatter * SoilTexture.ORGANIC_MATTER_MODIFIER; // max = 0.5f
                    if (worldIn.rand.nextFloat() < 0.9f - nutrientsRetention) {
                        int nitrogen = state.get(NITROGEN) - 1;
                        int phosphorous = state.get(PHOSPHORUS) - 1;
                        int potassium = state.get(POTASSIUM) - 1;
                        updateState((ServerWorld) worldIn, pos, state, moisture, nitrogen, phosphorous, potassium, organicMatter);
                        return;
                    }
                }

                worldIn.setBlockState(pos, state.with(MOISTURE, moisture), Constants.BlockFlags.BLOCK_UPDATE);
            }
        }
    }

    public static void updateState(ServerWorld worldIn, BlockPos pos, BlockState state, int moisture, int nitrogen, int phosphorous, int potassium, int organicMatter) {
        moisture = MathHelper.clamp(moisture, 0, 10);
        nitrogen = MathHelper.clamp(nitrogen, 0, 10);
        phosphorous = MathHelper.clamp(phosphorous, 0, 10);
        potassium = MathHelper.clamp(potassium, 0, 10);
        organicMatter = MathHelper.clamp(organicMatter, 0, 4);
        worldIn.setBlockState(pos, state
                .with(MOISTURE, moisture).with(ORGANIC_MATTER, organicMatter)
                .with(NITROGEN, nitrogen).with(PHOSPHORUS, phosphorous).with(POTASSIUM, potassium), Constants.BlockFlags.BLOCK_UPDATE);
    }

    @Override
    public BlockState getStateForPlacement(@Nonnull BlockItemUseContext context) {
        return getDefaultState();
    }

    @Override
    public void onFallenUpon(@Nonnull World worldIn, @Nonnull BlockPos pos, Entity entityIn, float fallDistance) {
        //farmland trampling removed!
        entityIn.onLivingFall(fallDistance, 1.0F);
    }

//    @Override
//    protected void fillStateContainer(@Nonnull StateContainer.Builder<Block, BlockState> builder) {
//        builder.add(MOISTURE, NITROGEN, PHOSPHORUS, POTASSIUM, ORGANIC_MATTER);
//    }
}
