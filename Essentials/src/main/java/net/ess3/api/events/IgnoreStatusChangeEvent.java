package net.ess3.api.events;

import net.ess3.api.IUser;

/**
 * This event is currently unused, and is retained for ABI compatibility and potential future implementation.
 */
public class IgnoreStatusChangeEvent extends StatusChangeEvent {
    public IgnoreStatusChangeEvent(final IUser affected, final IUser controller, final boolean value) {
        super(affected, controller, value);
    }
}
