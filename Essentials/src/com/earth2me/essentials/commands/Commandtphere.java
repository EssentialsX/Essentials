package com.earth2me.essentials.commands;

import org.bukkit.Server;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;


public class Commandtphere extends EssentialsCommand
{
	public Commandtphere()
	{
		super("tphere");
	}

	@Override
	public String[] getTriggers()
	{
		return new String[]
				{
					getName(), "telehere", "s"
				};
	}

	@Override
	public void run(Server server, Essentials parent, User player, String commandLabel, String[] args) throws Exception
	{
		User p = getPlayer(server, args, 0);
		if (!p.isTeleEnabled()) throw new Exception(p.getDisplayName() + " has teleportation disabled.");
		player.charge(this);
		p.teleportTo(player);
		player.sendMessage("§7Teleporting...");
		p.sendMessage("§7Teleporting...");
	}
}
