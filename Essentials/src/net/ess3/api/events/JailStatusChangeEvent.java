package net.ess3.api.events;

import net.ess3.api.IUser;

public class JailStatusChangeEvent extends StatusChangeEvent {
    public JailStatusChangeEvent(final IUser affected, final IUser controller, final boolean value) {
        super(affected, controller, value);
    }
}
