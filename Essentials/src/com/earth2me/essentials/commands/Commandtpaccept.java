package com.earth2me.essentials.commands;

import org.bukkit.Server;
import com.earth2me.essentials.User;


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
			throw new Exception("You do not have a pending request.");
		}

		if (user.isTeleportRequestHere())
		{
			user.canAfford(this);
			user.sendMessage("§7Teleport request accepted.");
			p.sendMessage("§7Teleport request accepted.");
			user.getTeleport().teleport(p, this.getName());
		}
		else
		{
			user.canAfford(this);
			user.sendMessage("§7Teleport request accepted.");
			p.sendMessage("§7Teleport request accepted.");
			p.getTeleport().teleport(user, this.getName());
		}
		user.requestTeleport(null, false);
	}
}
