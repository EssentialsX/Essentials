package com.earth2me.essentials.commands;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;

import org.bukkit.Server;
import org.bukkit.World;


public class Commandthunder extends EssentialsCommand
{
	public Commandthunder()
	{
		super("thunder");
	}

	@Override
	public void run(Server server, Essentials parent, User user, String commandLabel, String[] args) throws Exception
	{

		if (args.length < 1)
		{
			user.sendMessage("§cUsage: /" + commandLabel + " <true/false> [duration]");
			return;
		}

		user.charge(this);
		World world = user.getWorld();
		boolean setThunder = args[0].equalsIgnoreCase("true");
		if (!args[1].isEmpty() || args[1] != null)
		{

			world.setThundering(setThunder ? true : false);
			world.setThunderDuration(Integer.parseInt(args[1]) * 20);
			user.sendMessage("§7You " + (setThunder ? "enabled" : "disabled") + " thunder in your world for " + args[1] + " seconds");
		}
		else
		{
			world.setThundering(setThunder ? true : false);
			user.sendMessage("§7You " + (setThunder ? "enabled" : "disabled") + " thunder in your world");
		}

	}
}
