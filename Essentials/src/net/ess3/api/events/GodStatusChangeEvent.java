package net.ess3.api.events;

import net.ess3.api.IUser;


/**
 * Fired when a user's god status is toggled.
 *
 * Note that in older versions (original Essentials and early EssentialsX), the #getAffected
 * and #getController methods are inverted.
 */
public class GodStatusChangeEvent extends StatusChangeEvent {
    public GodStatusChangeEvent(IUser affected, IUser controller, boolean value) {
        super(affected, controller, value);
    }
}
