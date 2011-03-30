package com.earth2me.essentials.commands;

import org.bukkit.Server;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import com.earth2me.essentials.commands.EssentialsCommand;


public class Commandtpo extends EssentialsCommand
{
	public Commandtpo()
	{
		super("tpo");
	}

	@Override
	public void run(Server server, Essentials parent, User user, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 1)
		{
			user.sendMessage("§c Usage: /tpo [playername]");
		}
		else
		{
			//Just basically the old tp command
			User p = getPlayer(server, args, 0);
			user.teleportCooldown();
			user.charge(this);
			user.teleportToNow(p);
			user.sendMessage("§7Teleporting...");
		}

	}
}
