package com.creativechasm.cropcultivation.api.plant;

public enum PlantMacronutrient {
    /**
     * + healthy leaf and foliage growth<br>
     * - high N concentration --> rapid growth (maturity)
     */
    NITROGEN('N', pH -> {
        if (pH <= 4f) return 0f;
        if (pH <= 6f) return 0.5f * pH - 2f;
        if (pH <= 8f) return 1f;
        if (pH < 10f) return -0.5f * pH + 5f;
        return 0f;
    }),
    /**
     * + strong root growth<br>
     * + better flower development<br>
     * + larger fruit/seed size
     * - high P salt concentration --> fertilizer burn
     */
    PHOSPHORUS('P', pH -> {
        if (pH <= 4.25f) return 0f;
        if (pH <= 6f) return 0.286f * pH - 1.21f;
        if (pH <= 6.5f) return pH - 5.5f;
        if (pH <= 7.5f) return 1f;
        if (pH < 8.5f) return -0.75f * pH + 6.33f;
        if (pH <= 8.75f) return 3f * pH - 25.3f;
        return 1f;
    }),
    /**
     * + overall plant health and growth
     * + disease resistance
     * + better fruit/seed quality
     * - high K salt concentration --> fertilizer burn
     */
    POTASSIUM('K', pH -> {
        if (pH <= 4.5f) return 0f;
        if (pH < 6f) return 0.667f * pH -3f;
        return 1f;
    });

    public final char symbol;
    private final IFunctionX syntheticFunction;
    PlantMacronutrient(char symbol, IFunctionX func) {
        this.symbol = symbol;
        this.syntheticFunction = func;
    }

    /**
     * Uses influence of soil pH on plant nutrient availability to calculate percentage of usable nutrients in the soil for the plant
     * @param soilPH pH value of the soil
     * @return nutrient availability percentage (0.0 - 1.0)
     */
    public float getAvailabilityPctInSoilForPlant(float soilPH) {
        return syntheticFunction.f(soilPH);
    }

    interface IFunctionX {
        float f(float x); // y = f(x)
    }
}