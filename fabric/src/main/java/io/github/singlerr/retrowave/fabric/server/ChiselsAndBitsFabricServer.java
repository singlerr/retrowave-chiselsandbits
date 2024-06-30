package io.github.singlerr.retrowave.fabric.server;

import fuzs.forgeconfigapiport.fabric.api.forge.v4.ForgeConfigRegistry;
import mod.chiselsandbits.core.ChiselsAndBits;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.minecraftforge.fml.config.ModConfig;

public class ChiselsAndBitsFabricServer implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        ForgeConfigRegistry.INSTANCE.register(
                ChiselsAndBits.MODID,
                ModConfig.Type.SERVER,
                ChiselsAndBits.getConfig().getServerConfigSpec());
    }
}
