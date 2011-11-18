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
	public void run(Server server, User user, String commandLabel, String[] args) throws Exception
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
		p.requestTeleport(user, true);
		p.sendMessage(Util.format("teleportHereRequest", user.getDisplayName()));
		p.sendMessage(Util.i18n("typeTpaccept"));
		user.sendMessage(Util.format("requestSent", p.getDisplayName()));
	}
}
