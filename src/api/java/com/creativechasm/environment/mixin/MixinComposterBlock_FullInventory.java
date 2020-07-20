package com.creativechasm.environment.mixin;

import com.creativechasm.environment.api.item.LibItems;
import net.minecraft.block.ComposterBlock;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nonnull;

@Mixin(ComposterBlock.FullInventory.class) // uses AT to make the nested class public
public abstract class MixinComposterBlock_FullInventory extends Inventory implements ISidedInventory {

    @Redirect(
            method = "canExtractItem",
            at = @At(value = "FIELD", target = "Lnet/minecraft/item/Items;BONE_MEAL:Lnet/minecraft/item/Item;", opcode = Opcodes.GETSTATIC)
    )
    protected Item redirectCanExtractItem() {
        return LibItems.COMPOST; //replaces bone meal
    }

    @Inject(method = "getInventoryStackLimit", at = @At("HEAD"), cancellable = true)
    protected void onGetInventoryStackLimit(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(3);
    }

    @Override
    @Nonnull
    public ItemStack decrStackSize(int index, int count) {
        return super.decrStackSize(index, 3);
    }
}
