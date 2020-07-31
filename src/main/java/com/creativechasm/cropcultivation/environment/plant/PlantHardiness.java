package com.creativechasm.cropcultivation.environment.plant;

/**
 * plant's ability to tolerate
 */
public enum PlantHardiness {
    FROST, COLD,
    /**
     * plant doesn’t tolerate cold, killed by freezing
     */
    TENDER,
    HEAT, DROUGHT, FLOODING //, WIND
}
