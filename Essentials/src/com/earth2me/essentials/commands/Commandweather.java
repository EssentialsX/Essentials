package com.earth2me.essentials.commands;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;

import org.bukkit.Server;
import org.bukkit.World;


public class Commandweather extends EssentialsCommand
{
	public Commandweather()
	{
		super("weather");
	}

	@Override
	public void run(Server server, Essentials parent, User user, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 1)
		{
			user.sendMessage("§cUsage: /" + commandLabel + " <storm/sun> [duration]");
			return;
		}

		if (!user.isAuthorized(this))
		{
			user.sendMessage("§cThe power of the sky has been denied to you");
			return;
		}

		boolean isStorm = args[0].equalsIgnoreCase("storm");
		World world = user.getWorld();
		user.charge(this);
		if (!args[1].isEmpty() || args[1] != null)
		{

			world.setStorm(isStorm ? true : false);
			world.setWeatherDuration(Integer.parseInt(args[1]) * 20);
			user.sendMessage("§7You set the weather to  " + (isStorm ? "storm" : "sun") + " in your world for " + args[1] + " seconds");
			return;
		}
		else
		{
			world.setStorm(isStorm ? true : false);
			user.sendMessage("§7You set the weather to  " + (isStorm ? "storm" : "sun") + " in your world");
			return;
		}
	}
}



