package net.ess3.api.events;

import com.earth2me.essentials.CommandSource;
import net.ess3.api.IUser;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Fired when a /tpa, /tpaall or /tpahere request is made.
 */
public class TPARequestEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final CommandSource requester;
    private final IUser target;
    private boolean cancelled = false;
    private boolean requestToTPAHere;

    public TPARequestEvent(final CommandSource requester, final IUser target, final boolean tpaHere) {
        super();
        this.requester = requester;
        this.target = target;
        this.requestToTPAHere = tpaHere;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public CommandSource getRequester() {
        return requester;
    }

    public IUser getTarget() {
        return target;
    }

    public boolean isTeleportHere() {
        return requestToTPAHere;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
