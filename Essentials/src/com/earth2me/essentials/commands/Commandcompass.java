package com.earth2me.essentials.commands;

import org.bukkit.Server;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;


public class Commandcompass extends EssentialsCommand
{
	public Commandcompass()
	{
		super("compass");
	}

	@Override
	public void run(Server server, Essentials parent, User user, String commandLabel, String[] args) throws Exception
	{
		user.charge(this);
		int r = (int)user.getCorrectedYaw();
		String dir;
		if (r < 23) dir = "N";
		else if (r < 68) dir = "NE";
		else if (r < 113) dir = "E";
		else if (r < 158) dir = "SE";
		else if (r < 203) dir = "S";
		else if (r < 248) dir = "SW";
		else if (r < 293) dir = "W";
		else if (r < 338) dir = "NW";
		else dir = "N";
		user.sendMessage("§7Bearing: " + dir + " (" + r + " degrees)");
	}
}
