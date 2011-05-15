package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import org.bukkit.Server;


public class Commandignore extends EssentialsCommand
{

	public Commandignore()
	{
		super("ignore");
	}

	@Override
	protected void run(Server server, User user, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}
		User u;
		try
		{
			u = getPlayer(server, args, 0);
		}
		catch(NoSuchFieldException ex)
		{
			u = ess.getOfflineUser(args[0]);
		}
		if (u == null)
		{
			throw new Exception(Util.i18n("playerNotFound"));
		}
		String name = u.getName();
		if (user.isIgnoredPlayer(name)) {
			user.setIgnoredPlayer(name, false);
			user.sendMessage(Util.format("unignorePlayer", u.getName()));
		}
		else
		{
			user.setIgnoredPlayer(name, true);
			user.sendMessage(Util.format("ignorePlayer", u.getName()));
		}
	}
	
	
}
