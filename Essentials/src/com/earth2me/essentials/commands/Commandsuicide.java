package com.earth2me.essentials.commands;

import org.bukkit.Server;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;


public class Commandsuicide extends EssentialsCommand
{
	public Commandsuicide()
	{
		super("suicide");
	}

	@Override
	public void run(Server server, Essentials parent, User user, String commandLabel, String[] args) throws Exception
	{
		user.charge(this);
		user.setHealth(0);
		user.sendMessage("§cGoodbye Cruel World...");
		server.broadcastMessage("§7" + user.getDisplayName() + " took their own life" );
	}
}
