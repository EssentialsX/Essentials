package net.ess3.api.events;

import net.ess3.api.IUser;

public class BanStatusChangeEvent extends ReasonedStatusChangeEvent {
    public BanStatusChangeEvent(IUser affected, IUser controller, boolean value, String reason) {
        super(affected, controller, value, reason);
    }
}
