package com.creativechasm.environment.mixin;

/**
 * make sure crops can grow on any block extending FarmlandBlock<br>
 * We don't needed this at the moment, generally mods will use the instanceof operator to test for blocks of the FarmlandBlock type
 */
@Deprecated
public abstract class MixinCrops {

//    @Mixin(CropsBlock.class)
//    public static class MixinCropsBlock {
//        @Inject(method = "isValidGround", at = @At("HEAD"), cancellable = true)
//        protected void onIsValidGround(BlockState state, IBlockReader worldIn, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
//            cir.setReturnValue(state.getBlock() instanceof FarmlandBlock);
//        }
//    }
//
//    @Mixin(StemBlock.class)
//    public static class MixinStemBlock {
//        @Inject(method = "isValidGround", at = @At("HEAD"), cancellable = true)
//        protected void onIsValidGround(BlockState state, IBlockReader worldIn, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
//            cir.setReturnValue(state.getBlock() instanceof FarmlandBlock);
//        }
//    }
//
//    @Mixin(AttachedStemBlock.class)
//    public static class MixinAttachedStemBlock {
//        @Inject(method = "isValidGround", at = @At("HEAD"), cancellable = true)
//        protected void onIsValidGround(BlockState state, IBlockReader worldIn, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
//            cir.setReturnValue(state.getBlock() instanceof FarmlandBlock);
//        }
//    }
}
