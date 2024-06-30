package mod.chiselsandbits.config;

import lombok.Getter;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Mod root configuration.
 */
public class Configuration {
    private final ClientConfiguration clientConfig;
    private final ServerConfiguration serverConfig;
    private final CommonConfiguration commonConfig;

    @Getter
    private final ForgeConfigSpec clientConfigSpec;

    @Getter
    private final ForgeConfigSpec commonConfigSpec;

    @Getter
    private final ForgeConfigSpec serverConfigSpec;

    public Configuration() {
        final Pair<ClientConfiguration, ForgeConfigSpec> cli =
                new ForgeConfigSpec.Builder().configure(ClientConfiguration::new);
        final Pair<ServerConfiguration, ForgeConfigSpec> ser =
                new ForgeConfigSpec.Builder().configure(ServerConfiguration::new);
        final Pair<CommonConfiguration, ForgeConfigSpec> com =
                new ForgeConfigSpec.Builder().configure(CommonConfiguration::new);
        clientConfig = cli.getLeft();
        serverConfig = ser.getLeft();
        commonConfig = com.getLeft();

        clientConfigSpec = cli.getRight();
        serverConfigSpec = ser.getRight();
        commonConfigSpec = com.getRight();
    }

    public ClientConfiguration getClient() {
        return clientConfig;
    }

    public ServerConfiguration getServer() {
        return serverConfig;
    }

    public CommonConfiguration getCommon() {
        return commonConfig;
    }
}
