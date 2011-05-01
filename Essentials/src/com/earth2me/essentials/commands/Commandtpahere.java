package com.earth2me.essentials.commands;

import org.bukkit.Server;
import com.earth2me.essentials.User;


public class Commandtpahere extends EssentialsCommand
{
	public Commandtpahere()
	{
		super("tpahere");
	}

	@Override
	public void run(Server server, User user, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}

		User p = getPlayer(server, args, 0);
		if (!p.isTeleportEnabled())
		{
			throw new Exception(p.getDisplayName() + " has teleportation disabled.");
		}
		user.charge(this);
		p.requestTeleport(user, true);
		p.sendMessage("§c" + user.getDisplayName() + "§c has requested that you teleport to him/her.");
		p.sendMessage("§7To teleport, type §c/tpaccept§7.");
		user.sendMessage("§7Request sent to " + p.getDisplayName() + "§c.");
	}
}
