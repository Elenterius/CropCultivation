package com.creativechasm.cropcultivation.mixin;

import com.creativechasm.cropcultivation.CropCultivationMod;
import com.creativechasm.cropcultivation.item.ModItems;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.MarkerManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nonnull;
import java.util.Optional;

public abstract class MixinStonecutterContainer
{
    @Mixin(targets = "net.minecraft.inventory.container.StonecutterContainer$2")
    public static abstract class OutputSlot extends Slot
    {
        //        @Shadow(aliases = {"field_216955_a", "this$0"}) @Final private StonecutterContainer field_216955_a;
        @Shadow(aliases = {"field_216956_b", "val$worldPosCallableIn"}) @Final private IWorldPosCallable field_216956_b;

        public OutputSlot(IInventory inventoryIn, int index, int xPosition, int yPosition) {
            super(inventoryIn, index, xPosition, yPosition);
        }

        @Inject(method = "onTake(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/ItemStack;)Lnet/minecraft/item/ItemStack;", at = @At("HEAD"))
        protected void injectOnTake(PlayerEntity player, ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
            //normally the stack is the crafted item, but when using shift click to craft several items at once this is an empty stack
            onItemCrafted(stack);
        }

        @Override
        protected void onCrafting(@Nonnull ItemStack stack, int amount) {
            super.onCrafting(stack, amount);
            onItemCrafted(stack);
        }

        protected void onItemCrafted(@Nonnull ItemStack stack) {
            if (!stack.isEmpty()) {
                //guard against mods adding non-rock recipes
                if (stack.getItem() instanceof BlockItem && ((BlockItem) stack.getItem()).getBlock().getDefaultState().getMaterial() != Material.ROCK) return;

                //we only want rock that contains trace elements, filters out Stone/Cobblestone
                String registryName = Optional.ofNullable(stack.getItem().getRegistryName()).map(ResourceLocation::getPath).orElse("");
                if (!registryName.contains("granite") || !registryName.contains("diorite") || !registryName.contains("andesite")) return;

                //forge is missing a crafting event for the stonecutter :(
                //could fire custom event, but we don't need it
                //CraftingHandler.firePlayerStoneCutEvent(player, stack.copy(), ((CraftingHandler.IWorldPosCallableProvider) outerClassRef).getWorldPosCallable());

                field_216956_b.consume((world, pos) -> {
                    if (!world.isRemote && world.rand.nextFloat() < 0.15f) {
                        double d0 = world.rand.nextFloat() * 0.5F + 0.15F;
                        double d1 = world.rand.nextFloat() * 0.5F + 0.5D;
                        double d2 = world.rand.nextFloat() * 0.5F + 0.15F;

                        ItemStack byproductStack = new ItemStack(ModItems.LIME_DUST, world.rand.nextInt(stack.getCount()) + 1);
                        CropCultivationMod.LOGGER.debug(MarkerManager.getMarker("StoneCutter"), "dropping: " + byproductStack);
                        ItemEntity itementity = new ItemEntity(world, pos.getX() + d0, pos.getY() + d1, pos.getZ() + d2, byproductStack);
                        itementity.setDefaultPickupDelay();
                        world.addEntity(itementity);
                    }
                });
            }
        }
    }
}
