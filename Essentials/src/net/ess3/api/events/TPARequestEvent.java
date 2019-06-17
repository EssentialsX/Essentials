package net.ess3.api.events;

import com.earth2me.essentials.CommandSource;
import net.ess3.api.IUser;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;


public class TPARequestEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private CommandSource requester;
    private IUser target;
    private boolean cancelled = false, requestToTPAHere = false;

    public TPARequestEvent(CommandSource requester, IUser target, boolean tpaHere) {
        super();
        this.requester = requester;
        this.target = target;
        this.requestToTPAHere = tpaHere;
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
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}