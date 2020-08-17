package com.creativechasm.cropcultivation.item;

import com.creativechasm.cropcultivation.environment.ClimateUtil;
import com.creativechasm.cropcultivation.util.MathHelperX;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.IPlantable;

import javax.annotation.Nullable;
import java.util.List;

public class ThermoHygrometerItem extends DeviceItem {

    public ThermoHygrometerItem(Properties properties) {
        super(properties);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (stack.hasTag() && stack.getTag() != null) {
            CompoundNBT nbtTag = stack.getTag();

            String biomeName = nbtTag.getString("biomeName");
            float biomeTemperature = nbtTag.getFloat("biomeTemperature");
            float biomeHumidity = nbtTag.getFloat("biomeHumidity");
            float dewPointTemperature = nbtTag.getFloat("dewPointTemperature");
            float localTemperature = nbtTag.getFloat("localTemperature");
            float localHumidity = nbtTag.getFloat("localHumidity");

            tooltip.add(new StringTextComponent(""));
            tooltip.add(new TranslationTextComponent("measurement.cropcultivation.desc"));
            tooltip.add(new TranslationTextComponent("measurement.cropcultivation.temperature", String.format("%.2f\u00B0C (%.3f)", ClimateUtil.convertTemperatureMCToCelsius(localTemperature), localTemperature)).applyTextStyle(TextFormatting.GRAY));
            tooltip.add(new TranslationTextComponent("measurement.cropcultivation.humidity", String.format("%.2f RH", localHumidity)).applyTextStyle(TextFormatting.GRAY));
            tooltip.add(new TranslationTextComponent("measurement.cropcultivation.dew_point", String.format("%.2f\u00B0C (%.3f)", ClimateUtil.convertTemperatureMCToCelsius(dewPointTemperature), dewPointTemperature)).applyTextStyle(TextFormatting.GRAY));
            tooltip.add(new StringTextComponent(""));
            tooltip.add(new StringTextComponent("Biome: ").appendSibling(new TranslationTextComponent(biomeName)).applyTextStyle(TextFormatting.GRAY));
            tooltip.add(new TranslationTextComponent("measurement.cropcultivation.default_temperature", String.format("%.2f\u00B0C (%.3f)", ClimateUtil.convertTemperatureMCToCelsius(biomeTemperature), biomeTemperature)).applyTextStyle(TextFormatting.GRAY));
            tooltip.add(new TranslationTextComponent("measurement.cropcultivation.default_humidity", String.format("%.2f RH", biomeHumidity)).applyTextStyle(TextFormatting.GRAY));
        }
        else {
            super.addInformation(stack, worldIn, tooltip, flagIn);
        }
    }

    @Override
    public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context) {
        World world = context.getWorld();

        if (!world.isRemote) {
            BlockPos pos = context.getPos();
            Biome biome = world.getBiome(pos);

            float biomeTemperature = biome.getDefaultTemperature();
            float biomeHumidity = biome.getDownfall();
            float dewPointTemperature = ClimateUtil.calcDewPointTemperature(biomeTemperature, biomeHumidity);

            float localTemperature;
            BlockState state = world.getBlockState(pos);
            if (state.getBlock() instanceof IPlantable) {
                BlockPos posBelow = pos.down();
                BlockState below = world.getBlockState(posBelow);
                localTemperature = MathHelperX.lerp(0.7f, ClimateUtil.getLocalTemperature(biome, pos, state), ClimateUtil.getLocalTemperature(world.getBiome(posBelow), posBelow, below));
            }
            else {
                localTemperature = ClimateUtil.getLocalTemperature(biome, pos, state);
            }

            float localHumidity = Math.max(0f, ClimateUtil.calcRelativeHumidity(biome.getTemperature(pos), dewPointTemperature));

            CompoundNBT nbtTag = stack.getOrCreateTag();
            nbtTag.putString("biomeName", biome.getTranslationKey());
            nbtTag.putFloat("biomeTemperature", biomeTemperature);
            nbtTag.putFloat("biomeHumidity", biomeHumidity);
            nbtTag.putFloat("dewPointTemperature", dewPointTemperature);
            nbtTag.putFloat("localTemperature", localTemperature);
            nbtTag.putFloat("localHumidity", localHumidity);

            PlayerEntity player = context.getPlayer();
            if (player instanceof ServerPlayerEntity) {
                player.sendStatusMessage(
                        new TranslationTextComponent(biome.getTranslationKey())
                                .appendSibling(new StringTextComponent(" - ").applyTextStyle(TextFormatting.GRAY))
                                .appendSibling(new StringTextComponent(String.format("%.2f\u00B0C (%.3f)", ClimateUtil.convertTemperatureMCToCelsius(localTemperature), localTemperature)))
                                .appendSibling(new StringTextComponent(" - ").applyTextStyle(TextFormatting.GRAY))
                                .appendSibling(new StringTextComponent(String.format("%.2f RH", localHumidity)))
                        , true
                );
            }
        }

        return ActionResultType.SUCCESS;
    }

}
