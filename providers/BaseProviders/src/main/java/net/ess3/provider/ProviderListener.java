package net.ess3.provider;

import org.bukkit.event.Event;
import org.bukkit.event.Listener;

import java.util.function.Consumer;

public abstract class ProviderListener implements Provider, Listener {
    protected final Consumer<Event> function;

    public ProviderListener(final Consumer<Event> function) {
        this.function = function;
    }
}
