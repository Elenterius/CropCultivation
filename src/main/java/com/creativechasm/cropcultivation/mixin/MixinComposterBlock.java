package com.creativechasm.cropcultivation.mixin;

import com.creativechasm.cropcultivation.init.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ComposterBlock;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.IntegerProperty;
import net.minecraft.util.Direction;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nonnull;
import java.util.Random;

/**
 * Composter changes:<br>
 * - nerf speed<br>
 * - return compost instead of bone meal
 */
public abstract class MixinComposterBlock
{
    @Mixin(ComposterBlock.class)
    public static abstract class Composter extends Block
    {
        @Shadow @Final public static IntegerProperty LEVEL;

        public Composter(Properties properties) {
            super(properties);
        }

        @Redirect(method = "onBlockActivated", at = @At(value = "NEW", target = "net/minecraft/item/ItemStack"))
        protected ItemStack redirectSpawnedItem(IItemProvider item) {
            return new ItemStack(ModItems.COMPOST, 6); //replaces bone meal output
        }

        @Redirect(method = "createInventory", at = @At(value = "NEW", target = "net/minecraft/item/ItemStack"))
        protected ItemStack redirectInventoryItem(IItemProvider item) {
            return new ItemStack(ModItems.COMPOST, 6); //replaces bone meal output
        }

        @ModifyArg(
                method = "onBlockAdded",
                at = @At(value = "INVOKE", target = "Lnet/minecraft/world/ITickList;scheduleTick(Lnet/minecraft/util/math/BlockPos;Ljava/lang/Object;I)V"),
                index = 2
        )
        protected int adjustOnBlockAddedTicks(int ticks) {
            return ticks * 6; //increases time needed to convert material to compost
        }

        @ModifyArg(
                method = "addItem",
                at = @At(value = "INVOKE", target = "Lnet/minecraft/world/ITickList;scheduleTick(Lnet/minecraft/util/math/BlockPos;Ljava/lang/Object;I)V"),
                index = 2
        )
        private static int adjustAddItemTicks(int ticks) {
            return ticks * 6; //increases time needed to convert material to compost
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public void animateTick(@Nonnull BlockState stateIn, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull Random rand) {
            if (rand.nextInt(5) == 0) {
                if (stateIn.get(LEVEL) > 2) {
                    BlockState blockstate = worldIn.getBlockState(pos);
                    double yOffset = blockstate.getShape(worldIn, pos).max(Direction.Axis.Y, 0.5D, 0.5D) + 0.03125D;
                    for (int i = 0; i < rand.nextInt(stateIn.get(LEVEL)) + 2; ++i) {
                        worldIn.addParticle(ParticleTypes.ENTITY_EFFECT, pos.getX() + 0.13125F + 0.7375F * rand.nextFloat(), pos.getY() + yOffset + (1d - yOffset), pos.getZ() + 0.13125F + 0.7375F * rand.nextFloat(), 1.8f, 1.8f, 1.8f);
                    }
                }
            }
        }
    }

    @Mixin(targets = "net.minecraft.block.ComposterBlock$FullInventory")
    public static abstract class FullInventory extends Inventory implements ISidedInventory
    {
        @Redirect(method = "canExtractItem(ILnet/minecraft/item/ItemStack;Lnet/minecraft/util/Direction;)Z", at = @At(value = "FIELD", target = "Lnet/minecraft/item/Items;BONE_MEAL:Lnet/minecraft/item/Item;", opcode = Opcodes.GETSTATIC))
        protected Item redirectCanExtractItem() {
            return ModItems.COMPOST; //replaces bone meal
        }

        @Inject(method = "getInventoryStackLimit()I", at = @At("HEAD"), cancellable = true)
        protected void onGetInventoryStackLimit(CallbackInfoReturnable<Integer> cir) {
            cir.setReturnValue(6);
        }

        @Override
        @Nonnull
        public ItemStack decrStackSize(int index, int count) {
            return super.decrStackSize(index, 6);
        }
    }
}
