package com.creativechasm.cropcultivation.api.world;

public enum LightSource {
    /**
     * block can see the sun (sky)
     */
    DIRECT_SUN_LIGHT,
    /**
     * block can see the sun (sky) through foliage, glass, water...
     */
    INDIRECT_SUN_LIGHT,
    /**
     * block can see the moon (sky)
     */
    DIRECT_MOON_LIGHT,
    /**
     * block can see the moon (sky) through foliage, glass, water...
     */
    INDIRECT_MOON_LIGHT,
    /**
     * block can't see the sky ("artificial" light)
     */
    NO_SKY_LIGHT,
    SKY_LIGHT,
    ANY_LIGHT,
    NONE
}
