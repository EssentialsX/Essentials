package com.earth2me.essentials.commands;

import org.bukkit.Server;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;


public class Commandafk extends EssentialsCommand
{
	public Commandafk()
	{
		super("afk");
	}

	@Override
	public void run(Server server, Essentials parent, User user, String commandLabel, String[] args) throws Exception
	{
		user.charge(this);

		if (parent.away.contains(user))
		{
			user.sendMessage("§7You are no longer marked as away.");
			server.broadcastMessage("§7" + user.getDisplayName() + " is no longer AFK");
			parent.away.remove(user);
			return;
		}

		user.sendMessage("§7You are now marked as away.");
		server.broadcastMessage("§7" + user.getDisplayName() + " is now AFK");
		parent.away.add(user);
	}
}
