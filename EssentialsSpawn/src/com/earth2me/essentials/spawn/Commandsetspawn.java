package com.earth2me.essentials.spawn;

import org.bukkit.Server;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import com.earth2me.essentials.commands.EssentialsCommand;


public class Commandsetspawn extends EssentialsCommand
{
	public Commandsetspawn()
	{
		super("setspawn");
	}

	@Override
	public void run(Server server, User user, String commandLabel, String[] args) throws Exception
	{
		final String group = args.length > 0 ? getFinalArg(args, 0) : "default";
		ess.getSpawn().setSpawn(user.getLocation(), group);
		user.sendMessage(Util.format("spawnSet", group));
	}
}
