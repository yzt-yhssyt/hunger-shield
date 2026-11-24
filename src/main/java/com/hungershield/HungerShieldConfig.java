package com.hungershield;

/**
 * Central tuning values for the hunger shield.
 */
public final class HungerShieldConfig {
    public static final int MAX_FOOD = 1000;
    public static final float MAX_SATURATION = 1000.0F;

    public static final int SLOW_REGEN_THRESHOLD = 200;
    public static final int FAST_REGEN_THRESHOLD = 500;

    public static final float LOW_HEALTH_FRACTION = 0.50F;
    public static final float SHIELD_DAMAGE_MULTIPLIER = 3.0F;

    public static final float EXHAUSTION_MULTIPLIER = 10.0F;
    public static final float MAX_EXHAUSTION = 400.0F;

    private HungerShieldConfig() {
    }
}
