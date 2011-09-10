package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
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
		if (args.length > 1)
		{

			world.setStorm(isStorm ? true : false);
			world.setWeatherDuration(Integer.parseInt(args[1]) * 20);
			user.sendMessage(isStorm
							 ? Util.format("weatherStormFor", args[1])
							 : Util.format("weatherSunFor", args[1]));
			return;
		}
		else
		{
			world.setStorm(isStorm ? true : false);
			user.sendMessage(isStorm
							 ? Util.i18n("weatherStorm")
							 : Util.i18n("weatherSun"));
			return;
		}
	}
}
