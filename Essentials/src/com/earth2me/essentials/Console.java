package com.earth2me.essentials;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;


public final class Console implements IReplyTo
{
	private static final Console instance = new Console();
	private CommandSource replyTo;
	public final static String NAME = "Console";

	private Console()
	{
	}

	public static CommandSender getCommandSender(Server server) throws Exception
	{
		return server.getConsoleSender();
	}

	@Override
	public void setReplyTo(CommandSource user)
	{
		replyTo = user;
	}

	@Override
	public CommandSource getReplyTo()
	{
		return replyTo;
	}

	public static Console getConsoleReplyTo()
	{
		return instance;
	}
}
