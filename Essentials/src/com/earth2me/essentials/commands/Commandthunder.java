package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import org.bukkit.Server;
import org.bukkit.World;


public class Commandthunder extends EssentialsCommand
{
	public Commandthunder()
	{
		super("thunder");
	}

	@Override
	public void run(Server server, User user, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}

		World world = user.getWorld();
		boolean setThunder = args[0].equalsIgnoreCase("true");
		if (args.length > 1)
		{

			world.setThundering(setThunder ? true : false);
			world.setThunderDuration(Integer.parseInt(args[1]) * 20);
			user.sendMessage(Util.format("thunderDuration", (setThunder ? Util.i18n("enabled") : Util.i18n("disabled")), Integer.parseInt(args[1])));
			
		}
		else
		{
			world.setThundering(setThunder ? true : false);
			user.sendMessage(Util.format("thunder", setThunder ? Util.i18n("enabled") : Util.i18n("disabled")));
		}

	}
}
