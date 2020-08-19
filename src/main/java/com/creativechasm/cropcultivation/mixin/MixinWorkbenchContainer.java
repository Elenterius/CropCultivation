package com.creativechasm.cropcultivation.mixin;

import com.creativechasm.cropcultivation.util.BlockPropertyUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.RecipeBookContainer;
import net.minecraft.inventory.container.WorkbenchContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import net.minecraftforge.common.Tags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(WorkbenchContainer.class)
public abstract class MixinWorkbenchContainer extends RecipeBookContainer<CraftingInventory>
{
    public MixinWorkbenchContainer(ContainerType<?> type, int id) {
        super(type, id);
    }

    @Inject(
            method = "updateCraftingResult",
            at = @At(value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/item/crafting/ICraftingRecipe;getCraftingResult(Lnet/minecraft/inventory/IInventory;)Lnet/minecraft/item/ItemStack;"),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private static void injectUpdateCraftingResult(int id, World worldIn, PlayerEntity playerIn, CraftingInventory inventoryIn, CraftResultInventory inventoryResult, CallbackInfo ci,
                                                   ServerPlayerEntity serverplayerentity, ItemStack itemstack) {
        if (!itemstack.isEmpty() && Tags.Items.SEEDS.contains(itemstack.getItem())) { //is crafting output a seed?
            for (int i = 0; i < inventoryIn.getSizeInventory(); i++) {
                ItemStack stackInput = inventoryIn.getStackInSlot(i);
                if (Tags.Items.CROPS.contains(stackInput.getItem()) && stackInput.getCount() > 0) { //is crafting input a crop?
                    CompoundNBT nbtTag = stackInput.getTag();
                    if (nbtTag != null && nbtTag.contains("cropcultivation") && nbtTag.contains("BlockStateTag")) { //are there any nbt tags that have to be copied?
                        CompoundNBT inputTag = nbtTag.getCompound("BlockStateTag");
                        CompoundNBT propertiesTag = itemstack.getOrCreateChildTag("BlockStateTag");
                        propertiesTag.putInt(BlockPropertyUtil.YIELD_MODIFIER.getName(), inputTag.getInt(BlockPropertyUtil.YIELD_MODIFIER.getName()));
                        propertiesTag.putInt(BlockPropertyUtil.MOISTURE_TOLERANCE.getName(), inputTag.getInt(BlockPropertyUtil.MOISTURE_TOLERANCE.getName()));
                        propertiesTag.putInt(BlockPropertyUtil.TEMPERATURE_TOLERANCE.getName(), inputTag.getInt(BlockPropertyUtil.TEMPERATURE_TOLERANCE.getName()));
                        assert itemstack.getTag() != null;
                        itemstack.getTag().putBoolean("cropcultivation", nbtTag.getBoolean("cropcultivation"));
                        break;
                    }
                }
            }
        }
    }
}
