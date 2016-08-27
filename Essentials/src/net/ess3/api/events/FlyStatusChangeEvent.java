package net.ess3.api.events;

import net.ess3.api.IUser;

public class FlyStatusChangeEvent extends StatusChangeEvent {
    public FlyStatusChangeEvent(IUser affected, IUser controller, boolean value) {
        super(affected, controller, value);
    }
}
