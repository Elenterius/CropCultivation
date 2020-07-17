package com.creativechasm.environment.common.item;

import com.creativechasm.environment.api.world.ClimateUtil;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
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

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class ThermoHygrometerItem extends Item {

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
            tooltip.add(new TranslationTextComponent("measurement.temperature", String.format("%.2f\u00B0C (%.2f)", ClimateUtil.convertTemperatureMCToCelsius(localTemperature), localTemperature)).applyTextStyle(TextFormatting.GRAY));
            tooltip.add(new TranslationTextComponent("measurement.humidity", String.format("%.2f RH", localHumidity)).applyTextStyle(TextFormatting.GRAY));

            tooltip.add(new StringTextComponent(""));
            tooltip.add(new StringTextComponent("Biome: ").appendSibling(new TranslationTextComponent(biomeName)).applyTextStyle(TextFormatting.GRAY));
            tooltip.add(new TranslationTextComponent("measurement.temperature", String.format("%.2f\u00B0C (%.2f)", ClimateUtil.convertTemperatureMCToCelsius(biomeTemperature), biomeTemperature)).applyTextStyle(TextFormatting.GRAY));
            tooltip.add(new TranslationTextComponent("measurement.dew_point", String.format("%.2f\u00B0C (%.2f)", ClimateUtil.convertTemperatureMCToCelsius(dewPointTemperature), dewPointTemperature)).applyTextStyle(TextFormatting.GRAY));
            tooltip.add(new TranslationTextComponent("measurement.humidity", String.format("%.2f RH", biomeHumidity)).applyTextStyle(TextFormatting.GRAY));

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
            float localTemperature = biome.getTemperature(pos);
            float localHumidity = ClimateUtil.calcRelativeHumidity(localTemperature, dewPointTemperature);

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
                                .appendSibling(new StringTextComponent(String.format("%.2f\u00B0C", ClimateUtil.convertTemperatureMCToCelsius(localTemperature))))
                                .appendSibling(new StringTextComponent(" - ").applyTextStyle(TextFormatting.GRAY))
                                .appendSibling(new StringTextComponent(String.format("%.2f RH", localHumidity)))
                        , true
                );
            }
        }

        return ActionResultType.SUCCESS;
    }

}
