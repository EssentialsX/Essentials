package com.earth2me.essentials;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class CommandSource implements IReplyTo
{
	private CommandSource replyTo = null;
	protected CommandSender sender;

	public CommandSource(final CommandSender base)
	{
		this.sender = base;
	}

	public final CommandSender getSender()
	{
		return sender;
	}

	public final Player getPlayer()
	{
		if (sender instanceof Player)
		{
			return (Player)sender;
		}
		return null;
	}
	
	public final boolean isPlayer()
	{
		return (sender instanceof Player);
	}

	public final CommandSender setSender(final CommandSender base)
	{
		return this.sender = base;
	}
	
	
	public void sendMessage(String message)
	{
		if (!message.isEmpty())
		{
			sender.sendMessage(message);
		}
	}

	@Override
	public void setReplyTo(final CommandSource user)
	{
		replyTo = user;
	}

	@Override
	public CommandSource getReplyTo()
	{
		return replyTo;
	}
}
