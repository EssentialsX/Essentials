package net.ess3.api.events;

import net.ess3.api.IUser;


/**
 * <p>JailStatusChangeEvent class.</p>
 *
 * @author LoopyD
 * @version $Id: $Id
 */
public class JailStatusChangeEvent extends StatusChangeEvent {
    /**
     * <p>Constructor for JailStatusChangeEvent.</p>
     *
     * @param affected a {@link net.ess3.api.IUser} object.
     * @param controller a {@link net.ess3.api.IUser} object.
     * @param value a boolean.
     */
    public JailStatusChangeEvent(IUser affected, IUser controller, boolean value) {
        super(affected, controller, value);
    }
}
