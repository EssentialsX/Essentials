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
	public void run(Server server, User user, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}
		World world = user.getWorld();

		charge(user);
		setWorldTime(world, args[0]);
	}

	@Override
	public void run(Server server, CommandSender sender, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}
		for (World world : server.getWorlds())
		{
			setWorldTime(world, args[0]);
		}

		sender.sendMessage("Time set in all worlds.");
	}

	private void setWorldTime(World world, String timeString) throws Exception
	{
		long time = world.getTime();
		time = time - time % 24000;
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
		throw new Exception("/time only supports day/night.");
	}
}
