package mod.chiselsandbits.config.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;

public final class ConfigEntryHolder<T> implements Consumer<T> {

    @NotNull
    private T value;

    private final List<Consumer<T>> listeners;

    public ConfigEntryHolder(@NotNull T defaultValue) {
        this.value = defaultValue;
        this.listeners = new ArrayList<>();
    }

    public void registerListener(Consumer<T> listener) {
        this.listeners.add(listener);
    }

    @NotNull
    public T getValue() {
        return value;
    }

    @Override
    public void accept(T t) {
        this.value = t;
        this.listeners.forEach(l -> l.accept(t));
    }
}
