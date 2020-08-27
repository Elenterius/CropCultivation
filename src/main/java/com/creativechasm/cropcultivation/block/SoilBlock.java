package com.creativechasm.cropcultivation.block;

import com.creativechasm.cropcultivation.CropCultivationConfig;
import com.creativechasm.cropcultivation.CropCultivationMod;
import com.creativechasm.cropcultivation.environment.ClimateUtil;
import com.creativechasm.cropcultivation.environment.CropUtil;
import com.creativechasm.cropcultivation.environment.plant.PlantMacronutrient;
import com.creativechasm.cropcultivation.environment.soil.SoilMoisture;
import com.creativechasm.cropcultivation.environment.soil.SoilPH;
import com.creativechasm.cropcultivation.environment.soil.SoilStateContext;
import com.creativechasm.cropcultivation.environment.soil.SoilTexture;
import com.creativechasm.cropcultivation.init.ModBlocks;
import com.creativechasm.cropcultivation.init.ModTags;
import com.creativechasm.cropcultivation.init.ModTriggers;
import com.creativechasm.cropcultivation.registry.CropRegistry;
import com.creativechasm.cropcultivation.registry.ICropEntry;
import com.creativechasm.cropcultivation.util.BlockPropertyUtil;
import net.minecraft.block.*;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.*;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.FarmlandWaterManager;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import org.apache.logging.log4j.MarkerManager;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public abstract class SoilBlock extends FarmlandBlock {

    public static final IntegerProperty MOISTURE = BlockPropertyUtil.MOISTURE;
    public static final IntegerProperty ORGANIC_MATTER = BlockPropertyUtil.ORGANIC_MATTER;

    public final SoilTexture soilTexture;

    public SoilBlock(Properties properties, SoilTexture soilTexture) {
        super(properties.tickRandomly()); //farmland adds property MOISTURE_0_7 to stateContainer, we don't want that!

        // build new stateContainer with our own moisture property
        StateContainer.Builder<Block, BlockState> builder = new StateContainer.Builder<>(this);
        builder.add(MOISTURE, ORGANIC_MATTER); // normally we would call fillStateContainer(builder)
        StateContainer<Block, BlockState> container = builder.create(BlockState::new);

        try {
            Field field = ObfuscationReflectionHelper.findField(Block.class, "field_176227_L");// stateContainer
            field.setAccessible(true);
            field.set(this, container); //replace stateContainer
        } catch (Exception e) {
            CropCultivationMod.LOGGER.error(MarkerManager.getMarker("SoilBlock"), "Unable to replace stateContainer of SoilBlock", e);
            throw new RuntimeException("Unable to modify field_176227_L");
        }

        setDefaultState(stateContainer.getBaseState().with(MOISTURE, SoilMoisture.AVERAGE_0.getMoistureLevel()));
        this.soilTexture = soilTexture;
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return getDefaultState();
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (!stack.hasTag()) { //prevents resetting of the pH value if a block was copied with ctrl + middle mouse
            TileEntity tile = worldIn.getTileEntity(pos);
            if (tile instanceof SoilStateTileEntity) {
                ((SoilStateTileEntity) tile).setPH(soilTexture.pHType.randomPHAffectedByTemperature(worldIn.rand, worldIn.getBiome(pos).getTemperature(pos)));
            }
        }
    }

    @Override
    public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        //this catches all block state changes caused by the hoe through "tilling"
        if (oldState.getBlock() != this && oldState.getBlock() != Blocks.AIR && CropUtil.canBlockTurnIntoFarmland(oldState.getBlock())) {
            TileEntity tile = worldIn.getTileEntity(pos);
            if (tile instanceof SoilStateTileEntity) {
                ((SoilStateTileEntity) tile).setPH(soilTexture.pHType.randomPHAffectedByTemperature(worldIn.rand, worldIn.getBiome(pos).getTemperature(pos)));
            }
        }
    }

    public int getMaxMoistureLevel() {
        return SoilMoisture.MAX_VALUE;
    }

    protected boolean hasCrops(IBlockReader worldIn, BlockPos pos) {
        BlockState state = worldIn.getBlockState(pos.up());
        return state.getBlock() instanceof IPlantable && canSustainPlant(state, worldIn, pos, Direction.UP, (IPlantable) state.getBlock());
    }

    @Override
    public boolean canSustainPlant(BlockState state, IBlockReader world, BlockPos pos, Direction facing, IPlantable plantable) {
        PlantType plantType = plantable.getPlantType(world, pos);
        return plantType != PlantType.Nether;
    }

    @Override
    public boolean isFertile(BlockState blockState, IBlockReader world, BlockPos pos) {
        if (world instanceof ServerWorld) {
            TileEntity tileEntity = world.getTileEntity(pos);
            if (!(tileEntity instanceof SoilStateTileEntity)) return false;
            SoilStateTileEntity tileState = (SoilStateTileEntity) tileEntity;

            if (tileState.getNitrogen() > 0 && tileState.getPhosphorus() > 0) {
                return blockState.get(MOISTURE) > 0;
            }
        }
        return false;
    }

    @Override
    public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {

        if (!state.isValidPosition(worldIn, pos)) {
            // condition: there is an solid block above this block
            // vanilla farmland: turn to dirt
            // return --> terminate method
        }

        SoilStateContext soilContext = new SoilStateContext(worldIn, pos);
        if (!soilContext.isValid) return;

        int moistureGain = 0, moistureLoss = 0;
        int maxMoistureCapacity = soilTexture.getMaxMoistureCapacity(soilContext.organicMatter);
        boolean hasCrops = hasCrops(worldIn, pos);
        Biome biome = worldIn.getBiome(pos);
        boolean isRaining = worldIn.isRainingAt(pos.up());
        boolean hasWaterSource = doesSoilHaveWater(worldIn, pos, soilTexture.getMaxWaterDistance());

        // decrease moisture
        float directMoistureLoss = 0f;
        if (!hasWaterSource && !isRaining) {
            directMoistureLoss = 0.5f;
        }
        // increase moisture (irrigation/infiltration)
        else if (soilContext.moisture < getMaxMoistureLevel()) {
            moistureGain++;
            if (isRaining && worldIn.getRainStrength(1f) > 0.8) {
                moistureGain++;
            }
            soilContext.moisture += moistureGain;
        }

        //evaporation loss in arid biomes
        float evaporationLoss = 0f;
        if (ClimateUtil.doesWaterEvaporate(biome.getDefaultTemperature(), biome.getDownfall())) {
            evaporationLoss += 1f;
            if (worldIn.isDaytime() && worldIn.canBlockSeeSky(pos)) evaporationLoss += 0.25f;
        }

        //drainage loss
        float drainageLoss = 0f;
        if (soilContext.moisture >= maxMoistureCapacity - 1) {
            BlockState downState = worldIn.getBlockState(pos.down());
            Block subsoil = downState.getBlock();
            float waterRetentionModifier = soilContext.organicMatter * SoilTexture.ORGANIC_MATTER_MULTIPLIER;
            drainageLoss = soilTexture.getDrainageLoss() - waterRetentionModifier;
            if (subsoil == Blocks.CLAY) drainageLoss -= 1f - SoilTexture.CLAY.getDrainageLoss();
            else if (Tags.Blocks.SANDSTONE.contains(subsoil)) drainageLoss -= 0.45f;
            else if (Tags.Blocks.STONE.contains(subsoil)) drainageLoss -= 0.5f;
            else if (BlockTags.STONE_BRICKS.contains(subsoil)) drainageLoss -= 0.48f;
            else if (Tags.Blocks.ORES.contains(subsoil)) drainageLoss -= 0.45f;
            else if (Tags.Blocks.GRAVEL.contains(subsoil)) drainageLoss += 0.68f;
            else if (Tags.Blocks.SAND.contains(subsoil)) drainageLoss += 0.35f;
            else if (subsoil instanceof WetSpongeBlock) drainageLoss -= 1.35f; // wet sponge provides moisture
            else if (subsoil instanceof SpongeBlock) drainageLoss += 1.35f;  // dry sponge drains water "proactively"
//            EnvironmentLib.LOGGER.debug(MarkerManager.getMarker("SoilBlock"), "drainage: " + drainageLoss);
        }

        //total moisture loss
        moistureLoss = MathHelper.clamp(Math.round(directMoistureLoss + evaporationLoss + drainageLoss), 0, 3);
//        EnvironmentLib.LOGGER.debug(MarkerManager.getMarker("SoilBlock"), "total moisture loss: " + moistureLoss);
        if (moistureLoss > moistureGain) {
            worldIn.spawnParticle(ParticleTypes.MYCELIUM, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, 4, 0.25, 0.02, 0.25, 0.1);
        } else {
            worldIn.spawnParticle(ParticleTypes.SPLASH, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, 2, 0.25, 0.06, 0.25, 0.1);
        }
        soilContext.moisture -= moistureLoss;

        BlockPos cropPos = pos.up();
        BlockState cropState = worldIn.getBlockState(cropPos);

        if (cropState.getBlock() == Blocks.AIR) {
            soilContext.getTileState().resetCropYield();
        }
        else {
            //"boost" crop growth if high N concentration available in soil
            float boostChance = soilContext.nitrogen * PlantMacronutrient.NITROGEN.getAvailabilityPctInSoil(soilContext.pH) / soilContext.getMaxNutrientAmount();
            if (rand.nextFloat() < boostChance) {
                Block cropBlock = cropState.getBlock();
                Optional<ICropEntry> optionalICrop = CropCultivationMod.PROXY.getCropRegistry().get(cropBlock.getRegistryName());
                if (optionalICrop.isPresent()) {
                    if (!worldIn.getPendingBlockTicks().isTickPending(cropPos, cropBlock)) {
//                CropCultivationMod.LOGGER.debug(MarkerManager.getMarker("SoilBlock"), "force growing of crop: " + cropState.getBlock());
                        worldIn.getPendingBlockTicks().scheduleTick(cropPos, cropBlock, 2); //we are lazy and tick the crop instead
                    }
                }
                else if (cropBlock instanceof IGrowable) { //fallback for not registered crops
                    if (worldIn.rand.nextFloat() < CropUtil.getBaseGrowthChance()) {
                        IGrowable iGrowable = (IGrowable) cropBlock;
                        if (iGrowable.canGrow(worldIn, cropPos, cropState, false)) {

//                        CropCultivationMod.LOGGER.debug(MarkerManager.getMarker("SoilBlock"), "force growing of crop: " + cropState.getBlock());
                            iGrowable.grow(worldIn, worldIn.rand, cropPos, cropState);
                            worldIn.playEvent(Constants.WorldEvents.BONEMEAL_PARTICLES, cropPos, 5);
                            BlockState newCropState = worldIn.getBlockState(cropPos); //get updated block state

                            boolean useDefaultGrowth = ModTags.Blocks.USE_DEFAULT_GROWTH.contains(cropState.getBlock());

                            Optional<IntegerProperty> ageProperty = BlockPropertyUtil.getAgeProperty(cropState);
                            if (ageProperty.isPresent()) {
                                IntegerProperty age = ageProperty.get();
                                int cropAge = cropState.get(age);
                                int maxCropAge = BlockPropertyUtil.getMaxAge(age);

                                //consume nutrients
                                if (useDefaultGrowth) {
                                    CropUtil.FallbackCrop.consumeSoilNutrients(worldIn.rand, cropAge, maxCropAge, soilContext);
                                }
                                else {
                                    CropUtil.RegisteredCrop.consumeSoilNutrients(worldIn.rand, cropAge, maxCropAge, CropRegistry.GENERIC_CROP, soilContext);
                                }

                                //update crop yield
                                int newCropAge = BlockPropertyUtil.getAge(newCropState);
                                CropUtil.FallbackCrop.updateYield(cropAge, newCropAge, soilContext);
                            }
                            else {//fallback, what IGrowable has no age property? (tall flowers! lol)
                                soilContext.getTileState().resetCropYield();

                                // penalize the player for using "illegal" plant
                                if (useDefaultGrowth) {
                                    if (worldIn.rand.nextFloat() < 0.35f) soilContext.nitrogen -= 2;
                                    if (worldIn.rand.nextFloat() < 0.35f) soilContext.phosphorus -= 2;
                                    if (worldIn.rand.nextFloat() < 0.35f) soilContext.potassium -= 1;
                                }
                                else {
                                    if (CropUtil.RegisteredCrop.canConsumeNutrient(worldIn.rand, CropRegistry.GENERIC_CROP.getNitrogenNeed())) soilContext.nitrogen -= 2;
                                    if (CropUtil.RegisteredCrop.canConsumeNutrient(worldIn.rand, CropRegistry.GENERIC_CROP.getPhosphorusNeed())) soilContext.phosphorus -= 2;
                                    if (CropUtil.RegisteredCrop.canConsumeNutrient(worldIn.rand, CropRegistry.GENERIC_CROP.getPotassiumNeed())) soilContext.potassium -= 1;
                                }
                            }
                            CropUtil.FallbackCrop.consumeSoilMoisture(cropPos, newCropState, soilContext);
                        }
                    }
                }
                else if (cropBlock instanceof IPlantable) { //fallback, incompatible plants
                    if (cropBlock.ticksRandomly(cropState) && !worldIn.getPendingBlockTicks().isTickPending(cropPos, cropBlock)) {
                        worldIn.getPendingBlockTicks().scheduleTick(cropPos, cropBlock, 2);
                    }
                }
            }
        }

        //decompose organic matter into nutrients
        if (soilContext.organicMatter > 0) {
            float threshold = soilContext.getMaxNutrientAmount() * 0.7f;
            boolean isSoilLowInNutrients = soilContext.nitrogen < threshold && soilContext.phosphorus < threshold || soilContext.potassium < threshold;
            if (isSoilLowInNutrients && worldIn.rand.nextFloat() < 0.025f) {
                soilContext.pH -= 0.1f;
                soilContext.organicMatter--;
                soilContext.nitrogen += rand.nextInt(2) + 1;
                soilContext.phosphorus++;
                soilContext.potassium++;
            }
        }

        if (hasCrops && soilContext.moisture < SoilMoisture.AVERAGE_1.getMoistureLevel()) {
            cropState = worldIn.getBlockState(cropPos); //get new state in case it changed
            if (cropState.getBlock() instanceof CropsBlock) {
                float fertilizerBurnProbability = 0;
                if (soilContext.phosphorus > 8) fertilizerBurnProbability += CropCultivationConfig.FERTILIZER_BURN_CHANCE.get() * 0.5f;
                if (soilContext.potassium > 8) fertilizerBurnProbability += CropCultivationConfig.FERTILIZER_BURN_CHANCE.get() * 0.45f;
                if (worldIn.rand.nextFloat() < fertilizerBurnProbability) {
                    worldIn.setBlockState(cropPos, ModBlocks.DEAD_CROP_WITHERED.getDefaultState()); //fertilizer burn (kill crop)
                }
            }
        }

        soilContext.update(worldIn, hasWaterSource || isRaining); // update changes to world
    }

    @Override
    public void fillWithRain(World worldIn, BlockPos pos) {
        if (worldIn instanceof ServerWorld && worldIn.rand.nextFloat() < 0.36f) {
            if (ClimateUtil.getLocalTemperature(worldIn.getBiome(pos), pos, worldIn.getBlockState(pos)) >= 0.15F) {
                TileEntity tileEntity = worldIn.getTileEntity(pos);
                if (!(tileEntity instanceof SoilStateTileEntity)) return;
                CropCultivationMod.LOGGER.debug(MarkerManager.getMarker("SoilBlock"), "filling with rain...");

                SoilStateTileEntity tileState = (SoilStateTileEntity) tileEntity;
                BlockState blockState = worldIn.getBlockState(pos);
                int moisture = blockState.get(MOISTURE);
                moisture++; //increase moisture

                //decrease pH (rain is slightly acidic)
                float pH = tileState.getPH() - 0.2f;

                //wash away nutrients
                int organicMatter = blockState.get(ORGANIC_MATTER);
                CropCultivationMod.LOGGER.debug(MarkerManager.getMarker("SoilBlock"), "rain strength: " + worldIn.getRainStrength(1f));
                CropCultivationMod.LOGGER.debug(MarkerManager.getMarker("SoilBlock"), "moisture: " + moisture);
                CropCultivationMod.LOGGER.debug(MarkerManager.getMarker("SoilBlock"), "max moisture: " + soilTexture.getMaxMoistureCapacity(organicMatter));
                if (moisture > soilTexture.getMaxMoistureCapacity(organicMatter) && worldIn.getRainStrength(1f) > 0.75f) {
                    float nutrientsRetention = organicMatter * SoilTexture.ORGANIC_MATTER_MULTIPLIER; // max = 0.5f
                    if (worldIn.rand.nextFloat() < 0.9f - nutrientsRetention) {
                        moisture = MathHelper.clamp(moisture, 1, SoilMoisture.MAX_VALUE);
                        organicMatter = MathHelper.clamp(organicMatter, 0, 4);
                        tileState.setPH(pH);
                        tileState.setNitrogen(tileState.getNitrogen() - 1);
                        tileState.setPhosphorus(tileState.getPhosphorus() - 1);
                        tileState.setPotassium(tileState.getPotassium() - 1);
                        worldIn.setBlockState(pos, blockState.with(MOISTURE, moisture).with(ORGANIC_MATTER, organicMatter), Constants.BlockFlags.BLOCK_UPDATE);
                        return;
                    }
                }

                tileState.setPH(pH);
                worldIn.setBlockState(pos, blockState.with(MOISTURE, MathHelper.clamp(moisture, 0, SoilMoisture.MAX_VALUE)), Constants.BlockFlags.BLOCK_UPDATE);
            }
        }
    }

    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {

        // check if block was placed above
        if (facing == Direction.UP && !stateIn.isValidPosition(worldIn, currentPos)) {
            worldIn.getPendingBlockTicks().scheduleTick(currentPos, this, 1); //"turn to dirt?"
        }

        return stateIn;
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        ItemStack stack = player.getHeldItem(handIn);
        Item item = stack.getItem();
        ActionResultType result = ActionResultType.PASS;
        boolean shrinkStack = false;

        if (ModTags.Items.COMPOST_MATERIAL.contains(item) && state.get(ORGANIC_MATTER) < 4) {
            if (!worldIn.isRemote) {
                worldIn.setBlockState(pos, state.cycle(ORGANIC_MATTER), Constants.BlockFlags.BLOCK_UPDATE);
                shrinkStack = true;
            }
            result = ActionResultType.SUCCESS;
        }

        if (!worldIn.isRemote) {
            boolean isLimingMaterial = ModTags.Items.LIMING_MATERIAL.contains(item);
            boolean isAcidifyingMaterial = ModTags.Items.ACIDIFYING_MATERIAL.contains(item);
            boolean isFertilizer = ModTags.Items.FERTILIZER_GROUP.contains(item);

            if (isFertilizer || isLimingMaterial || isAcidifyingMaterial) {
                TileEntity tileEntity = worldIn.getTileEntity(pos);
                if (tileEntity instanceof SoilStateTileEntity) {
                    SoilStateTileEntity tileState = (SoilStateTileEntity) tileEntity;

                    if (isLimingMaterial) { // increase soil pH
                        float pH = tileState.getPH();
                        if (pH < (float) SoilPH.MAX_VALUE) {
                            tileState.setPH(pH + soilTexture.getLimingModifier()); //reflects lime effectiveness for increasing pH depending on soil consistency
                            shrinkStack = true;
                            result = ActionResultType.SUCCESS;
                        }
                    }

                    if (isAcidifyingMaterial) { // decrease soil pH
                        float pH = tileState.getPH();
                        if (pH > (float) SoilPH.MIN_VALUE) {
                            tileState.setPH(pH - soilTexture.getAcidifyingModifier());
                            shrinkStack = true;
                            result = ActionResultType.SUCCESS;
                        }
                    }

                    if (isFertilizer) { // increase nutrients in soil
                        boolean fertilizerUsed = false;
                        boolean isNFertilizer = ModTags.Items.N_FERTILIZER.contains(item);
                        boolean isPFertilizer = ModTags.Items.P_FERTILIZER.contains(item);
                        boolean isKFertilizer = ModTags.Items.K_FERTILIZER.contains(item);
                        boolean isSuperFertilizer = !isNFertilizer && !isPFertilizer && !isKFertilizer; // illegal/creative fertilizer (any item directly added to the fertilizer_group tag)
                        int max = tileState.getMaxNutrientAmount();
                        if (tileState.getNitrogen() < max && (isNFertilizer || isSuperFertilizer)) {
                            tileState.addNitrogen(1);
                            fertilizerUsed = true;
                        }
                        if (tileState.getPhosphorus() < max && (isPFertilizer || isSuperFertilizer)) {
                            tileState.addPhosphorus(1);
                            fertilizerUsed = true;
                        }
                        if (isKFertilizer || isSuperFertilizer) {
                            if (tileState.getPotassium() < max) {
                                tileState.addPotassium(1);
                                fertilizerUsed = true;
                            }
                            else if (state.get(MOISTURE) > SoilMoisture.EXCESSIVELY_WET.getMoistureLevel() && worldIn.rand.nextFloat() < 0.1f) {
                                fertilizerUsed = true;
                                worldIn.createExplosion(null, pos.getX(), pos.getY(), pos.getZ(), 1f, Explosion.Mode.BREAK);
                                if (player instanceof ServerPlayerEntity) ModTriggers.POTASSIUM_EXPLOSION.trigger((ServerPlayerEntity) player);
                            }
                        }

                        if (fertilizerUsed) {
                            shrinkStack = true;
                            result = ActionResultType.SUCCESS;
                        }
                    }
                }
            }

            if (shrinkStack) {
                if (!player.abilities.isCreativeMode) {
                    stack.shrink(1);
                }
            }
        }

        return result;
    }

    @Override
    public void onFallenUpon(World worldIn, BlockPos pos, Entity entityIn, float fallDistance) {
        //removed farmland trampling!
        entityIn.onLivingFall(fallDistance, 1.0F);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if(state.getBlock() != newState.getBlock()) {
            super.onReplaced(state, worldIn, pos, newState, isMoving);
        }
    }

    @Override
    @Nullable
    public abstract TileEntity createTileEntity(BlockState state, IBlockReader world);

    @Override
    public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new StringTextComponent(""));
        tooltip.add(new TranslationTextComponent("measurement.cropcultivation.soil_texture", new TranslationTextComponent("soil_texture." + soilTexture.name().toLowerCase())).applyTextStyle(TextFormatting.GRAY));
        tooltip.add(new TranslationTextComponent("measurement.cropcultivation.soil_ph", new TranslationTextComponent("soil_ph." + soilTexture.pHType.name().toLowerCase())).applyTextStyle(TextFormatting.GRAY));
        tooltip.add(new TranslationTextComponent("measurement.cropcultivation.drainage_type", new TranslationTextComponent("soil_drainage." + soilTexture.getDrainageType().name().toLowerCase())).applyTextStyle(TextFormatting.GRAY));
    }

    public static boolean doesSoilHaveWater(IWorldReader worldIn, BlockPos pos, int distance) {
        for (BlockPos blockpos : BlockPos.getAllInBoxMutable(pos.add(-distance, 0, -distance), pos.add(distance, 1, distance))) {
            if (worldIn.getFluidState(blockpos).isTagged(FluidTags.WATER)) return true;
        }
        return FarmlandWaterManager.hasBlockWaterTicket(worldIn, pos);
    }

//    public static float calculateMoistureAmbiance(IWorldReader worldIn, BlockPos pos, int distance) {
//        float score = 0f;
//
//        for (BlockPos blockpos : BlockPos.getAllInBoxMutable(pos.add(-distance, 0, -distance), pos.add(distance, 1, distance))) {
//            if (worldIn.getFluidState(blockpos).isTagged(FluidTags.WATER)) {
//                score += (distance - blockpos.manhattanDistance(pos) + 0.5f) / distance;
//            }
//        }
//        if (FarmlandWaterManager.hasBlockWaterTicket(worldIn, pos)) score += 1f;
//
//        float n = (distance + distance) * (distance + distance) - 1;
//        return score / n;
//    }
}
