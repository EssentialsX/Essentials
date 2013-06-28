package net.ess3.essentials.api.events;

import com.earth2me.essentials.User;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;


public class UserToggleGodmodeEvent extends Event implements Cancellable
{
	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled;
	private User user;
	private boolean enabled;

	/**
	 * Thrown when a user's god mode is toggled.
	 *
	 * @param user - the user whose god mode is toggled.
	 * @param enabled - true if god mode was enabled, false if disabled.
	 */
	public UserToggleGodmodeEvent (User user, boolean enabled)
	{
		enabled = this.enabled;
		user = this.user;
		this.cancelled = false;
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
