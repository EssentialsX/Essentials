package net.ess3.api.events;

import java.math.BigDecimal;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;


public class UserBalanceUpdateEvent extends Event
{
	private static final HandlerList handlers = new HandlerList();
	private final Player player;
	private final BigDecimal balance;

	public UserBalanceUpdateEvent(Player player, BigDecimal balance)
	{
		this.player = player;
		this.balance = balance;
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

	public Player getPlayer()
	{
		return player;
	}

	public BigDecimal getNewBalance()
	{
		return balance;
	}
}
