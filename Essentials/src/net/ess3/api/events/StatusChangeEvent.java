package net.ess3.api.events;

import net.ess3.api.IUser;
import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;


/**
 * This handles common boilerplate for other StatusChangeEvents
 *
 * @author LoopyD
 * @version $Id: $Id
 */
public class StatusChangeEvent extends StateChangeEvent implements Cancellable {
    private boolean newValue;

    /**
     * <p>Constructor for StatusChangeEvent.</p>
     *
     * @param affected a {@link net.ess3.api.IUser} object.
     * @param controller a {@link net.ess3.api.IUser} object.
     * @param value a boolean.
     */
    public StatusChangeEvent(IUser affected, IUser controller, boolean value) {
        this(!Bukkit.getServer().isPrimaryThread(), affected, controller, value);
    }

    /**
     * <p>Constructor for StatusChangeEvent.</p>
     *
     * @param isAsync a boolean.
     * @param affected a {@link net.ess3.api.IUser} object.
     * @param controller a {@link net.ess3.api.IUser} object.
     * @param value a boolean.
     */
    public StatusChangeEvent(boolean isAsync, IUser affected, IUser controller, boolean value) {
        super(isAsync, affected, controller);
        this.newValue = value;
    }

    /**
     * <p>getValue.</p>
     *
     * @return a boolean.
     */
    public boolean getValue() {
        return newValue;
    }
}
