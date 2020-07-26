package com.creativechasm.cropcultivation.item;

import com.creativechasm.cropcultivation.api.block.SoilBlock;
import com.creativechasm.cropcultivation.api.block.SoilStateTileEntity;
import com.creativechasm.cropcultivation.api.plant.PlantMacronutrient;
import com.creativechasm.cropcultivation.api.soil.SoilPH;
import com.creativechasm.cropcultivation.api.world.ClimateUtil;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
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
public class SoilMeterItem extends Item
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
            tooltip.add(new TranslationTextComponent("measurement.temperature", String.format("%.2f\u00B0C (%.2f)", ClimateUtil.convertTemperatureMCToCelsius(localTemperature), localTemperature)).applyTextStyle(TextFormatting.GRAY));
            tooltip.add(new TranslationTextComponent("measurement.light_level", lightLevel).applyTextStyle(TextFormatting.GRAY));

            tooltip.add(new StringTextComponent(""));
            tooltip.add(SoilPH.getTextComponentForPH(pH, String.format("pH: %.1f (%s)", pH, SoilPH.fromPH(pH).name())));
            tooltip.add(
                    new StringTextComponent("N: " + nbtTag.getInt("N")).applyTextStyle(TextFormatting.GRAY)
                            .appendSibling(new StringTextComponent(" - ").applyTextStyle(TextFormatting.DARK_GRAY))
                            .appendSibling(new StringTextComponent("P: " + nbtTag.getInt("P")).applyTextStyle(TextFormatting.GRAY))
                            .appendSibling(new StringTextComponent(" - ").applyTextStyle(TextFormatting.DARK_GRAY))
                            .appendSibling(new StringTextComponent("K: " + nbtTag.getInt("K")).applyTextStyle(TextFormatting.GRAY))
            );
        }
        else {
            tooltip.add(new StringTextComponent(" ").appendSibling(new TranslationTextComponent("soil_meter.desc").applyTextStyle(TextFormatting.GRAY)));
        }
    }

    @Override
    public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getPos();
        BlockState state = world.getBlockState(pos);
        boolean isSoilBlock = state.getBlock() instanceof SoilBlock;

        if (!isSoilBlock) {
            BlockPos tempPos = context.getPos().down();
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
                    Biome biome = world.getBiome(pos);

                    CompoundNBT nbtTag = stack.getOrCreateTag();
                    int moisture = state.get(SoilBlock.MOISTURE);
                    float localTemperature = biome.getTemperature(pos);
                    int lightLevel = world.getLightSubtracted(pos.up(), 0);

                    nbtTag.putInt("moisture", moisture);
                    nbtTag.putFloat("localTemperature", localTemperature);
                    nbtTag.putInt("light_level", lightLevel);
                    nbtTag.putFloat("pH", tileState.getPH());
                    nbtTag.putInt("N", tileState.getNitrogen());
                    nbtTag.putInt("P", tileState.getPhosphorus());
                    nbtTag.putInt("K", tileState.getPotassium());

                    PlayerEntity player = context.getPlayer();
                    if (player instanceof ServerPlayerEntity) {
                        player.sendStatusMessage(new TranslationTextComponent("measurement.soil_moisture", Math.round(moisture / 9f * 100) + "%").applyTextStyle(moisture >= 5 ? TextFormatting.AQUA : TextFormatting.WHITE)
                                        .appendSibling(new StringTextComponent(" - ").applyTextStyle(TextFormatting.GRAY))
                                        .appendSibling(new StringTextComponent(String.format("%.2f\u00B0C (%.2f)", ClimateUtil.convertTemperatureMCToCelsius(localTemperature), localTemperature)))
                                        .appendSibling(new StringTextComponent(" - ").applyTextStyle(TextFormatting.GRAY))
                                        .appendSibling(new TranslationTextComponent("measurement.light_level", lightLevel))
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

                    return ActionResultType.SUCCESS;
                }
            }
            return ActionResultType.FAIL;
        }
        return ActionResultType.PASS;
    }
}
