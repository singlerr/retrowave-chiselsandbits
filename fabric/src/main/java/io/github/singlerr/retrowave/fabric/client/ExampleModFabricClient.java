package io.github.singlerr.retrowave.fabric.client;

import fuzs.forgeconfigapiport.fabric.api.forge.v4.ForgeConfigRegistry;
import mod.chiselsandbits.core.ChiselsAndBits;
import net.fabricmc.api.ClientModInitializer;
import net.minecraftforge.fml.config.ModConfig;

public final class ExampleModFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ForgeConfigRegistry.INSTANCE.register(
                ChiselsAndBits.MODID,
                ModConfig.Type.CLIENT,
                ChiselsAndBits.getConfig().getClientConfigSpec());
    }
}
