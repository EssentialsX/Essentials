package net.ess3.api.events;

import net.ess3.api.IUser;
import org.bukkit.event.Cancellable;


/**
 * <p>NickChangeEvent class.</p>
 *
 * @author LoopyD
 * @version $Id: $Id
 */
public class NickChangeEvent extends StateChangeEvent implements Cancellable {
    private String newValue;

    /**
     * <p>Constructor for NickChangeEvent.</p>
     *
     * @param affected a {@link net.ess3.api.IUser} object.
     * @param controller a {@link net.ess3.api.IUser} object.
     * @param value a {@link java.lang.String} object.
     */
    public NickChangeEvent(IUser affected, IUser controller, String value) {
        super(affected, controller);
        this.newValue = value;
    }

    /**
     * <p>getValue.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getValue() {
        return newValue;
    }
}
