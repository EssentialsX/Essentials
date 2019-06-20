package net.ess3.api.events;

import net.ess3.api.IUser;


/**
 * <p>GodStatusChangeEvent class.</p>
 *
 * @author LoopyD
 * @version $Id: $Id
 */
public class GodStatusChangeEvent extends StatusChangeEvent {
    /**
     * <p>Constructor for GodStatusChangeEvent.</p>
     *
     * @param affected a {@link net.ess3.api.IUser} object.
     * @param controller a {@link net.ess3.api.IUser} object.
     * @param value a boolean.
     */
    public GodStatusChangeEvent(IUser affected, IUser controller, boolean value) {
        super(affected, controller, value);
    }
}
