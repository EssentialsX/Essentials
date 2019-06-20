package net.ess3.api.events;

import net.ess3.api.IUser;

/**
 * <p>FlyStatusChangeEvent class.</p>
 *
 * @author LoopyD
 * @version $Id: $Id
 */
public class FlyStatusChangeEvent extends StatusChangeEvent {
    /**
     * <p>Constructor for FlyStatusChangeEvent.</p>
     *
     * @param affected a {@link net.ess3.api.IUser} object.
     * @param controller a {@link net.ess3.api.IUser} object.
     * @param value a boolean.
     */
    public FlyStatusChangeEvent(IUser affected, IUser controller, boolean value) {
        super(affected, controller, value);
    }
}
