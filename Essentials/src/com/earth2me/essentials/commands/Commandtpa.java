package com.earth2me.essentials.commands;

import org.bukkit.Server;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;


public class Commandtpa extends EssentialsCommand
{
	public Commandtpa()
	{
		super("tpa");
	}

	@Override
	public void run(Server server, User player, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}

		User p = getPlayer(server, args, 0);
		if (!p.isTeleportEnabled())
		{
			throw new Exception(Util.format("teleportDisabled", p.getDisplayName()));
		}
		player.charge(this);
		if (!p.isIgnoredPlayer(player.getName()))
		{
			p.requestTeleport(player, false);
			p.sendMessage(Util.format("teleportRequest", player.getDisplayName()));
			p.sendMessage(Util.i18n("typeTpaccept"));
			p.sendMessage(Util.i18n("typeTpdeny"));
		}
		player.sendMessage(Util.format("requestSent", p.getDisplayName()));
	}
}
