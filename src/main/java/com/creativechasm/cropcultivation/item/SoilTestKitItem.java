package com.creativechasm.cropcultivation.item;

import com.creativechasm.cropcultivation.block.SoilBlock;
import com.creativechasm.cropcultivation.block.SoilStateTileEntity;
import com.creativechasm.cropcultivation.environment.plant.PlantMacronutrient;
import com.creativechasm.cropcultivation.environment.soil.SoilPH;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class SoilTestKitItem extends DeviceItem
{

    public SoilTestKitItem(Properties properties) {
        super(properties);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (stack.hasTag() && stack.getTag() != null) {
            CompoundNBT nbtTag = stack.getTag();

            tooltip.add((new StringTextComponent("")));
            tooltip.add((new TranslationTextComponent("measurement.desc")));

            float pH = nbtTag.getFloat("pH");
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
            super.addInformation(stack, worldIn, tooltip, flagIn);
        }
    }

    @Override
    public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getPos();
        BlockState blockState = world.getBlockState(pos);
        if (blockState.getBlock() instanceof SoilBlock) {
            if (!world.isRemote) {
                TileEntity tileEntity = world.getTileEntity(pos);
                if (tileEntity instanceof SoilStateTileEntity) {
                    SoilStateTileEntity tileState = (SoilStateTileEntity) tileEntity;

                    CompoundNBT nbtTag = stack.getOrCreateTag();
                    nbtTag.putFloat("pH", tileState.getPH());
                    nbtTag.putInt("N", tileState.getNitrogen());
                    nbtTag.putInt("P", tileState.getPhosphorus());
                    nbtTag.putInt("K", tileState.getPotassium());

                    PlayerEntity player = context.getPlayer();
                    if (player instanceof ServerPlayerEntity) {
                        player.sendStatusMessage(
                                SoilPH.getTextComponentForPH(tileState.getPH(), String.format("pH: %.1f", tileState.getPH()))
                                        .appendSibling(new StringTextComponent(" - ").applyTextStyle(TextFormatting.GRAY))
                                        .appendSibling(PlantMacronutrient.getTextComponentForNutrient(PlantMacronutrient.NITROGEN, tileState.getNitrogen()))
                                        .appendSibling(new StringTextComponent(" - ").applyTextStyle(TextFormatting.GRAY))
                                        .appendSibling(PlantMacronutrient.getTextComponentForNutrient(PlantMacronutrient.PHOSPHORUS, tileState.getPhosphorus()))
                                        .appendSibling(new StringTextComponent(" - ").applyTextStyle(TextFormatting.GRAY))
                                        .appendSibling(PlantMacronutrient.getTextComponentForNutrient(PlantMacronutrient.POTASSIUM, tileState.getPotassium()))
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
