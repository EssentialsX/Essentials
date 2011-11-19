package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import org.bukkit.Server;


public class Commandtpdeny extends EssentialsCommand
{
	public Commandtpdeny()
	{
		super("tpdeny");
	}

	@Override
	public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{
		final User player = user.getTeleportRequest();
		if (player == null)
		{
			throw new Exception(Util.i18n("noPendingRequest"));
		}

		user.sendMessage(Util.i18n("requestDenied"));
		player.sendMessage(Util.format("requestDeniedFrom", user.getDisplayName()));
		user.requestTeleport(null, false);
	}
}
