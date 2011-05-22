package com.earth2me.essentials.commands;

import org.bukkit.Server;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;


public class Commandtpdeny extends EssentialsCommand
{
	public Commandtpdeny()
	{
		super("tpdeny");
	}

	@Override
	public void run(Server server, User user, String commandLabel, String[] args) throws Exception
	{
		User p = user.getTeleportRequest();
		if (p == null)
		{
			throw new Exception(Util.i18n("noPendingRequest"));
		}

		charge(user);
		user.sendMessage(Util.i18n("requestDenied"));
		p.sendMessage(Util.i18n("requestDenied"));
		user.requestTeleport(null, false);
	}
}
