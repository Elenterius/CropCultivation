package com.creativechasm.cropcultivation.optionaldependency;

import com.creativechasm.cropcultivation.CropCultivationMod;
import com.creativechasm.cropcultivation.block.SoilBlock;
import com.creativechasm.cropcultivation.block.SoilStateTileEntity;
import com.creativechasm.cropcultivation.environment.CropUtil;
import com.creativechasm.cropcultivation.util.BlockPropertyUtil;
import com.creativechasm.cropcultivation.util.MiscUtil;
import com.google.gson.JsonObject;
import enemeez.simplefarming.block.growable.DoubleCropBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import org.apache.logging.log4j.MarkerManager;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleFarmingHandler implements IOptionalModHandler
{
    @Override
    public void onSetup() {
        CropCultivationMod.LOGGER.info(MarkerManager.getMarker("ModCompat"), "setting up mod compatibility for <Simple Farming>...");
        CropCultivationMod.LOGGER.warn(MarkerManager.getMarker("ModCompat"), "The growing behavior of all compatible <Simple Farming> crops will be modified!");
    }

    public static class DoubleCropYieldModifier extends LootModifier
    {
        public DoubleCropYieldModifier(ILootCondition[] conditionsIn) {
            super(conditionsIn);
        }

        @Nonnull
        @Override
        protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {

            CropCultivationMod.LOGGER.debug(MarkerManager.getMarker("DoubleCropYieldModifier"), "loot: " + generatedLoot.toString());
            BlockState state = context.get(LootParameters.BLOCK_STATE);
            BlockPos pos = context.get(LootParameters.POSITION);

            ItemStack toolStack = context.get(LootParameters.TOOL);
            Entity entity = context.get(LootParameters.THIS_ENTITY);
            CropCultivationMod.LOGGER.debug(MarkerManager.getMarker("DoubleCropYieldModifier"), String.format("tool: %s, fortuneModifier: %s", toolStack, MiscUtil.getFortuneLevel(entity)));

            if (state != null && pos != null) {
                World world = context.getWorld();
                Map<Item, Integer> lootCountMap = new HashMap<>();

                generatedLoot.forEach(stack -> {
                    int count = lootCountMap.computeIfAbsent(stack.getItem(), item -> 0);
                    lootCountMap.put(stack.getItem(), count + stack.getCount());
                });

                BlockPos soilPos = state.get(DoubleCropBlock.HALF) == DoubleBlockHalf.UPPER ? pos.down(2) : pos.down();
                TileEntity tileEntity = world.getTileEntity(soilPos);
                if (lootCountMap.size() > 0 && tileEntity instanceof SoilStateTileEntity) {
                    SoilStateTileEntity soil = (SoilStateTileEntity) tileEntity;
                    float yieldMultiplier = soil.getCropYieldAveraged(BlockPropertyUtil.getAge(state)); //get crop yield averaged by crop age

                    lootCountMap.forEach((item, count) -> {
                        int yieldAmount = Math.max(1, Math.round(count * yieldMultiplier));
                        CropCultivationMod.LOGGER.debug(MarkerManager.getMarker("DoubleCropYieldModifier"), String.format("item: %s, count: %s, multiplier: %s, yield: %s", item, count, yieldMultiplier, yieldAmount));
                        CropUtil.modifyGeneratedLoot(generatedLoot, item, count, yieldAmount, context.getRandom());
                    });
                }
            }

            CropCultivationMod.LOGGER.debug(MarkerManager.getMarker("DoubleCropYieldModifier"), "out: " + generatedLoot.toString());
            return generatedLoot;
        }

        public static class LootCondition implements ILootCondition
        {
            @Override
            public boolean test(LootContext context) {
                BlockState state = context.get(LootParameters.BLOCK_STATE);
                BlockPos pos = context.get(LootParameters.POSITION);
                if (state != null && pos != null) {
                    return state.getBlock() instanceof DoubleCropBlock && (context.getWorld().getBlockState(pos.down()).getBlock() instanceof SoilBlock || context.getWorld().getBlockState(pos.down(2)).getBlock() instanceof SoilBlock);
                }
                return false;
            }
        }

        static class Serializer extends GlobalLootModifierSerializer<DoubleCropYieldModifier>
        {
            @Override
            public DoubleCropYieldModifier read(ResourceLocation name, JsonObject object, ILootCondition[] conditionsIn) {
                //we build our own conditions array
                ILootCondition[] conditions = new ILootCondition[]{new DoubleCropYieldModifier.LootCondition()};
                return new DoubleCropYieldModifier(conditions);
            }
        }
    }
}
