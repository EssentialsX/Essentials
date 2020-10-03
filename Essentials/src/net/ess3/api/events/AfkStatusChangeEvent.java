package net.ess3.api.events;

import net.ess3.api.IUser;

public class AfkStatusChangeEvent extends StatusChangeEvent {
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
