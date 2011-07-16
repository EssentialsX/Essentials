package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import org.bukkit.Server;
import org.bukkit.entity.Fireball;
import org.bukkit.util.Vector;


public class Commandfireball extends EssentialsCommand
{

	public Commandfireball()
	{
		super("fireball");
	}

	@Override
	protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{
		charge(user);
        final Vector direction = user.getEyeLocation().getDirection().multiply(2);
		user.getWorld().spawn(user.getEyeLocation().add(direction.getX(), direction.getY(), direction.getZ()), Fireball.class);
	}
}
