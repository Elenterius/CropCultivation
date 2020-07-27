package com.creativechasm.cropcultivation.mixin;

import com.creativechasm.cropcultivation.api.item.ModItems;
import com.creativechasm.cropcultivation.handler.CraftingHandler;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.StonecutterContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IWorldPosCallable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public abstract class MixinStonecutterContainer
{
    @Mixin(StonecutterContainer.class)
    public static abstract class Container implements CraftingHandler.IWorldPosCallableProvider
    {
        @Shadow @Final private IWorldPosCallable worldPosCallable;

        @Override
        public IWorldPosCallable getWorldPosCallable() {
            return worldPosCallable;
        }
    }

    @Mixin(targets = "net.minecraft.inventory.container.StonecutterContainer$2")
    public static abstract class OutputSlot {
        @Shadow(aliases = {"this$0"})
        StonecutterContainer outerClassRef;

        /**
         * @param stack normally is the crafted item, but when using shift click to craft several items at once this is an empty stack, method is still called for each item created
         */
        @Inject(
                method = "onTake",
                at = @At("TAIL")
        )
        protected void injectOnTake(PlayerEntity player, ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
            if (outerClassRef instanceof CraftingHandler.IWorldPosCallableProvider) {
                //CraftingHandler.firePlayerStoneCutEvent(player, stack.copy(), ((CraftingHandler.IWorldPosCallableProvider) outerClassRef).getWorldPosCallable());

                ((CraftingHandler.IWorldPosCallableProvider) outerClassRef).getWorldPosCallable().consume((world, pos) -> {
                    if (!world.isRemote && world.rand.nextFloat() < 0.15f) {
                        double d0 = world.rand.nextFloat() * 0.5F + 0.15F;
                        double d1 = world.rand.nextFloat() * 0.5F + 0.5D;
                        double d2 = world.rand.nextFloat() * 0.5F + 0.15F;
                        ItemEntity itementity = new ItemEntity(world, pos.getX() + d0, pos.getY() + d1, pos.getZ() + d2, new ItemStack(ModItems.LIME_DUST, world.rand.nextInt(2) + 1));
                        itementity.setDefaultPickupDelay();
                        world.addEntity(itementity);
                    }
                });
            }
        }
    }
}
