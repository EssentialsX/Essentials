package net.ess3.api.events;

import net.ess3.api.IUser;


/**
 * <p>AfkStatusChangeEvent class.</p>
 *
 * @author LoopyD
 * @version $Id: $Id
 */
public class AfkStatusChangeEvent extends StatusChangeEvent {
    /**
     * <p>Constructor for AfkStatusChangeEvent.</p>
     *
     * @param affected a {@link net.ess3.api.IUser} object.
     * @param value a boolean.
     */
    public AfkStatusChangeEvent(IUser affected, boolean value) {
        super(affected, affected, value);
    }
}
