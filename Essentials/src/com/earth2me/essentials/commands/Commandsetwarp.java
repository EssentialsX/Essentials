package com.earth2me.essentials.commands;

import org.bukkit.Location;
import org.bukkit.Server;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;


public class Commandsetwarp extends EssentialsCommand
{
	public Commandsetwarp()
	{
		super("setwarp");
	}

	@Override
	public void run(Server server, Essentials parent, User user, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 1)
		{
			user.sendMessage("§cUsage: /setwarp [warp name]");
			return;
		}

		user.charge(this);
		Location loc = user.getLocation();
		Essentials.getWarps().setWarp(args[0], loc);
		user.sendMessage("§7Warp set.");
	}
}
