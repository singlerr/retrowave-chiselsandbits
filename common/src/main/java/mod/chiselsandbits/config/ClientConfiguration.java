package mod.chiselsandbits.config;

import mod.chiselsandbits.config.utils.ConfigEntryHolder;
import net.minecraft.network.chat.Component;

public final class ClientConfiguration extends CommonConfiguration{

    private final ConfigEntryHolder<Boolean> enableRightClickModeChange;
    private final ConfigEntryHolder<Boolean> invertBitBagFullness;
    private final ConfigEntryHolder<Boolean> enableToolbarIcons;
    private final ConfigEntryHolder<Boolean> perChiselMode;
    private final ConfigEntryHolder<Boolean> chatModeNotification;
    private final ConfigEntryHolder<Boolean> itemNameModeDisplay;
    private final ConfigEntryHolder<Boolean> addBrokenBlocksToCreativeClipboard;

    public ClientConfiguration(Component title) {
        super(title);
    }
}
