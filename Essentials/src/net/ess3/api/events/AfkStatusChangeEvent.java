package net.ess3.api.events;

import net.ess3.api.IUser;


public class AfkStatusChangeEvent extends StatusChangeEvent {
    private String message = null;

    // TODO: refactor AFK code to pass include /afk message here
    public AfkStatusChangeEvent(IUser affected, boolean value, String message) {
        super(affected, affected, value);
        this.message = message;
    }

    @Deprecated
    public AfkStatusChangeEvent(IUser affected, boolean value) {
        super(affected, affected, value);
    }

    public String getMessage() {
        return message;
    }
}
