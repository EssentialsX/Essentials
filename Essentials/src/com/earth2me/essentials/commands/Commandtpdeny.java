package com.earth2me.essentials.commands;

import org.bukkit.Server;
import com.earth2me.essentials.User;


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
			throw new Exception("You do not have a pending request.");
		}

		if (user.isTeleportRequestHere())
		{
			user.charge(this);
			user.sendMessage("§7Teleport request denied.");
			p.sendMessage("§7Teleport request denied.");
		}
		else
		{
			user.charge(this);
			user.sendMessage("§7Teleport request denied.");
			p.sendMessage("§7Teleport request denied.");
		}
		user.requestTeleport(null, false);
	}
}
