package net.ess3.api.events;

import net.ess3.api.IUser;
import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;

/**
 * This handles common boilerplate for events for changes in state that are boolean (true/false).
 */
public abstract class StatusChangeEvent extends StateChangeEvent implements Cancellable {
    private final boolean newValue;

    public StatusChangeEvent(final IUser affected, final IUser controller, final boolean value) {
        this(!Bukkit.getServer().isPrimaryThread(), affected, controller, value);
    }

    public StatusChangeEvent(final boolean isAsync, final IUser affected, final IUser controller, final boolean value) {
        super(isAsync, affected, controller);
        this.newValue = value;
    }

    public boolean getValue() {
        return newValue;
    }
}
