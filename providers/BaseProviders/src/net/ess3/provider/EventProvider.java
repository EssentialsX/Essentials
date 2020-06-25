package net.ess3.provider;

import org.bukkit.event.Event;
import org.bukkit.event.Listener;

import java.util.function.Consumer;

public abstract class EventProvider implements Provider, Listener {
    protected Consumer<Event> function;

    public EventProvider(Consumer<Event> function) {
        this.function = function;
    }
}
