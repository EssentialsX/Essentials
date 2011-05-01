package com.earth2me.essentials.commands;

import org.bukkit.Server;
import com.earth2me.essentials.User;


public class Commandsuicide extends EssentialsCommand
{
	public Commandsuicide()
	{
		super("suicide");
	}

	@Override
	public void run(Server server, User user, String commandLabel, String[] args) throws Exception
	{
		charge(user);
		user.setHealth(0);
		user.sendMessage("§cGoodbye Cruel World...");
		server.broadcastMessage("§7" + user.getDisplayName() + " took their own life");
	}
}
