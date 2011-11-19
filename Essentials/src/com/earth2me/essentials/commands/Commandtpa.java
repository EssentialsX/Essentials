package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import org.bukkit.Server;


public class Commandtpa extends EssentialsCommand
{
	public Commandtpa()
	{
		super("tpa");
	}

	@Override
	public void run(Server server, User user, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}

		User player = getPlayer(server, args, 0);
		if (!player.isTeleportEnabled())
		{
			throw new Exception(Util.format("teleportDisabled", player.getDisplayName()));
		}
		if (!player.isIgnoredPlayer(user.getName()))
		{
			player.requestTeleport(user, false);
			player.sendMessage(Util.format("teleportRequest", user.getDisplayName()));
			player.sendMessage(Util.i18n("typeTpaccept"));
			player.sendMessage(Util.i18n("typeTpdeny"));
		}
		user.sendMessage(Util.format("requestSent", player.getDisplayName()));
	}
}
