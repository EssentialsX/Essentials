package com.earth2me.essentials.commands;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftServer;
import com.earth2me.essentials.Essentials;


public class Commandunbanip extends EssentialsCommand
{
	public Commandunbanip()
	{
		super("unbanip");
	}
	
	@Override
	public String[] getTriggers()
	{
		return new String[] { getName(), "pardonip" };
	}

	@Override
	public void run(Server server, Essentials parent, CommandSender sender, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 1)
		{
			sender.sendMessage("Usage: /" + commandLabel + " [address]");
			return;
		}

		((CraftServer)server).getHandle().d(args[0]);
		sender.sendMessage("Unbanned IP address.");
		Essentials.getStatic().loadBanList();
	}
}
