package com.earth2me.essentials.commands;

import org.bukkit.Server;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;


public class Commandtpahere extends EssentialsCommand
{
	public Commandtpahere()
	{
		super("tpahere");
	}

	@Override
	public void run(Server server, Essentials parent, User user, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 1)
		{
			user.sendMessage("§cUsage: /tpahere [playername]");
			return;
		}

		User p = getPlayer(server, args, 0);
		if (!p.isTeleEnabled()) throw new Exception(p.getDisplayName() + " has teleportation disabled.");
		user.charge(this);
		parent.tpcRequests.put(p, user);
		parent.tpcHere.put(p, true);
		p.sendMessage("§c" + user.getDisplayName() + "§c has requested that you teleport to him/her.");
		p.sendMessage("§7To teleport, type §c/tpaccept§7.");
		user.sendMessage("§7Request sent to " + p.getDisplayName() + "§c.");
	}
}
