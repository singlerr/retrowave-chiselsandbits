package io.github.singlerr.retrowave.fabric;

import io.github.singlerr.ModBootstrap;
import net.fabricmc.api.ModInitializer;

public final class FabricBootstrap implements ModInitializer {
    @Override
    public void onInitialize() {

        ModBootstrap.init();
    }
}
