package com.earth2me.essentials.commands;

import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import org.bukkit.Location;
import org.bukkit.Server;


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
		user.getTeleport().teleport(new Location(user.getWorld(), user.getLocation().getX(), topY + 1, user.getLocation().getZ()), new Trade(this.getName(), ess));
		user.sendMessage(Util.i18n("teleportTop"));
	}
}
