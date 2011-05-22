package com.earth2me.essentials.commands;

import com.earth2me.essentials.Charge;
import org.bukkit.Location;
import org.bukkit.Server;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;


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
		user.getTeleport().teleport(new Location(user.getWorld(), user.getLocation().getX(), topY + 1, user.getLocation().getZ()), new Charge(this));
		user.sendMessage(Util.i18n("teleportTop"));
	}
}
