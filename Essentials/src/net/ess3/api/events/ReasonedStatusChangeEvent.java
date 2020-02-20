package net.ess3.api.events;

import net.ess3.api.IUser;

public class ReasonedStatusChangeEvent extends StatusChangeEvent {
    private String reason;

    public ReasonedStatusChangeEvent(IUser affected, IUser controller, boolean value, String reason) {
        super(affected, controller, value);
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}