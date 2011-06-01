package com.earth2me.essentials;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftServer;

public final class Console implements IReplyTo {
	private static Console instance = new Console();
	private CommandSender replyTo;
	public final static String NAME = "Console";
	
	private Console() {
		
	}
	
	public static CommandSender getCommandSender(Server server) throws Exception {
		if (! (server instanceof CraftServer)) {
			throw new Exception(Util.i18n("invalidServer"));
		}
		return ((CraftServer)server).getServer().console;
	}

	public void setReplyTo(CommandSender user) {
		replyTo = user;
	}

	public CommandSender getReplyTo() {
		return replyTo;
	}
	
	public static Console getConsoleReplyTo() {
		return instance;
	}
}
