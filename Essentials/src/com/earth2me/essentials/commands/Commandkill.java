package com.earth2me.essentials.commands;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import com.earth2me.essentials.Essentials;
import org.bukkit.entity.Player;
import com.earth2me.essentials.User;


public class Commandkill extends EssentialsCommand
{
	public Commandkill()
	{
		super("kill");
	}

	@Override
	public void run(Server server, Essentials parent, CommandSender sender, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 1)
		{
			sender.sendMessage("§cUsage: /kill [player]");
			return;
		}

		User.charge(sender, this);
		for (Player p : server.matchPlayer(args[0]))
		{
			p.setHealth(0);
			sender.sendMessage("§cKilled " + p.getDisplayName() + ".");
		}
	}
}
