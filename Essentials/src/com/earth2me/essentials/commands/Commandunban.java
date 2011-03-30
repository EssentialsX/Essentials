package com.earth2me.essentials.commands;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftServer;
import com.earth2me.essentials.Essentials;


public class Commandunban extends EssentialsCommand
{
	public Commandunban()
	{
		super("unban");
	}

	@Override
	public String[] getTriggers()
	{
		return new String[] { getName(), "pardon" };
	}

	@Override
	public void run(Server server, Essentials parent, CommandSender sender, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 1)
		{
			sender.sendMessage("Usage: /" + commandLabel  + " [player]");
			return;
		}

		((CraftServer)server).getHandle().c.f.b(args[0]);
		sender.sendMessage("Unbanned player.");
		Essentials.getStatic().loadBanList();
	}
}
