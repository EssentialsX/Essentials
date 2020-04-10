package net.ess3.api.events;

import net.ess3.api.IUser;


public class MuteStatusChangeEvent extends StatusChangeEvent {
    private Long timestamp;
    private String reason;

    public MuteStatusChangeEvent(IUser affected, IUser controller, boolean value, long timestamp, String reason) {
        super(affected, controller, value);
        this.timestamp = timestamp;
        this.reason = reason == null ? null : (reason.isEmpty() ? null : reason);
    }

    /**
     * @return If the mute is temporary, returns the timestamp; if permanent, returns 0, and if unknown, returns -1.
     */
    public Long getTimestamp() {
        return timestamp;
    }

    /**
     * @return Returns the reason if provided, otherwise null.
     */
    public String getReason() {
        return reason;
    }
}
