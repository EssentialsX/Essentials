package com.earth2me.essentials.commands;

import org.bukkit.Server;
import com.earth2me.essentials.User;


public class Commandtphere extends EssentialsCommand
{
	public Commandtphere()
	{
		super("tphere");
	}

	@Override
	public void run(Server server, User user, String commandLabel, String[] args) throws Exception
	{
		User p = getPlayer(server, args, 0);
		if (!p.isTeleportEnabled())
		{
			throw new Exception(p.getDisplayName() + " has teleportation disabled.");
		}
		p.getTeleport().teleport(user, commandLabel);
		user.sendMessage("§7Teleporting...");
		p.sendMessage("§7Teleporting...");
	}
}
