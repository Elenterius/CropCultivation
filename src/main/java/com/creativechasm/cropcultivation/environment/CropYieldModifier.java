package com.creativechasm.cropcultivation.environment;

import com.creativechasm.cropcultivation.CropCultivationMod;
import com.creativechasm.cropcultivation.block.SoilBlock;
import com.creativechasm.cropcultivation.block.SoilStateTileEntity;
import com.creativechasm.cropcultivation.util.BlockPropertyUtil;
import com.creativechasm.cropcultivation.util.MiscUtil;
import com.google.gson.JsonObject;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropsBlock;
import net.minecraft.entity.Entity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
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

public class CropYieldModifier extends LootModifier
{
    public CropYieldModifier(ILootCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Nonnull
    @Override
    protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {

        CropCultivationMod.LOGGER.debug(MarkerManager.getMarker("CropYieldModifier"), "in: " + generatedLoot.toString());
        BlockState state = context.get(LootParameters.BLOCK_STATE);
        BlockPos pos = context.get(LootParameters.POSITION);

        ItemStack toolStack = context.get(LootParameters.TOOL);
        Entity entity = context.get(LootParameters.THIS_ENTITY);
//            float luck = context.getLuck(); //applies only to loot from chests and fishing loot, hmm...
        CropCultivationMod.LOGGER.debug(MarkerManager.getMarker("CropYieldModifier"), String.format("tool: %s, fortuneModifier: %s", toolStack, MiscUtil.getFortuneLevel(entity)));

        if (state != null && pos != null) {
            World world = context.getWorld();
            Map<Item, Integer> lootCountMap = new HashMap<>();

            generatedLoot.forEach(stack -> {
                int count = lootCountMap.computeIfAbsent(stack.getItem(), item -> 0);
                lootCountMap.put(stack.getItem(), count + stack.getCount());
            });

            TileEntity tileEntity = world.getTileEntity(pos.down());
            if (lootCountMap.size() > 0 && tileEntity instanceof SoilStateTileEntity) {
                SoilStateTileEntity soil = (SoilStateTileEntity) tileEntity;
                float yieldMultiplier = soil.getCropYieldAveraged(BlockPropertyUtil.getAge(state)); //get crop yield averaged by crop age
                float yieldModifier = BlockPropertyUtil.getYieldModifier(state);

                lootCountMap.forEach((item, count) -> {
                    int yieldAmount = Math.max(1, Math.round((count + yieldModifier) * yieldMultiplier));
                    CropCultivationMod.LOGGER.debug(MarkerManager.getMarker("CropYieldModifier"), String.format("item: %s, count: %s, modifier: %s, multiplier: %s, yield: %s", item, count, yieldModifier, yieldMultiplier, yieldAmount));
                    CropUtil.modifyGeneratedLoot(generatedLoot, item, count, yieldAmount, context.getRandom());
                });
            }

            generatedLoot.forEach(stack -> {
                Item item = stack.getItem();
                if (item instanceof BlockItem && ((BlockItem) item).getBlock() instanceof CropsBlock) {
                    CompoundNBT propertiesTag = stack.getOrCreateChildTag("BlockStateTag");
                    propertiesTag.putInt(BlockPropertyUtil.YIELD_MODIFIER.getName(), state.get(BlockPropertyUtil.YIELD_MODIFIER));
                    propertiesTag.putInt(BlockPropertyUtil.MOISTURE_TOLERANCE.getName(), state.get(BlockPropertyUtil.MOISTURE_TOLERANCE));
                    propertiesTag.putInt(BlockPropertyUtil.TEMPERATURE_TOLERANCE.getName(), state.get(BlockPropertyUtil.TEMPERATURE_TOLERANCE));
                }
            });
        }

        CropCultivationMod.LOGGER.debug(MarkerManager.getMarker("CropYieldModifier"), "out: " + generatedLoot.toString());
        return generatedLoot;
    }

    private static class LootCondition implements ILootCondition
    {
        @Override
        public boolean test(LootContext context) {
            BlockState state = context.get(LootParameters.BLOCK_STATE);
            BlockPos pos = context.get(LootParameters.POSITION);
            if (state != null && pos != null) {
                return state.getBlock() instanceof CropsBlock && context.getWorld().getBlockState(pos.down()).getBlock() instanceof SoilBlock;
            }
            return false;
        }
    }

    public static class Serializer extends GlobalLootModifierSerializer<CropYieldModifier>
    {
        @Override
        public CropYieldModifier read(ResourceLocation name, JsonObject object, ILootCondition[] conditionsIn) {
            //we build our own conditions array
            ILootCondition[] conditions = new ILootCondition[]{new LootCondition()};
            return new CropYieldModifier(conditions);
        }
    }
}
