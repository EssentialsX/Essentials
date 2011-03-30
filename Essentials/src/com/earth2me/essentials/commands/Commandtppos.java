package com.earth2me.essentials.commands;

import org.bukkit.Location;
import org.bukkit.Server;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;


public class Commandtppos extends EssentialsCommand
{
	public Commandtppos()
	{
		super("tppos");
	}

	@Override
	public String[] getTriggers()
	{
		return new String[]
				{
					getName(), "tpp"
				};
	}

	@Override
	public void run(Server server, Essentials parent, User user, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 3)
		{
			user.sendMessage("§cUsage: /tppos [x] [y] [z]");
			return;
		}
		int x = Integer.parseInt(args[0]);
		int y = Integer.parseInt(args[1]);
		int z = Integer.parseInt(args[2]);
		Location l = new Location(user.getWorld(),x,y,z);
		user.canAfford(this);
		user.teleportCooldown();
		user.sendMessage("§7Teleporting...");
		user.teleport(user.getSafeDestination(l), this.getName());
	}
}