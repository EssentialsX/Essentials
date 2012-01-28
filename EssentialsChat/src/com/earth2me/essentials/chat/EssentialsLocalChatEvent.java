package com.earth2me.essentials.chat;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerChatEvent;


public class EssentialsLocalChatEvent extends Event implements Cancellable
{
	private Player player;
	private String message;
	private String format = "<%1$s> %2$s";
	private long radius;
	private boolean cancelled = false;
	private PlayerChatEvent parentEvent = null;
	private static final HandlerList handlers = new HandlerList();

	public EssentialsLocalChatEvent(final Player player, final String message, final String format, final long radius)
	{
		this.player = player;
		this.message = message;
		this.format = format;
		this.radius = radius;
	}

	public EssentialsLocalChatEvent(final PlayerChatEvent event, final long radius)
	{
		this(event.getPlayer(), event.getMessage(), event.getFormat(), radius);
		this.parentEvent = event;
	}

	@Override
	public boolean isCancelled()
	{
		return cancelled;
	}

	@Override
	public void setCancelled(final boolean cancel)
	{
		this.cancelled = cancel;
	}

	public String getMessage()
	{
		return message;
	}

	public void setMessage(final String message)
	{
		this.message = message;
	}

	public void setPlayer(final Player player)
	{
		this.player = player;
	}

	public Player getPlayer()
	{
		return player;
	}

	public String getFormat()
	{
		return format;
	}

	public void setFormat(final String format)
	{
		// Oh for a better way to do this!
		try
		{
			String.format(format, player, message);
		}
		catch (RuntimeException ex)
		{
			ex.fillInStackTrace();
			throw ex;
		}
		this.format = format;
	}

	public long getRadius()
	{
		return radius;
	}

	public void setRadius(final long radius)
	{
		this.radius = radius;
	}

	public PlayerChatEvent getParentEvent()
	{
		return parentEvent;
	}

	public HandlerList getHandlers()
	{
		return handlers;
	}

	public static HandlerList getHandlerList()
	{
		return handlers;
	}
}