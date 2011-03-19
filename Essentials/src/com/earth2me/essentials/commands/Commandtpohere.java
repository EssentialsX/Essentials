package com.earth2me.essentials.commands;

import org.bukkit.Server;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;


public class Commandtpohere extends EssentialsCommand
{
	public Commandtpohere()
	{
		super("tpohere");
	}

	@Override
	public void run(Server server, Essentials parent, User user, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 1)
		{
			user.sendMessage("§c Usage: /tpohere [playername]");
		}
		else
		{
			//Just basically the old tphere command
			User p = getPlayer(server, args, 0);
			user.charge(this);
			p.teleportToNow(user);
			user.sendMessage("§7Teleporting...");
		}
	}
}
