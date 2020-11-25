package net.ess3.api.events;

import net.ess3.api.IUser;

/**
 * Fired when a player's flight status is toggled using /fly.
 */
public class FlyStatusChangeEvent extends StatusChangeEvent {
    public FlyStatusChangeEvent(final IUser affected, final IUser controller, final boolean value) {
        super(affected, controller, value);
    }
}
