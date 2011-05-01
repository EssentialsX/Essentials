package com.earth2me.essentials.commands;

import org.bukkit.Location;
import org.bukkit.Server;
import com.earth2me.essentials.User;


public class Commandtop extends EssentialsCommand
{
	public Commandtop()
	{
		super("top");
	}

	@Override
	public void run(Server server, User user, String commandLabel, String[] args) throws Exception
	{
		int topX = user.getLocation().getBlockX();
		int topZ = user.getLocation().getBlockZ();
		int topY = user.getWorld().getHighestBlockYAt(topX, topZ);
		charge(user);
		user.getTeleport().teleport(new Location(user.getWorld(), user.getLocation().getX(), topY + 1, user.getLocation().getZ()), this.getName());
		user.sendMessage("§7Teleporting to top.");
	}
}
