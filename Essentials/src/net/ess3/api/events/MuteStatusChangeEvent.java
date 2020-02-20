package net.ess3.api.events;

import net.ess3.api.IUser;

public class MuteStatusChangeEvent extends ReasonedStatusChangeEvent {
    public MuteStatusChangeEvent(IUser affected, IUser controller, boolean value, String reason) {
        super(affected, controller, value, reason);
    }
}
