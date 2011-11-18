package com.earth2me.essentials;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;


public final class Console implements IReplyTo
{
	private static Console instance = new Console();
	private CommandSender replyTo;
	public final static String NAME = "Console";

	private Console()
	{
	}

	public static CommandSender getCommandSender(Server server) throws Exception
	{
		return server.getConsoleSender();
	}

	@Override
	public void setReplyTo(CommandSender user)
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
