package com.earth2me.essentials;

import com.earth2me.essentials.api.IReplyTo;
import org.bukkit.command.CommandSender;


public final class Console implements IReplyTo
{
	private static Console instance = new Console();
	private CommandSender replyTo;
	public final static String NAME = "Console";

	private Console()
	{
	}

	@Override
	public void setReplyTo(final CommandSender user)
	{
		replyTo = user;
	}

	@Override
	public CommandSender getReplyTo()
	{
		return replyTo;
	}

	public static Console getConsoleReplyTo()
	{
		return instance;
	}
}
