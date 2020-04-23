package net.ess3.api.events;

import net.ess3.api.IUser;

/**
 * Fired when a player's vanish status changes due to the /vanish command.
 *
 * For other cases where the player's vanish status changes, you should listen on PlayerJoinEvent and
 * check with {@link IUser#isVanished()}.
 */
public class VanishStatusChangeEvent extends StatusChangeEvent {
    public VanishStatusChangeEvent(IUser affected, IUser controller, boolean value) {
        super(affected, controller, value);
    }
}
