package net.ess3.api.events;

import net.ess3.api.IUser;


/**
 * <p>MuteStatusChangeEvent class.</p>
 *
 * @author LoopyD
 * @version $Id: $Id
 */
public class MuteStatusChangeEvent extends StatusChangeEvent {
    /**
     * <p>Constructor for MuteStatusChangeEvent.</p>
     *
     * @param affected a {@link net.ess3.api.IUser} object.
     * @param controller a {@link net.ess3.api.IUser} object.
     * @param value a boolean.
     */
    public MuteStatusChangeEvent(IUser affected, IUser controller, boolean value) {
        super(affected, controller, value);
    }
}
