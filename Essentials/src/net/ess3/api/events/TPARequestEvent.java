package net.ess3.api.events;

import com.earth2me.essentials.CommandSource;
import net.ess3.api.IUser;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;


/**
 * <p>TPARequestEvent class.</p>
 *
 * @author LoopyD
 * @version $Id: $Id
 */
public class TPARequestEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private CommandSource requester;
    private IUser target;
    private boolean cancelled = false, requestToTPAHere = false;

    /**
     * <p>Constructor for TPARequestEvent.</p>
     *
     * @param requester a {@link com.earth2me.essentials.CommandSource} object.
     * @param target a {@link net.ess3.api.IUser} object.
     * @param tpaHere a boolean.
     */
    public TPARequestEvent(CommandSource requester, IUser target, boolean tpaHere) {
        super();
        this.requester = requester;
        this.target = target;
        this.requestToTPAHere = tpaHere;
    }

    /**
     * <p>Getter for the field <code>requester</code>.</p>
     *
     * @return a {@link com.earth2me.essentials.CommandSource} object.
     */
    public CommandSource getRequester() {
        return requester;
    }

    /**
     * <p>Getter for the field <code>target</code>.</p>
     *
     * @return a {@link net.ess3.api.IUser} object.
     */
    public IUser getTarget() {
        return target;
    }

    /**
     * <p>isTeleportHere.</p>
     *
     * @return a boolean.
     */
    public boolean isTeleportHere() {
        return requestToTPAHere;
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
}
