package io.github.singlerr.retrowave.fabric;

import fuzs.forgeconfigapiport.fabric.api.forge.v4.ForgeConfigRegistry;
import mod.chiselsandbits.core.ChiselsAndBits;
import net.fabricmc.api.ModInitializer;
import net.minecraftforge.fml.config.ModConfig;

public final class FabricBootstrap implements ModInitializer {
    @Override
    public void onInitialize() {
        ForgeConfigRegistry.INSTANCE.register(
                ChiselsAndBits.MODID,
                ModConfig.Type.COMMON,
                ChiselsAndBits.getConfig().getCommonConfigSpec());
    }
}
