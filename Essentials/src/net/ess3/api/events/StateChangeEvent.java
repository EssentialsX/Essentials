package net.ess3.api.events;

import net.ess3.api.IUser;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;


/**
 * This handles common boilerplate for other StateChangeEvents
 *
 * @author LoopyD
 * @version $Id: $Id
 */
public class StateChangeEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled = false;
    IUser affected;
    IUser controller;

    /**
     * <p>Constructor for StateChangeEvent.</p>
     *
     * @param affected a {@link net.ess3.api.IUser} object.
     * @param controller a {@link net.ess3.api.IUser} object.
     */
    public StateChangeEvent(IUser affected, IUser controller) {
        super();
        this.affected = affected;
        this.controller = controller;
    }

    /**
     * <p>Constructor for StateChangeEvent.</p>
     *
     * @param isAsync a boolean.
     * @param affected a {@link net.ess3.api.IUser} object.
     * @param controller a {@link net.ess3.api.IUser} object.
     */
    public StateChangeEvent(boolean isAsync, IUser affected, IUser controller) {
        super(isAsync);
        this.affected = affected;
        this.controller = controller;
    }

    /**
     * <p>Getter for the field <code>affected</code>.</p>
     *
     * @return a {@link net.ess3.api.IUser} object.
     */
    public IUser getAffected() {
        return this.affected;
    }

    /**
     * <p>Getter for the field <code>controller</code>.</p>
     *
     * @return a {@link net.ess3.api.IUser} object.
     */
    public IUser getController() {
        return controller;
    }

    /** {@inheritDoc} */
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    /**
     * <p>getHandlerList.</p>
     *
     * @return a {@link org.bukkit.event.HandlerList} object.
     */
    public static HandlerList getHandlerList() {
        return handlers;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    /** {@inheritDoc} */
    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
