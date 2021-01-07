package net.ess3.api.events;

import net.ess3.api.IUser;
import org.bukkit.event.HandlerList;

/**
 * This event is currently unused, and is retained for ABI compatibility and potential future implementation.
 */
public class IgnoreStatusChangeEvent extends StatusChangeEvent {
    private static final HandlerList handlers = new HandlerList();

    public IgnoreStatusChangeEvent(final IUser affected, final IUser controller, final boolean value) {
        super(affected, controller, value);
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
