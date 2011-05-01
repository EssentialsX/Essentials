package com.earth2me.essentials.commands;

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
	public void run(Server server, User user, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}

		boolean isStorm = args[0].equalsIgnoreCase("storm");
		World world = user.getWorld();
		user.charge(this);
		if (args.length > 1)
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
