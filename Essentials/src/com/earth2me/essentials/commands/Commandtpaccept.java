package com.earth2me.essentials.commands;

import org.bukkit.Server;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import com.earth2me.essentials.commands.EssentialsCommand;


public class Commandtpaccept extends EssentialsCommand
{
	public Commandtpaccept()
	{
		super("tpaccept");
	}

	@Override
	public void run(Server server, Essentials parent, User user, String commandLabel, String[] args) throws Exception
	{
	
		User p = parent.tpcRequests.get(user);
		if (p == null) throw new Exception("You do not have a pending request.");
		parent.tpcRequests.remove(user);
		
		if (parent.tpcHere.get(user))
		{
			user.teleportCooldown();
			user.canAfford(this);
			user.sendMessage("§7Teleport request accepted.");
			p.sendMessage("§7Teleport request accepted.");
			user.teleport(p, this.getName());
		}
		else
		{
			user.canAfford(this);
			user.sendMessage("§7Teleport request accepted.");
			p.sendMessage("§7Teleport request accepted.");
			p.teleport(user, this.getName());
		}
	}
}
