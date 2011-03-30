package com.earth2me.essentials.commands;

import org.bukkit.Server;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;


public class Commanddepth extends EssentialsCommand
{
	public Commanddepth()
	{
		super("depth");
	}

	@Override
	public void run(Server server, Essentials parent, User user, String commandLabel, String[] args) throws Exception
	{
		user.charge(this);
		int y = user.getLocation().getBlockY() - 63;
		if (y > 0) user.sendMessage("§7You are " + y + " block(s) above sea level.");
		else if (y < 0) user.sendMessage("§7You are " + (-y) + " block(s) below sea level.");
		else user.sendMessage("§7You are at sea level.");
	}
}
