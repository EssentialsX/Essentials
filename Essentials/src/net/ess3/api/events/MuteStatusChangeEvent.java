package net.ess3.api.events;

import net.ess3.api.IUser;


public class MuteStatusChangeEvent extends StatusChangeEvent {
    private Long timestamp;
    private String reason;

    @Deprecated
    public MuteStatusChangeEvent(IUser affected, IUser controller, boolean value) {
        this(affected, controller, value, null, null);
    }

    public MuteStatusChangeEvent(IUser affected, IUser controller, boolean value, Long timestamp, String reason) {
        super(affected, controller, value);
        this.timestamp = timestamp;
        this.reason = reason == null ? null : (reason.isEmpty() ? null : reason);
    }

    /**
     * @return Returns null if the timestamp is unknown due to usage of deprecated api; Otherwise, the timestamp with 0 indicating permanent.
     */
    public Long getTimestamp() {
        return timestamp;
    }

    /**
     * @return Returns null if no reason is provided or due to the usage of a deprecated api; Otherwise, the reason.
     */
    public String getReason() {
        return reason;
    }
}
