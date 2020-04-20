package net.ess3.api.events;

import net.ess3.api.IUser;

import java.util.Optional;


public class MuteStatusChangeEvent extends StatusChangeEvent {
    private Long timestamp;
    private String reason;

    public MuteStatusChangeEvent(IUser affected, IUser controller, boolean value, Long timestamp, String reason) {
        super(affected, controller, value);
        this.timestamp = timestamp;
        this.reason = reason == null ? null : (reason.isEmpty() ? null : reason);
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
}
