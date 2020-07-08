package com.creativechasm.environment.api.soil;

public enum PlantNutrient {
    /**
     * + healthy leaf and foliage growth<br>
     * + rapid growth
     */
    NITROGEN('N', pH -> {
        if (pH <= 4f) return 0f;
        if (pH <= 6f) return 0.5f * pH - 2f;
        if (pH <= 8f) return 1f;
        if (pH <= 10f) return -0.5f * pH + 5f;
        return 0f;
    }),
    /**
     * + strong root growth<br>
     * + better flower & fruit development<br>
     * + larger seed size
     */
    PHOSPHORUS('P', pH -> {
        if (pH <= 4.25f) return 0f;
        if (pH <= 6f) return 0.286f * pH - 1.21f;
        if (pH <= 6.5f) return pH - 5.5f;
        if (pH <= 7.5f) return 1f;
        if (pH <= 8.5f) return -0.75f * pH + 6.33f;
        if (pH <= 8.75f) return 3f * pH - 25.3f;
        return 1f;
    }),
    /**
     * + overall plant health and growth
     * + disease resistance
     * + better seed quality
     */
    POTASSIUM('K', pH -> {
        if (pH <= 4.5f) return 0f;
        if (pH <= 6f) return 0.667f * pH -3f;
        return 1f;
    });

    public final char symbol;
    private final IFunctionX syntheticFunction;
    PlantNutrient(char symbol, IFunctionX func) {
        this.symbol = symbol;
        this.syntheticFunction = func;
    }

    /**
     * Uses influence of soil pH on plant nutrient availability to calculate percentage of usable nutrients in the soil for the plant
     * @param soilPH pH value of the soil
     * @return nutrient availability multiplier
     */
    public float getAvailabilityInSoilForPlant(float soilPH) {
        return syntheticFunction.f(soilPH);
    }

    interface IFunctionX {
        float f(float x); // y = f(x)
    }
}