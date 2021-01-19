package net.ess3.api.events;

import net.ess3.api.IUser;
import org.bukkit.event.HandlerList;

import java.util.Optional;

/**
 * Fired when a player's mute status is changed.
 */
public class MuteStatusChangeEvent extends StatusChangeEvent {
    private static final HandlerList handlers = new HandlerList();
    private final Long timestamp;
    private final String reason;

    public MuteStatusChangeEvent(final IUser affected, final IUser controller, final boolean value, final Long timestamp, final String reason) {
        super(affected, controller, value);
        this.timestamp = timestamp;
        this.reason = reason == null ? null : reason.isEmpty() ? null : reason;
    }

    /**
     * @return If the mute is temporary, returns a present optional with the timestamp; if permanent or unknown, returns an empty optional.
     */
    public Optional<Long> getTimestamp() {
        return Optional.ofNullable(timestamp <= 0 ? null : timestamp);
    }

    /**
     * @return Returns the reason if provided, otherwise null.
     */
    public String getReason() {
        return reason;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
