package com.earth2me.essentials.commands;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;


public class Commandtime extends EssentialsCommand
{
	public Commandtime()
	{
		super("time");
	}

	@Override
	public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}
		if (user.isAuthorized("essentials.time.world"))
		{
			final World world = user.getWorld();

			charge(user);
			setWorldTime(world, args[0]);
		}
		else
		{
			if (user.isAuthorized("essentials.time.player"))
			{

				long time = user.getPlayerTime();
				time -= time % 24000;
				if ("day".equalsIgnoreCase(args[0]))
				{
					final World world = user.getWorld();
					user.setPlayerTime(time + 24000 - world.getTime(), true);
					return;
				}
				if ("night".equalsIgnoreCase(args[0]))
				{
					final World world = user.getWorld();
					user.setPlayerTime(time + 37700 - world.getTime(), true);
					return;
				}
				throw new Exception(Util.i18n("onlyDayNight"));
			}
		}
	}

	@Override
	public void run(final Server server, final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}
		for (World world : server.getWorlds())
		{
			setWorldTime(world, args[0]);
		}

		sender.sendMessage(Util.i18n("timeSet"));
	}

	private void setWorldTime(final World world, final String timeString) throws Exception
	{
		long time = world.getTime();
		time -= time % 24000;
		if ("day".equalsIgnoreCase(timeString))
		{
			world.setTime(time + 24000);
			return;
		}
		if ("night".equalsIgnoreCase(timeString))
		{
			world.setTime(time + 37700);
			return;
		}
		throw new Exception(Util.i18n("onlyDayNight"));
	}
}
