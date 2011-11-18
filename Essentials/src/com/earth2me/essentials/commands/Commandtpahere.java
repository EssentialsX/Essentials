package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import org.bukkit.Server;


public class Commandtpahere extends EssentialsCommand
{
	public Commandtpahere()
	{
		super("tpahere");
	}

	@Override
	public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}

		final User player = getPlayer(server, args, 0);
		if (!player.isTeleportEnabled())
		{
			throw new Exception(Util.format("teleportDisabled", player.getDisplayName()));
		}
		player.requestTeleport(user, true);
		player.sendMessage(Util.format("teleportHereRequest", user.getDisplayName()));
		player.sendMessage(Util.i18n("typeTpaccept"));
		user.sendMessage(Util.format("requestSent", player.getDisplayName()));
	}
}
