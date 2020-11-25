package net.ess3.api.events;

import net.ess3.api.IUser;

/**
 * Fired when a player's vanish status changes due to the /vanish command.
 * <p>
 * For other cases where the player's vanish status changes, you should listen on PlayerJoinEvent and
 * check with {@link IUser#isVanished()}.
 */
public class VanishStatusChangeEvent extends StatusChangeEvent {
    public VanishStatusChangeEvent(final IUser affected, final IUser controller, final boolean value) {
        super(affected, controller, value);
    }
}
