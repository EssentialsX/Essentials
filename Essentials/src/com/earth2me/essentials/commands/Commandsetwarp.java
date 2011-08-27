package com.earth2me.essentials.commands;

import org.bukkit.Location;
import org.bukkit.Server;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;


public class Commandsetwarp extends EssentialsCommand
{
	public Commandsetwarp()
	{
		super("setwarp");
	}

	@Override
	public void run(Server server, User user, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}

		Location loc = user.getLocation();
		ess.getWarps().setWarp(args[0], loc);
		user.sendMessage(Util.format("warpSet", args[0]));
	}
}
