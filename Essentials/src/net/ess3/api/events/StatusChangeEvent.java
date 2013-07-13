package net.ess3.api.events;

import com.earth2me.essentials.User;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;


/**
 * This handles common boilerplate for other StatusChangeEvents
 *
 */
public class StatusChangeEvent extends Event implements Cancellable
{
	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled;
	User affected;
	User controller;

	public User getAffected()
	{
		return this.affected;
	}

	public User getController()
	{
		return controller;
	}

	@Override
	public HandlerList getHandlers()
	{
		return handlers;
	}

	public static HandlerList getHandlerList()
	{
		return handlers;
	}

	@Override
	public boolean isCancelled()
	{
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled)
	{
		this.cancelled = cancelled;
	}

}
