package com.earth2me.essentials.commands;

import org.bukkit.Server;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;


public class Commandtpaccept extends EssentialsCommand
{
	public Commandtpaccept()
	{
		super("tpaccept");
	}

	@Override
	public void run(Server server, User user, String commandLabel, String[] args) throws Exception
	{

		User p = user.getTeleportRequest();
		if (p == null)
		{
			throw new Exception(Util.i18n("noPendingRequest"));
		}

		user.canAfford(this);
		user.sendMessage(Util.i18n("requestAccepted"));
		p.sendMessage(Util.i18n("requestAccepted"));
		
		if (user.isTeleportRequestHere())
		{
			user.getTeleport().teleport(p, this.getName());
		}
		else
		{
			p.getTeleport().teleport(user, this.getName());
		}
		user.requestTeleport(null, false);
	}
}
