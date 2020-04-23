package net.ess3.api.events;

import net.ess3.api.IUser;
import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;


/**
 * This handles common boilerplate for events for changes in state that are boolean (true/false).
 */
public abstract class StatusChangeEvent extends StateChangeEvent implements Cancellable {
    private boolean newValue;

    public StatusChangeEvent(IUser affected, IUser controller, boolean value) {
        this(!Bukkit.getServer().isPrimaryThread(), affected, controller, value);
    }

    public StatusChangeEvent(boolean isAsync, IUser affected, IUser controller, boolean value) {
        super(isAsync, affected, controller);
        this.newValue = value;
    }

    public boolean getValue() {
        return newValue;
    }
}
