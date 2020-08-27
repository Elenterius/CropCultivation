package com.creativechasm.cropcultivation.item;

import com.creativechasm.cropcultivation.CropCultivationMod;
import com.creativechasm.cropcultivation.environment.ClimateUtil;
import com.creativechasm.cropcultivation.environment.CropUtil;
import com.creativechasm.cropcultivation.environment.plant.IPlantGrowthCA;
import com.creativechasm.cropcultivation.environment.soil.SoilStateContext;
import com.creativechasm.cropcultivation.init.ModTags;
import com.creativechasm.cropcultivation.registry.CropRegistry;
import com.creativechasm.cropcultivation.registry.ICropEntry;
import com.creativechasm.cropcultivation.util.BlockPropertyUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.IGrowable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.IntegerProperty;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.IPlantable;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class CropReaderItem extends DeviceItem implements IMeasuringDevice
{
    public CropReaderItem(Properties properties) {
        super(properties);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (stack.hasTag() && stack.getTag() != null) {
            CompoundNBT nbtTag = stack.getTag();

            String commonId = nbtTag.getString("commonId");
            Optional<ICropEntry> optionalCrop = CropCultivationMod.PROXY.getCropRegistry().get(commonId);

            tooltip.add(new StringTextComponent(""));
            tooltip.add(new StringTextComponent("Crop: ").appendSibling(new StringTextComponent(nbtTag.getString("blockRN")).applyTextStyle(TextFormatting.GRAY)));
            tooltip.add(new StringTextComponent("Age: ").appendSibling(new StringTextComponent(nbtTag.getInt("age") + "/" + nbtTag.getInt("maxAge")).applyTextStyle(TextFormatting.GRAY)));

            tooltip.add(new StringTextComponent("Yield Sum: ").appendSibling(new StringTextComponent(String.format("%s", nbtTag.contains("yieldSum") ? nbtTag.getFloat("yieldSum") + "" : "?")).applyTextStyle(TextFormatting.GRAY)));
            tooltip.add(new StringTextComponent("Yield Multiplier: ").appendSibling(new StringTextComponent(String.format("%.2f", nbtTag.contains("yieldMultiplier") ? nbtTag.getFloat("yieldMultiplier") : 1.0f)).applyTextStyle(TextFormatting.GRAY)));

            tooltip.add(new StringTextComponent(""));
            tooltip.add(new StringTextComponent("Common ID: ").appendSibling(new StringTextComponent(optionalCrop.isPresent() ? commonId : "?").applyTextStyle(TextFormatting.GRAY)));
            optionalCrop.ifPresent(iCropEntry -> tooltip.add(new StringTextComponent("Registered: ").appendSibling(new StringTextComponent(nbtTag.getString("registered")).applyTextStyle(TextFormatting.GRAY))));

            if (optionalCrop.isPresent()) {
                ICropEntry cropEntry = optionalCrop.get();
                tooltip.add(new StringTextComponent(""));
                tooltip.add(new StringTextComponent("Needs"));
                tooltip.add(new StringTextComponent(String.format("Nitrogen: %s%%", (int) (cropEntry.getNitrogenNeed() * 100))).applyTextStyle(TextFormatting.GRAY));
                tooltip.add(new StringTextComponent(String.format("Phosphorus: %s%%", (int) (cropEntry.getPhosphorusNeed() * 100))).applyTextStyle(TextFormatting.GRAY));
                tooltip.add(new StringTextComponent(String.format("Potassium: %s%%", (int) (cropEntry.getPotassiumNeed() * 100))).applyTextStyle(TextFormatting.GRAY));
                tooltip.add(new StringTextComponent(""));
                tooltip.add(new StringTextComponent("Tolerance"));
                tooltip.add(new StringTextComponent(String.format("Moisture: %s - %s", cropEntry.getMinSoilMoisture(), cropEntry.getMaxSoilMoisture())).applyTextStyle(TextFormatting.GRAY));
                tooltip.add(new StringTextComponent(String.format("pH: %s - %s", cropEntry.getMinSoilPH(), cropEntry.getMaxSoilPH())).applyTextStyle(TextFormatting.GRAY));
                tooltip.add(new StringTextComponent(String.format("Temperature: %.2f°C - %.2f°C (%.2f - %.2f)", ClimateUtil.convertTemperatureMCToCelsius(cropEntry.getMinTemperature()), ClimateUtil.convertTemperatureMCToCelsius(cropEntry.getMaxTemperature()), cropEntry.getMinTemperature(), cropEntry.getMaxTemperature())).applyTextStyle(TextFormatting.GRAY));

                if (cropEntry instanceof IPlantGrowthCA) {
                    tooltip.add(new StringTextComponent(""));
                    tooltip.add(new StringTextComponent("Neighborhood"));
                    tooltip.add(new StringTextComponent(String.format("Metric: %s", ((IPlantGrowthCA) cropEntry).getNeighborhood())));
                    tooltip.add(new StringTextComponent(String.format("Population: %s - %s", ((IPlantGrowthCA) cropEntry).getMinPlantNeighbors(), ((IPlantGrowthCA) cropEntry).getMaxPlantNeighbors())));
                }
            }
        }
        else {
            super.addInformation(stack, worldIn, tooltip, flagIn);
        }
    }

    @Override
    public void gatherData(ServerWorld world, PlayerEntity player, BlockPos pos, ItemStack stack) {
        BlockState state = world.getBlockState(pos);
        boolean isPlant = state.getBlock() instanceof IGrowable || state.getBlock() instanceof IPlantable;

        if (isPlant) {
            CompoundNBT nbtTag = stack.getOrCreateTag();

            String age = "?", maxAge = "?";
            int ageInt = 0;
            Optional<IntegerProperty> ageProperty = BlockPropertyUtil.getAgeProperty(state);
            if (ageProperty.isPresent()) {
                ageInt = state.get(ageProperty.get());
                int maxAgeInt = BlockPropertyUtil.getMaxAge(ageProperty.get());
                nbtTag.putInt("age", ageInt);
                nbtTag.putInt("maxAge", maxAgeInt);
                age = "" + ageInt;
                maxAge = "" + maxAgeInt;
            }
            else {
                nbtTag.remove("age");
                nbtTag.remove("maxAge");
            }

            nbtTag.putString("blockRN", Optional.ofNullable(state.getBlock().getRegistryName()).map(ResourceLocation::toString).orElse("?"));

            boolean useDefaultGrowth = ModTags.Blocks.USE_DEFAULT_GROWTH.contains(state.getBlock());
            if (!useDefaultGrowth) {
                Optional<ICropEntry> optionalCrop = CropCultivationMod.PROXY.getCropRegistry().get(state.getBlock().getRegistryName());
                ICropEntry cropEntry = optionalCrop.orElse(CropRegistry.GENERIC_CROP);

                Optional<String> optionalId = CropCultivationMod.PROXY.getCropRegistry().getCommonId(cropEntry);
                optionalId.ifPresent(id -> {
                    nbtTag.putString("commonId", id);
                    nbtTag.putString("registered", "" + CropCultivationMod.PROXY.getCropRegistry().getModsFor(id));
                });

                SoilStateContext soilContext = new SoilStateContext(world, pos.down());
                boolean canGrow = soilContext.isValid && CropUtil.RegisteredCrop.canCropGrow(world, pos, state, cropEntry, soilContext);
                float growthChange = soilContext.isValid ? CropUtil.RegisteredCrop.getGrowthChance(cropEntry, soilContext) : 0f;

                if (soilContext.isValid) {
                    nbtTag.putFloat("yieldSum", soilContext.getTileState().getCropYieldSum());
                    if (ageProperty.isPresent()) {
                        nbtTag.putFloat("yieldMultiplier", soilContext.getTileState().getCropYieldAveraged(ageInt));
                    }
                }

                if (player instanceof ServerPlayerEntity) {
                    player.sendStatusMessage(
                            new StringTextComponent(String.format("Common ID: %s", optionalId.orElse("?")))
                                    .appendSibling(new StringTextComponent(" - ").applyTextStyle(TextFormatting.GRAY))
                                    .appendSibling(new StringTextComponent(String.format("Age: %s/%s", age, maxAge)))
                                    .appendSibling(new StringTextComponent(" - ").applyTextStyle(TextFormatting.GRAY))
                                    .appendSibling(new StringTextComponent(String.format("Can Grow: %s", canGrow)).applyTextStyle(canGrow ? TextFormatting.GREEN : TextFormatting.RED))
                                    .appendSibling(new StringTextComponent(" - ").applyTextStyle(TextFormatting.GRAY))
                                    .appendSibling(new StringTextComponent(String.format("Grow Chance: %.3f", growthChange)).applyTextStyle(TextFormatting.WHITE))
                            , true
                    );
                }

                return;
            }

            //fallback
            nbtTag.remove("commonId");
            nbtTag.remove("yieldSum");
            nbtTag.remove("yieldMultiplier");
            if (player instanceof ServerPlayerEntity) {
                player.sendStatusMessage(new StringTextComponent(String.format("Age: %s/%s", age, maxAge)), true);
            }
        }
    }
}
