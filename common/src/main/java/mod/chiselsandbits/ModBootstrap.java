package mod.chiselsandbits;

import mod.chiselsandbits.registry.ModBlockRegistries;
import mod.chiselsandbits.registry.ModItemRegistries;

public final class ModBootstrap {

    public static final String MOD_ID = "assets/chiselsandbits";

    public static void init() {

        ModBlockRegistries.initialize();
        ModItemRegistries.initialize();
    }
}
