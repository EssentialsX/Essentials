package net.ess3.api.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import net.ess3.api.IUser;


public class TPARequestEvent extends Event implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
    private IUser requester, requested;
    private boolean cancelled = false;

    public TPARequestEvent(IUser requester, IUser requested) {
    	super();
        this.requester = requester;
        this.requested = requested;
    }

    public IUser getRequester() {
    	return requester;
    }
    
    public IUser getRequested() {
    	return requested;
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
}
