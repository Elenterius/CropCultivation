package com.creativechasm.environment.mixin;


@Deprecated
public abstract class MixinCrops {

//    @Mixin(CropsBlock.class)
//    public static abstract class MixinCropsBlock extends Block implements ISeedItemProvider
//    {
//
//        public MixinCropsBlock(Properties properties) {
//            super(properties);
//        }
//
//        @Shadow protected abstract IItemProvider getSeedsItem();
//
//        @Override
//        public Item getSeedItem() {
//            return getSeedsItem().asItem();
//        }
//
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
