package net.ess3.api.events;

import net.ess3.api.IUser;


public class AfkStatusChangeEvent extends StatusChangeEvent {
    private Cause cause;

    @Deprecated
    public AfkStatusChangeEvent(IUser affected, boolean value) {
        this(affected, value, Cause.UNKNOWN);
    }

    public AfkStatusChangeEvent(IUser affected, boolean value, Cause cause) {
        super(affected, affected, value);
        this.cause = cause;
    }

    public Cause getCause() {
        return cause;
    }

    public enum Cause {
        ACTIVITY,
        MOVE,
        INTERACT,
        COMMAND,
        JOIN,
        QUIT,
        UNKNOWN
    }
}
