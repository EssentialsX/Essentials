package net.ess3.api.events;

import net.ess3.api.IUser;


/**
 * <p>IgnoreStatusChangeEvent class.</p>
 *
 * @author LoopyD
 * @version $Id: $Id
 */
public class IgnoreStatusChangeEvent extends StatusChangeEvent {
    /**
     * <p>Constructor for IgnoreStatusChangeEvent.</p>
     *
     * @param affected a {@link net.ess3.api.IUser} object.
     * @param controller a {@link net.ess3.api.IUser} object.
     * @param value a boolean.
     */
    public IgnoreStatusChangeEvent(IUser affected, IUser controller, boolean value) {
        super(affected, controller, value);
    }
}
