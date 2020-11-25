package net.ess3.api.events;

import net.ess3.api.IUser;

/**
 * Fired when a user's god status is toggled.
 * <p>
 * Note that in older versions (original Essentials and early EssentialsX), the #getAffected
 * and #getController methods are inverted.
 */
public class GodStatusChangeEvent extends StatusChangeEvent {
    public GodStatusChangeEvent(final IUser affected, final IUser controller, final boolean value) {
        super(affected, controller, value);
    }
}
