package com.earth2me.essentials.commands;

import org.bukkit.Location;
import org.bukkit.Server;
import com.earth2me.essentials.User;
import com.earth2me.essentials.TargetBlock;
import org.bukkit.entity.TNTPrimed;


public class Commandantioch extends EssentialsCommand
{
	public Commandantioch()
	{
		super("antioch");
	}

	@Override
	public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{
		charge(user);
		ess.broadcastMessage(user.getName(), "...lobbest thou thy Holy Hand Grenade of Antioch towards thy foe,");
		ess.broadcastMessage(user.getName(), "who being naughty in My sight, shall snuff it.");

		final Location loc = new TargetBlock(user).getTargetBlock().getLocation();
		loc.getWorld().spawn(loc, TNTPrimed.class);
	}
}
