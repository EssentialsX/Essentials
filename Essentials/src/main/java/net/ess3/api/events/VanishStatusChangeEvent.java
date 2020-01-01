package net.ess3.api.events;

import net.ess3.api.IUser;

/**
 * Called only in Commandvanish. For other events please use classes such as PlayerJoinEvent and eventually {@link IUser#isVanished()}.
 */
public class VanishStatusChangeEvent extends StatusChangeEvent {
    public VanishStatusChangeEvent(IUser affected, IUser controller, boolean value) {
        super(affected, controller, value);
    }
}
