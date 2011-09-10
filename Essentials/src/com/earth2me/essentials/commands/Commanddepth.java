package com.earth2me.essentials.commands;

import org.bukkit.Server;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;


public class Commanddepth extends EssentialsCommand
{
	public Commanddepth()
	{
		super("depth");
	}

	@Override
	public void run(Server server, User user, String commandLabel, String[] args) throws Exception
	{
		int y = user.getLocation().getBlockY() - 63;
		if (y > 0)
		{
			user.sendMessage(Util.format("depthAboveSea", y));
		}
		else if (y < 0)
		{
			user.sendMessage(Util.format("depthBelowSea", (-y)));
		}
		else
		{
			user.sendMessage(Util.i18n("depth"));
		}
	}
}
