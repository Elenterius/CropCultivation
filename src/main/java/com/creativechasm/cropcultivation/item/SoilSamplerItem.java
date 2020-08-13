package com.creativechasm.cropcultivation.item;

import com.creativechasm.cropcultivation.block.SoilBlock;
import com.creativechasm.cropcultivation.block.SoilStateTileEntity;
import com.creativechasm.cropcultivation.environment.soil.SoilTexture;
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
public class SoilSamplerItem extends DeviceItem
{

    public SoilSamplerItem(Properties properties) {
        super(properties);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (stack.hasTag() && stack.getTag() != null) {
            CompoundNBT nbtTag = stack.getTag();

            String soilTexture = "soil_texture." + nbtTag.getString("texture");
            String drainageType = "soil_drainage." + nbtTag.getString("drainage");
            int moisture = Math.round((nbtTag.getInt("moisture") / 9f) * 100);
            int organic = (int) (nbtTag.getInt("organic") / 4f * 100);

            tooltip.add(new StringTextComponent(""));
            tooltip.add(new TranslationTextComponent("measurement.cropcultivation.desc"));
            tooltip.add(new TranslationTextComponent("measurement.cropcultivation.soil_moisture", moisture + "%").applyTextStyle(TextFormatting.GRAY));
            tooltip.add(new TranslationTextComponent("measurement.cropcultivation.organic_matter", organic + "%").applyTextStyle(TextFormatting.GRAY));
            tooltip.add(new TranslationTextComponent("measurement.cropcultivation.soil_texture", new TranslationTextComponent(soilTexture)).applyTextStyle(TextFormatting.GRAY));
            tooltip.add(new TranslationTextComponent("measurement.cropcultivation.drainage_type", new TranslationTextComponent(drainageType)).applyTextStyle(TextFormatting.GRAY));
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
                    int moisture = blockState.get(SoilBlock.MOISTURE);
                    int organicMatter = blockState.get(SoilBlock.ORGANIC_MATTER);

                    SoilTexture texture = ((SoilBlock) blockState.getBlock()).soilTexture;
                    nbtTag.putInt("moisture", moisture);
                    nbtTag.putInt("organic", organicMatter);
                    nbtTag.putString("texture", texture.name().toLowerCase());
                    nbtTag.putString("drainage", texture.getDrainageType().name().toLowerCase());

                    PlayerEntity player = context.getPlayer();
                    if (player instanceof ServerPlayerEntity) {
                        player.sendStatusMessage(new TranslationTextComponent("measurement.cropcultivation.soil_moisture", Math.round(moisture / 9f * 100) + "%").applyTextStyle(moisture >= 5 ? TextFormatting.AQUA : TextFormatting.WHITE)
                                        .appendSibling(new StringTextComponent(" - ").applyTextStyle(TextFormatting.GRAY))
                                        .appendSibling(new TranslationTextComponent("measurement.cropcultivation.organic_matter", ((int) (organicMatter / 4f * 100)) + "%").applyTextStyle(TextFormatting.GOLD))
                                        .appendSibling(new StringTextComponent(" - ").applyTextStyle(TextFormatting.GRAY))
                                        .appendSibling(new TranslationTextComponent("measurement.cropcultivation.soil_texture", new TranslationTextComponent("soil_texture." + texture.name().toLowerCase())).applyTextStyle(TextFormatting.WHITE))
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
