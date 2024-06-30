package mod.chiselsandbits.neoforge;

import fuzs.forgeconfigapiport.neoforge.api.forge.v4.ForgeConfigRegistry;
import mod.chiselsandbits.core.ChiselsAndBits;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLConstructModEvent;

@Mod(ChiselsAndBits.MODID)
public final class NeoForgeBootstrap {

    public NeoForgeBootstrap(IEventBus eventBus) {
        eventBus.addListener(this::onConstruct);
    }

    public void onConstruct(FMLConstructModEvent event) {
        ForgeConfigRegistry.INSTANCE.register(
                ChiselsAndBits.MODID,
                ModConfig.Type.COMMON,
                ChiselsAndBits.getConfig().getCommonConfigSpec());
        ForgeConfigRegistry.INSTANCE.register(
                ChiselsAndBits.MODID,
                ModConfig.Type.CLIENT,
                ChiselsAndBits.getConfig().getClientConfigSpec());
        ForgeConfigRegistry.INSTANCE.register(
                ChiselsAndBits.MODID,
                ModConfig.Type.SERVER,
                ChiselsAndBits.getConfig().getServerConfigSpec());
    }
}
