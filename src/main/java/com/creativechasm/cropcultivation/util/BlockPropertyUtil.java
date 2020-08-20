package com.creativechasm.cropcultivation.util;

import com.creativechasm.cropcultivation.environment.ClimateUtil;
import com.creativechasm.cropcultivation.environment.plant.WeedType;
import com.creativechasm.cropcultivation.environment.soil.SoilMoisture;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropsBlock;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.properties.BlockStateProperties;

import java.util.Optional;

public abstract class BlockPropertyUtil {
    //Soil
    public static final IntegerProperty MOISTURE = IntegerProperty.create("moisture", 0, SoilMoisture.MAX_VALUE);
    public static final IntegerProperty ORGANIC_MATTER = IntegerProperty.create("organic_matter", 0, 4);

    //Plant
    public static final EnumProperty<WeedType> WEED_TYPE = EnumProperty.create("plant", WeedType.class);

    //CROP
    public static final IntegerProperty YIELD_MODIFIER = IntegerProperty.create("yield_modifier", 0, 4);
    public static final IntegerProperty MOISTURE_TOLERANCE = IntegerProperty.create("moisture_tolerance", 0, 2);
    public static final IntegerProperty TEMPERATURE_TOLERANCE = IntegerProperty.create("temperature_tolerance", 0, 4);

    public static float getYieldModifier(BlockState state) {
        return state.getBlock() instanceof CropsBlock ? state.get(YIELD_MODIFIER) - 2f : 0f;
    }

    public static int getMoistureTolerance(BlockState state) {
        return state.getBlock() instanceof CropsBlock ? state.get(MOISTURE_TOLERANCE) : 0;
    }

    public static float getTemperatureTolerance(BlockState state) {
        if (state.getBlock() instanceof CropsBlock) {
            float tolerance = getTemperatureToleranceInCelsius(state.get(TEMPERATURE_TOLERANCE));
            return tolerance == 0f ? 0f : ClimateUtil.convertTemperatureCelsiusToMC(tolerance);
        }
        return 0f;
    }

    public static float getTemperatureToleranceInCelsius(int rawValue) {
        return (rawValue / 4f) * 10f;
    }

    public static final ImmutableMap<IntegerProperty, Integer> maxAgeMappings;
    static {
        //we can't know if someone might have manipulate the properties, so we search for the max value
        maxAgeMappings = ImmutableMap.<IntegerProperty, Integer>builder()
                .put(BlockStateProperties.AGE_0_1, BlockStateProperties.AGE_0_1.getAllowedValues().stream().max(Integer::compareTo).orElse(1))
                .put(BlockStateProperties.AGE_0_3, BlockStateProperties.AGE_0_3.getAllowedValues().stream().max(Integer::compareTo).orElse(3))
                .put(BlockStateProperties.AGE_0_5, BlockStateProperties.AGE_0_5.getAllowedValues().stream().max(Integer::compareTo).orElse(5))
                .put(BlockStateProperties.AGE_0_7, BlockStateProperties.AGE_0_7.getAllowedValues().stream().max(Integer::compareTo).orElse(7))
                .put(BlockStateProperties.AGE_0_15, BlockStateProperties.AGE_0_15.getAllowedValues().stream().max(Integer::compareTo).orElse(15))
                .put(BlockStateProperties.AGE_0_25, BlockStateProperties.AGE_0_25.getAllowedValues().stream().max(Integer::compareTo).orElse(25))
                .build();
    }

    public static int getMaxAge(IntegerProperty property) {
        if (maxAgeMappings.containsKey(property)) {
            return maxAgeMappings.get(property);
        }
        else {
            //we can't use the last element of the collection since the order of the underlying HashSet is not guaranteed to remain constant
            return property.getAllowedValues().stream().max(Integer::compareTo).orElse(0);
        }
    }

    public static Optional<IntegerProperty> getAgeProperty(BlockState state) {
        if (state.getBlock() instanceof CropsBlock) {
            return Optional.of(((CropsBlock) state.getBlock()).getAgeProperty());
        }
        for (IProperty<?> prop : state.getProperties()) {
            if (prop.getName().equals("age") && prop instanceof IntegerProperty) {
                return Optional.of((IntegerProperty) prop);
            }
        }
        return Optional.empty();
    }

    public static int getAge(BlockState state) {
        return getAgeProperty(state).map(state::get).orElse(0);
    }

    public static Optional<int[]> getCurrentAgeAndMaxAge(BlockState state) {
        if (state.getBlock() instanceof CropsBlock) {
            CropsBlock block = (CropsBlock) state.getBlock();
            return Optional.of(new int[]{state.get(block.getAgeProperty()), block.getMaxAge()});
        }

        for (IProperty<?> prop : state.getProperties()) {
            if (prop.getName().equals("age") && prop instanceof IntegerProperty) {
                IntegerProperty ageProperty = (IntegerProperty) prop;
                return Optional.of(new int[]{state.get(ageProperty), getMaxAge(ageProperty)});
            }
        }

        return Optional.empty();
    }
}
