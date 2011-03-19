package com.earth2me.essentials.commands;

import org.bukkit.Server;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;


public class Commandtpdeny extends EssentialsCommand
{
	public Commandtpdeny()
	{
		super("tpdeny");
	}

	@Override
	public void run(Server server, Essentials parent, User user, String commandLabel, String[] args) throws Exception
	{
		User p = parent.tpcRequests.get(user);
		if (p == null) throw new Exception("You do not have a pending request.");
		parent.tpcRequests.remove(user);
		
		if (parent.tpcHere.get(user))
		{
			user.charge(this);
			user.sendMessage("§7Teleport request denied.");
			p.sendMessage("§7Teleport request denied.");
		    parent.tpcHere.remove(user);
		}
		else
		{
			user.charge(this);
			user.sendMessage("§7Teleport request denied.");
			p.sendMessage("§7Teleport request denied.");
			parent.tpcRequests.remove(user);
		}
	}
}
