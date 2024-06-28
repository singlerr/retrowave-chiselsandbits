package mod.chiselsandbits.config;

import lombok.Getter;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import mod.chiselsandbits.config.utils.ConfigEntryHolder;
import net.minecraft.network.chat.Component;

@Getter
public class CommonConfiguration extends AbstractConfiguration {

    private final ConfigEntryHolder<Boolean> enableHelp;
    private final ConfigEntryHolder<Long> collisionBoxCacheSize;

    protected CommonConfiguration(Component title) {
        super(title);

        this.enableHelp = new ConfigEntryHolder<>(true);
        this.collisionBoxCacheSize = new ConfigEntryHolder<>(10000L);

        ConfigCategory helpCategory = createCategory(Component.translatable("mod.chiselsandbits.config.common.help"));
        helpCategory.addEntry(getEntryBuilder()
                .startBooleanToggle(
                        Component.translatable("mod.chiselsandbits.config.common.help.enabled"), enableHelp.getValue())
                .setTooltip(Component.translatable("mod.chiselsandbits.config.common.help.enabled.comment"))
                .setSaveConsumer(enableHelp)
                .build());

        ConfigCategory performanceCategory =
                createCategory(Component.translatable("mod.chiselsandbits.config.common.performance"));
        performanceCategory.addEntry(getEntryBuilder()
                .startLongField(
                        Component.translatable("mod.chiselsandbits.config.common.performance.collisions.cache.size"),
                        collisionBoxCacheSize.getValue())
                .setTooltip(Component.translatable(
                        "mod.chiselsandbits.config.common.performance.collisions.cache.size.comment"))
                .setSaveConsumer(collisionBoxCacheSize)
                .build());
    }
}
