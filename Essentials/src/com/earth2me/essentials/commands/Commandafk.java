package com.earth2me.essentials.commands;

import org.bukkit.Server;
import com.earth2me.essentials.User;


public class Commandafk extends EssentialsCommand
{
	public Commandafk()
	{
		super("afk");
	}

	@Override
	public void run(Server server, User user, String commandLabel, String[] args) throws Exception
	{
		charge(user);

		if (!user.toggleAfk())
		{
			user.sendMessage("§7You are no longer marked as away.");
			server.broadcastMessage("§7" + user.getDisplayName() + " is no longer AFK");
		} else {
			user.sendMessage("§7You are now marked as away.");
			server.broadcastMessage("§7" + user.getDisplayName() + " is now AFK");
		}
	}
}
