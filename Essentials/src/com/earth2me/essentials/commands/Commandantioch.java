package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.TNTPrimed;

// This command has a in theme message that only shows if you supply a parameter #EasterEgg
public class Commandantioch extends EssentialsCommand
{
	public Commandantioch()
	{
		super("antioch");
	}

	@Override
	public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length > 0)
		{
			ess.broadcastMessage(user, "...lobbest thou thy Holy Hand Grenade of Antioch towards thy foe,");
			ess.broadcastMessage(user, "who being naughty in My sight, shall snuff it.");
		}		

		final Location loc = Util.getTarget(user);
		loc.getWorld().spawn(loc, TNTPrimed.class);
	}
}
