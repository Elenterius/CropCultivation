package com.creativechasm.environment.common.item;

import com.creativechasm.environment.api.block.SoilBlock;
import com.creativechasm.environment.api.block.SoilStateTileEntity;
import com.creativechasm.environment.api.soil.SoilPH;
import com.creativechasm.environment.api.soil.SoilTexture;
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
import net.minecraft.util.StringUtils;
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
public class SoilTestKitItem extends Item {

    public SoilTestKitItem(Properties properties) {
        super(properties);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (stack.hasTag()) {
            CompoundNBT nbtTag = stack.getTag();

            tooltip.add((new StringTextComponent("")));
            tooltip.add((new TranslationTextComponent("soilKit.results")));

            //noinspection ConstantConditions
            String soilTexture = nbtTag.getString("texture");
            if (!StringUtils.isNullOrEmpty(soilTexture)) {
                tooltip.add((new TranslationTextComponent("soilKit.texture", soilTexture)).applyTextStyle(TextFormatting.GRAY));
            }

            int moisture = Math.round((nbtTag.getInt("moisture") / 9f) * 100);
            tooltip.add((new TranslationTextComponent("soilKit.moisture", moisture + "%")).applyTextStyle(TextFormatting.GRAY));
            int organic = (int) (nbtTag.getInt("organic") / 4f * 100);
            tooltip.add((new TranslationTextComponent("soilKit.organic", organic + "%")).applyTextStyle(TextFormatting.GRAY));

            float pH = nbtTag.getFloat("pH");
            tooltip.add(getTextComponentForPH(pH, String.format("pH: %.1f (%s)", pH, SoilPH.fromPH(pH).name())));

            tooltip.add(
                    getTextComponentForNutrient("N: ", nbtTag.getInt("N"), TextFormatting.LIGHT_PURPLE, TextFormatting.DARK_PURPLE)
                            .appendSibling(new StringTextComponent(" - ").applyTextStyle(TextFormatting.GRAY))
                            .appendSibling(getTextComponentForNutrient("P: ", nbtTag.getInt("P"), TextFormatting.BLUE, TextFormatting.DARK_BLUE))
                            .appendSibling(new StringTextComponent(" - ").applyTextStyle(TextFormatting.GRAY))
                            .appendSibling(getTextComponentForNutrient("K: ", nbtTag.getInt("K"), TextFormatting.YELLOW, TextFormatting.GOLD))
            );
        }
    }

    private ITextComponent getTextComponentForNutrient(String symbol, int nutrientAmount, TextFormatting lightColor, TextFormatting darkColor) {
        return new StringTextComponent(symbol + nutrientAmount).applyTextStyle(nutrientAmount >= 5 ? darkColor : nutrientAmount > 0 ? lightColor : TextFormatting.WHITE);
    }

    private ITextComponent getTextComponentForPH(float pH, String str) {
        TextFormatting color = TextFormatting.GREEN;
        if (pH <= 4f) color = TextFormatting.DARK_RED;
        if (pH < 5.5f) color = TextFormatting.RED;
        if (pH < 7.5f) color = TextFormatting.GOLD;
        if (pH > 7.5f) color = TextFormatting.DARK_GREEN;
        if (pH > 8f) color = TextFormatting.DARK_AQUA;
        if (pH >= 9f) color = TextFormatting.BLUE;
        if (pH > 11.5f) color = TextFormatting.DARK_BLUE;
        return new StringTextComponent(str).applyTextStyle(color);
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
                    nbtTag.putInt("organic", blockState.get(SoilBlock.ORGANIC_MATTER));
                    nbtTag.putInt("moisture", blockState.get(SoilBlock.MOISTURE));
                    SoilTexture texture = ((SoilBlock) blockState.getBlock()).soilTexture;
                    nbtTag.putString("texture", texture.name() + " (" + texture.getDrainageType().name() + ")");

                    PlayerEntity player = context.getPlayer();
                    if (player instanceof ServerPlayerEntity) {
                        player.sendStatusMessage(
                                getTextComponentForPH(tileState.getPH(), String.format("pH: %.1f", tileState.getPH()))
                                        .appendSibling(new StringTextComponent(" - ").applyTextStyle(TextFormatting.GRAY))
                                        .appendSibling(getTextComponentForNutrient("N: ", tileState.getNitrogen(), TextFormatting.LIGHT_PURPLE, TextFormatting.DARK_PURPLE))
                                        .appendSibling(new StringTextComponent(" - ").applyTextStyle(TextFormatting.GRAY))
                                        .appendSibling(getTextComponentForNutrient("P: ", tileState.getPhosphorus(), TextFormatting.BLUE, TextFormatting.DARK_BLUE))
                                        .appendSibling(new StringTextComponent(" - ").applyTextStyle(TextFormatting.GRAY))
                                        .appendSibling(getTextComponentForNutrient("K: ", tileState.getPotassium(), TextFormatting.YELLOW, TextFormatting.GOLD))
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
