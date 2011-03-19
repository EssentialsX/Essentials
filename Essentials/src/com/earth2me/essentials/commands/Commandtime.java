package com.earth2me.essentials.commands;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;


public class Commandtime extends EssentialsCommand
{
	public Commandtime()
	{
		super("time");
	}

	@Override
	public void run(Server server, Essentials parent, User user, String commandLabel, String[] args) throws Exception
	{
		World world = user.getWorld();
		long time = world.getTime();
		time = time - time % 24000;
		if (args.length < 1)
		{
			user.sendMessage("§cUsage: /time [day|night]");
			return;
		}
		if ("day".equalsIgnoreCase(args[0]))
		{
			user.charge(this);
			world.setTime(time + 24000);
			return;
		}
		if ("night".equalsIgnoreCase(args[0]))
		{
			user.charge(this);
			world.setTime(time + 37700);
			return;
		}
		throw new Exception("/time only supports day/night.");
	}

	@Override
	public void run(Server server, Essentials parent, CommandSender sender, String commandLabel, String[] args) throws Exception
	{
		for (World world : server.getWorlds())
		{
			long time = world.getTime();
			time = time - time % 24000;
			if (args.length < 1)
			{
				sender.sendMessage("Usage: /time [day|night]");
				return;
			}

			if ("day".equalsIgnoreCase(args[0])) world.setTime(time + 24000);
			else if ("night".equalsIgnoreCase(args[0])) world.setTime(time + 37700);
			else throw new Exception("/time only supports day/night.");
		}

		sender.sendMessage("Time set in all worlds.");
	}
}
