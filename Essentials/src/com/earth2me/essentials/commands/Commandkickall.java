package com.earth2me.essentials.commands;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;

public class Commandkickall extends EssentialsCommand
{
	public Commandkickall()
	{
		super("kickall");
	}

	@Override
	public void run(Server server, Essentials parent, User user, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 1)
		{
			user.sendMessage("§7Usage: /" + commandLabel + "<reason>");
			return;
		}


		for (Player p : server.getOnlinePlayers())
		{
			if (server.getOnlinePlayers().length == 1 && p.getName().equalsIgnoreCase(user.getName()))
			{
				user.sendMessage("§7Only you online...");
				return;
			}
			else
			{
				if (!p.getName().equalsIgnoreCase(user.getName()))
				{
					p.kickPlayer(args.length < 1 ? args[0] : "Kicked from server");
				}
			}
		}
	}

	@Override
	public void run(Server server, Essentials parent, CommandSender sender, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 1)
		{
			sender.sendMessage("Usage: /" + commandLabel + "<reason>");
			return;
		}

		for (Player p : server.getOnlinePlayers())
			p.kickPlayer(args.length < 1 ? args[0] : "Kicked from server");
	}
}
