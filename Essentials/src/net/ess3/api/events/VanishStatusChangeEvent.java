package net.ess3.api.events;

import net.ess3.api.IUser;

/**
 * Called only in Commandvanish. For other events please use classes such as PlayerJoinEvent and eventually {@link net.ess3.api.IUser#isVanished()}.
 *
 * @author LoopyD
 * @version $Id: $Id
 */
public class VanishStatusChangeEvent extends StatusChangeEvent {
    /**
     * <p>Constructor for VanishStatusChangeEvent.</p>
     *
     * @param affected a {@link net.ess3.api.IUser} object.
     * @param controller a {@link net.ess3.api.IUser} object.
     * @param value a boolean.
     */
    public VanishStatusChangeEvent(IUser affected, IUser controller, boolean value) {
        super(affected, controller, value);
    }
}
