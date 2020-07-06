package com.creativechasm.environment.mixin;

import com.creativechasm.environment.api.item.LibItems;
import net.minecraft.block.ComposterBlock;
import net.minecraft.item.Item;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ComposterBlock.FullInventory.class) // uses AT to make the nested class public
public abstract class MixinComposterBlock_FullInventory {

    @Redirect(
            method = "canExtractItem",
            at = @At(value = "FIELD", target = "Lnet/minecraft/item/Items;BONE_MEAL:Lnet/minecraft/item/Item;", opcode = Opcodes.GETSTATIC)
    )
    public Item redirectCanExtractItem() {
        return LibItems.COMPOST; //replaces bone meal
    }

}
