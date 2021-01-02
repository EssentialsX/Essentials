package net.ess3.api.events;

import net.ess3.api.IUser;
import org.bukkit.event.HandlerList;

/**
 * Fired when a player's AFK status changes.
 */
public class AfkStatusChangeEvent extends StatusChangeEvent {
    private static final HandlerList handlers = new HandlerList();
    private final Cause cause;

    @Deprecated
    public AfkStatusChangeEvent(final IUser affected, final boolean value) {
        this(affected, value, Cause.UNKNOWN);
    }

    public AfkStatusChangeEvent(final IUser affected, final boolean value, final Cause cause) {
        super(affected, affected, value);
        this.cause = cause;
    }

    public Cause getCause() {
        return cause;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * The cause of the AFK status change.
     */
    public enum Cause {
        ACTIVITY,
        MOVE,
        INTERACT,
        COMMAND,
        JOIN,
        CHAT,
        QUIT,
        UNKNOWN
    }
}
