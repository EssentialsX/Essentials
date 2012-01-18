package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.api.IUser;
import org.bukkit.World;
import org.bukkit.command.CommandSender;


public class Commandweather extends EssentialsCommand
{
	//TODO: Remove duplication
	@Override
	public void run(final IUser user, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}

		final boolean isStorm = args[0].equalsIgnoreCase("storm");
		final World world = user.getWorld();
		if (args.length > 1)
		{

			world.setStorm(isStorm ? true : false);
			world.setWeatherDuration(Integer.parseInt(args[1]) * 20);
			user.sendMessage(isStorm
							 ? _("weatherStormFor", world.getName(), args[1])
							 : _("weatherSunFor", world.getName(), args[1]));
		}
		else
		{
			world.setStorm(isStorm ? true : false);
			user.sendMessage(isStorm
							 ? _("weatherStorm", world.getName())
							 : _("weatherSun", world.getName()));
		}
	}

	//TODO: Translate these
	@Override
	protected void run(final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 2) //running from console means inserting a world arg before other args
		{
			throw new Exception("When running from console, usage is: /" + commandName + " <world> <storm/sun> [duration]");
		}

		final boolean isStorm = args[1].equalsIgnoreCase("storm");
		final World world = server.getWorld(args[0]);
		if (world == null)
		{
			throw new Exception("World named " + args[0] + " not found!");
		}
		if (args.length > 2)
		{

			world.setStorm(isStorm ? true : false);
			world.setWeatherDuration(Integer.parseInt(args[2]) * 20);
			sender.sendMessage(isStorm
							   ? _("weatherStormFor", world.getName(), args[2])
							   : _("weatherSunFor", world.getName(), args[2]));
		}
		else
		{
			world.setStorm(isStorm ? true : false);
			sender.sendMessage(isStorm
							   ? _("weatherStorm", world.getName())
							   : _("weatherSun", world.getName()));
		}
	}
}
