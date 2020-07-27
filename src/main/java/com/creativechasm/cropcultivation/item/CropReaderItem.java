package com.creativechasm.cropcultivation.item;

import com.creativechasm.cropcultivation.api.block.BlockPropertyUtil;
import com.creativechasm.cropcultivation.api.plant.ICropEntry;
import com.creativechasm.cropcultivation.api.plant.IPlantGrowthCA;
import com.creativechasm.cropcultivation.init.CommonRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.IGrowable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.IntegerProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.IPlantable;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;

@ParametersAreNonnullByDefault
public class CropReaderItem extends Item implements IMeasuringDevice
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
            Optional<ICropEntry> optionalCrop = CommonRegistry.CROP_REGISTRY.get(commonId);

            tooltip.add(new StringTextComponent(""));
            tooltip.add(new StringTextComponent(String.format("Common ID: %s", optionalCrop.isPresent() ? commonId : "?")));
            tooltip.add(new StringTextComponent(String.format("Age: %s/%s", nbtTag.getInt("age"), nbtTag.getInt("maxAge"))));

            if (optionalCrop.isPresent()) {
                ICropEntry cropEntry = optionalCrop.get();
                tooltip.add(new StringTextComponent(""));
                tooltip.add(new StringTextComponent("Needs"));
                tooltip.add(new StringTextComponent(String.format("Nitrogen: %s%%", (int)(cropEntry.getNitrogenNeed() * 100))));
                tooltip.add(new StringTextComponent(String.format("Phosphorus: %s%%", (int)(cropEntry.getPhosphorusNeed() * 100))));
                tooltip.add(new StringTextComponent(String.format("Potassium: %s%%", (int)(cropEntry.getPotassiumNeed() * 100))));
                tooltip.add(new StringTextComponent(""));
                tooltip.add(new StringTextComponent(String.format("Moisture: %s - %s", cropEntry.getMinSoilMoisture(), cropEntry.getMaxSoilMoisture())));
                tooltip.add(new StringTextComponent(String.format("pH: %s - %s", cropEntry.getMinSoilPH(), cropEntry.getMaxSoilPH())));
                tooltip.add(new StringTextComponent(String.format("Temperature: %s - %s", cropEntry.getMinTemperature(), cropEntry.getMaxTemperature())));

                if (cropEntry instanceof IPlantGrowthCA) {
                    tooltip.add(new StringTextComponent(""));
                    tooltip.add(new StringTextComponent("Neighborhood"));
                    tooltip.add(new StringTextComponent(String.format("Metric: %s", ((IPlantGrowthCA) cropEntry).getNeighborhood())));
                    tooltip.add(new StringTextComponent(String.format("Population: %s - %s", ((IPlantGrowthCA) cropEntry).getMinPlantNeighbors(), ((IPlantGrowthCA) cropEntry).getMaxPlantNeighbors())));
                }
            }
        }
        else {
            tooltip.add(new StringTextComponent(" ").appendSibling(new TranslationTextComponent("crop_reader.desc").applyTextStyle(TextFormatting.GRAY)));
        }
    }

    @Override
    public void gatherData(ServerWorld world, PlayerEntity player, BlockPos pos, ItemStack stack) {
        BlockState state = world.getBlockState(pos);
        boolean isPlant = state.getBlock() instanceof IGrowable || state.getBlock() instanceof IPlantable;

        if (isPlant) {
            CompoundNBT nbtTag = stack.getOrCreateTag();

            String age = "?", maxAge = "?";
            Optional<IntegerProperty> ageProperty = BlockPropertyUtil.getAgeProperty(state);
            if (ageProperty.isPresent()) {
                int ageInt = state.get(ageProperty.get());
                int maxAgeInt = BlockPropertyUtil.getMaxAge(ageProperty.get());
                nbtTag.putInt("age", ageInt);
                nbtTag.putInt("maxAge", maxAgeInt);
                age = "" + ageInt;
                maxAge = "" + maxAgeInt;
            }

            Optional<ICropEntry> optionalCrop = CommonRegistry.CROP_REGISTRY.get(state.getBlock().getRegistryName());
            if (optionalCrop.isPresent()) {
                ICropEntry cropEntry = optionalCrop.get();
                Optional<String> optionalId = CommonRegistry.CROP_REGISTRY.getCommonId(cropEntry);
                optionalId.ifPresent(id -> nbtTag.putString("commonId", id));

                boolean canGrow = false; // CropUtil.RegisteredCrop.canCropGrow()

                if (player instanceof ServerPlayerEntity) {
                    player.sendStatusMessage(
                            new StringTextComponent(String.format("Common ID: %s", optionalId.orElse("?")))
                                    .appendSibling(new StringTextComponent(" - ").applyTextStyle(TextFormatting.GRAY))
                                    .appendSibling(new StringTextComponent(String.format("Age: %s/%s", age, maxAge)))
                                    .appendSibling(new StringTextComponent(" - ").applyTextStyle(TextFormatting.GRAY))
                                    .appendSibling(new StringTextComponent(String.format("Can Grow: %s", canGrow)).applyTextStyle(canGrow ? TextFormatting.GREEN : TextFormatting.RED))
                            , true
                    );
                }
            }
            else {
                nbtTag.remove("commonId");
                if (player instanceof ServerPlayerEntity) {
                    player.sendStatusMessage(new StringTextComponent(String.format("Age: %s/%s", age, maxAge)), true);
                }
            }
        }
    }
}
