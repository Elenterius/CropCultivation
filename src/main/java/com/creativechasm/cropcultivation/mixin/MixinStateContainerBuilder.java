package com.creativechasm.cropcultivation.mixin;

import com.creativechasm.cropcultivation.optionaldependency.OptionalRegistry;
import com.creativechasm.cropcultivation.util.BlockPropertyUtil;
import net.minecraft.block.CropsBlock;
import net.minecraft.block.StemBlock;
import net.minecraft.state.IProperty;
import net.minecraft.state.IStateHolder;
import net.minecraft.state.StateContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StateContainer.Builder.class)
public abstract class MixinStateContainerBuilder<O, S extends IStateHolder<S>>
{
    @Shadow public abstract StateContainer.Builder<O, S> add(IProperty<?>... propertiesIn);

    @Inject(method = "<init>", at = @At("TAIL"))
    protected void init(O owner, CallbackInfo ci) {
        if (owner instanceof CropsBlock || owner instanceof StemBlock || OptionalRegistry.isSimpleFarmingCrop(owner)) {
            //we do this because crop block subclasses tend to override the fillStateContainer() method (i.e. beetroot) without calling super
            add(BlockPropertyUtil.YIELD_MODIFIER);
            add(BlockPropertyUtil.MOISTURE_TOLERANCE);
            add(BlockPropertyUtil.TEMPERATURE_TOLERANCE);
        }
    }
}
