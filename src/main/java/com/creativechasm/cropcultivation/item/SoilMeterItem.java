package com.creativechasm.cropcultivation.item;

import com.creativechasm.cropcultivation.block.SoilBlock;
import com.creativechasm.cropcultivation.block.SoilStateTileEntity;
import com.creativechasm.cropcultivation.environment.ClimateUtil;
import com.creativechasm.cropcultivation.environment.plant.PlantMacronutrient;
import com.creativechasm.cropcultivation.environment.soil.SoilPH;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class SoilMeterItem extends Item implements IMeasuringDevice
{
    public SoilMeterItem(Properties properties) {
        super(properties);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (stack.hasTag() && stack.getTag() != null) {
            CompoundNBT nbtTag = stack.getTag();

            float pH = nbtTag.getFloat("pH");
            int moisture = Math.round((nbtTag.getInt("moisture") / 9f) * 100);
            float localTemperature = nbtTag.getFloat("localTemperature");
            int lightLevel = (nbtTag.getInt("light_level"));

            tooltip.add(new StringTextComponent(""));
            tooltip.add(new TranslationTextComponent("measurement.desc"));
            tooltip.add(new TranslationTextComponent("measurement.soil_moisture", moisture + "%").applyTextStyle(TextFormatting.GRAY));
            tooltip.add(new TranslationTextComponent("measurement.temperature", String.format("%.2f\u00B0C (%.3f)", ClimateUtil.convertTemperatureMCToCelsius(localTemperature), localTemperature)).applyTextStyle(TextFormatting.GRAY));
            tooltip.add(new TranslationTextComponent("measurement.light_level", lightLevel).applyTextStyle(TextFormatting.GRAY));

            tooltip.add(new StringTextComponent(""));
            tooltip.add(SoilPH.getTextComponentForPH(pH, String.format("pH: %.1f (%s)", pH, SoilPH.fromPH(pH).name())));

            int nitrogen = nbtTag.getInt("N");
            float nPct = PlantMacronutrient.NITROGEN.getAvailabilityPctInSoil(pH);
            int phosphorus = nbtTag.getInt("P");
            float pPct = PlantMacronutrient.PHOSPHORUS.getAvailabilityPctInSoil(pH);
            int potassium = nbtTag.getInt("K");
            float kPct = PlantMacronutrient.POTASSIUM.getAvailabilityPctInSoil(pH);

            tooltip.add(new StringTextComponent(""));
            tooltip.add(new TranslationTextComponent("nutrient.nitrogen")
                    .appendSibling(new StringTextComponent(String.format(": %d%% x %.1f = ", nitrogen * 10, nPct)).applyTextStyle(TextFormatting.DARK_GRAY)
                    .appendSibling(new StringTextComponent(String.format("%.2f%%", nitrogen * nPct * 10f)).applyTextStyle(TextFormatting.GRAY))));
            tooltip.add(new TranslationTextComponent("nutrient.phosphorus")
                    .appendSibling(new StringTextComponent(String.format(": %d%% x %.1f = ", phosphorus * 10, pPct)).applyTextStyle(TextFormatting.DARK_GRAY)
                    .appendSibling(new StringTextComponent(String.format("%.2f%%", phosphorus * pPct * 10f)).applyTextStyle(TextFormatting.GRAY))));
            tooltip.add(new TranslationTextComponent("nutrient.potassium")
                    .appendSibling(new StringTextComponent(String.format(": %d%% x %.1f = ", potassium * 10, kPct)).applyTextStyle(TextFormatting.DARK_GRAY)
                    .appendSibling(new StringTextComponent(String.format("%.2f%%", potassium * kPct * 10f)).applyTextStyle(TextFormatting.GRAY))));
        }
        else {
            tooltip.add(new StringTextComponent(" ").appendSibling(new TranslationTextComponent("soil_meter.desc").applyTextStyle(TextFormatting.GRAY)));
        }
    }

    @Override
    public void gatherData(ServerWorld world, PlayerEntity player, BlockPos pos, ItemStack stack) {
        BlockState state = world.getBlockState(pos);
        boolean isSoilBlock = state.getBlock() instanceof SoilBlock;

        if (!isSoilBlock) {
            BlockPos tempPos = pos.down();
            BlockState tempState = world.getBlockState(tempPos);
            if (tempState.getBlock() instanceof SoilBlock) {
                pos = tempPos;
                state = tempState;
                isSoilBlock = true;
            }
        }

        if (isSoilBlock) {
            if (!world.isRemote) {
                TileEntity tileEntity = world.getTileEntity(pos);
                if (tileEntity instanceof SoilStateTileEntity) {
                    SoilStateTileEntity tileState = (SoilStateTileEntity) tileEntity;

                    CompoundNBT nbtTag = stack.getOrCreateTag();
                    int moisture = state.get(SoilBlock.MOISTURE);
                    float localTemperature = ClimateUtil.getLocalTemperature(world.getBiome(pos), pos, state);
                    int lightLevel = world.getLightSubtracted(pos.up(), 0);
                    float pH = tileState.getPH();

                    nbtTag.putInt("moisture", moisture);
                    nbtTag.putFloat("localTemperature", localTemperature);
                    nbtTag.putInt("light_level", lightLevel);
                    nbtTag.putFloat("pH", pH);
                    nbtTag.putInt("N", tileState.getNitrogen());
                    nbtTag.putInt("P", tileState.getPhosphorus());
                    nbtTag.putInt("K", tileState.getPotassium());

                    if (player instanceof ServerPlayerEntity) {
                        player.sendStatusMessage(
                                new TranslationTextComponent("measurement.soil_moisture", Math.round(moisture / 9f * 100) + "%").applyTextStyle(moisture >= 5 ? TextFormatting.AQUA : TextFormatting.WHITE)
                                        .appendSibling(new StringTextComponent(" - ").applyTextStyle(TextFormatting.GRAY))
                                        .appendSibling(new StringTextComponent(String.format("%.2f\u00B0C (%.3f)", ClimateUtil.convertTemperatureMCToCelsius(localTemperature), localTemperature)).applyTextStyle(TextFormatting.WHITE))
                                        .appendSibling(new StringTextComponent(" - ").applyTextStyle(TextFormatting.GRAY))
                                        .appendSibling(new TranslationTextComponent("measurement.light_level", lightLevel).applyTextStyle(TextFormatting.YELLOW))
                                        .appendSibling(new StringTextComponent(" - ").applyTextStyle(TextFormatting.GRAY))
                                        .appendSibling(SoilPH.getTextComponentForPH(tileState.getPH(), String.format("pH: %.1f", tileState.getPH()))
                                                .appendSibling(new StringTextComponent(" - ").applyTextStyle(TextFormatting.GRAY))
                                                .appendSibling(PlantMacronutrient.getTextComponentForNutrient(PlantMacronutrient.NITROGEN, tileState.getNitrogen()))
                                                .appendSibling(new StringTextComponent(" - ").applyTextStyle(TextFormatting.GRAY))
                                                .appendSibling(PlantMacronutrient.getTextComponentForNutrient(PlantMacronutrient.PHOSPHORUS, tileState.getPhosphorus()))
                                                .appendSibling(new StringTextComponent(" - ").applyTextStyle(TextFormatting.GRAY))
                                                .appendSibling(PlantMacronutrient.getTextComponentForNutrient(PlantMacronutrient.POTASSIUM, tileState.getPotassium())))
                                , true
                        );
                    }
                }
            }
        }
    }
}
