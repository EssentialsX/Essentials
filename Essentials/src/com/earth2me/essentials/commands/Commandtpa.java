package com.earth2me.essentials.commands;

import org.bukkit.Server;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;


public class Commandtpa extends EssentialsCommand
{
	public Commandtpa()
	{
		super("tpa");
	}

	@Override
	public void run(Server server, Essentials parent, User player, String commandLabel, String[] args) throws Exception
	{
		if(args.length < 1)
		{
			player.sendMessage("§cUsage: /tpa [playername]");
			return;
		}

		User p = getPlayer(server, args, 0);
		if (!p.isTeleEnabled()) throw new Exception(p.getDisplayName() + " has teleportation disabled.");
		player.charge(this);
		parent.tpcRequests.put(p, player);
		parent.tpcHere.put(p, false);
		p.sendMessage("§c" + player.getDisplayName() + "§c has requested to teleport to you.");
		p.sendMessage("§7To teleport, type §c/tpaccept§7.");
		p.sendMessage("§7To deny this request, type §c/tpdeny§7.");
		player.sendMessage("§7Request sent to " + p.getDisplayName() + "§7.");
	}
}
