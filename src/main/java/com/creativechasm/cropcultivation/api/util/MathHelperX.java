package com.creativechasm.cropcultivation.api.util;

import net.minecraft.util.math.MathHelper;

public class MathHelperX extends MathHelper {

    public static float roundTo1Decimal(float value) {
        return Math.round(value * 10) / 10f;
    }

    public static float roundTo2Decimals(float value) {
        return Math.round(value * 100) / 100f;
    }

    public static double truncateTo2Decimals(float value) {
        return Math.floor(value * 100) / 100d;
    }
}
