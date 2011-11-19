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
	protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}
		User player;
		try
		{
			player = getPlayer(server, args, 0);
		}
		catch(NoSuchFieldException ex)
		{
			player = ess.getOfflineUser(args[0]);
		}
		if (player == null)
		{
			throw new Exception(Util.i18n("playerNotFound"));
		}
		final String name = player.getName();
		if (user.isIgnoredPlayer(name)) {
			user.setIgnoredPlayer(name, false);
			user.sendMessage(Util.format("unignorePlayer", player.getName()));
		}
		else
		{
			user.setIgnoredPlayer(name, true);
			user.sendMessage(Util.format("ignorePlayer", player.getName()));
		}
	}


}
