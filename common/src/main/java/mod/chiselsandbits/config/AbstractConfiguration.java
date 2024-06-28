package mod.chiselsandbits.config;

import lombok.AccessLevel;
import lombok.Getter;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class AbstractConfiguration {

    private final ConfigBuilder configBuilder;

    @Getter(value = AccessLevel.PROTECTED)
    private final ConfigEntryBuilder entryBuilder;

    protected AbstractConfiguration(Component title) {
        this.configBuilder = ConfigBuilder.create().setTitle(title);
        this.entryBuilder = this.configBuilder.entryBuilder();
    }

    protected ConfigCategory createCategory(Component name) {
        return this.configBuilder.getOrCreateCategory(name);
    }

    public Screen buildScreen() {
        return this.configBuilder.build();
    }
}
