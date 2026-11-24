package com.hungershield;

import net.fabricmc.api.ModInitializer;

public class HungerShield implements ModInitializer {
    public static final String MOD_ID = "hunger_shield";

    @Override
    public void onInitialize() {
        // All behavior is implemented with mixins so the mod has no hard
        // compile-time dependency on Fabric API.
    }
}
