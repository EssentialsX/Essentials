package com.earth2me.essentials.commands;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftServer;
import com.earth2me.essentials.Essentials;


public class Commandbanip extends EssentialsCommand
{
	public Commandbanip()
	{
		super("banip");
	}

	@Override
	public void run(Server server, Essentials parent, CommandSender sender, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 1)
		{
			sender.sendMessage("Usage: /" + commandLabel + " [address]");
			return;
		}

		((CraftServer)server).getHandle().c.f.c(args[0]);
		sender.sendMessage("§7Banned IP address.");
		Essentials.getStatic().loadBanList();

	}
}
