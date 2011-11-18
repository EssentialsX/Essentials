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
	public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{
		final int depth = user.getLocation().getBlockY() - 63;
		if (depth > 0)
		{
			user.sendMessage(Util.format("depthAboveSea", depth));
		}
		else if (depth < 0)
		{
			user.sendMessage(Util.format("depthBelowSea", (-depth)));
		}
		else
		{
			user.sendMessage(Util.i18n("depth"));
		}
	}
}
