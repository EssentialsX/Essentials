package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import org.bukkit.Location;
import org.bukkit.Server;


public class Commandtop extends EssentialsCommand
{
	public Commandtop()
	{
		super("top");
	}

	@Override
	public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{
		final int topX = user.getLocation().getBlockX();
		final int topZ = user.getLocation().getBlockZ();
		final int topY = user.getWorld().getHighestBlockYAt(topX, topZ);
		user.getTeleport().teleport(new Location(user.getWorld(), user.getLocation().getX(), topY + 1, user.getLocation().getZ()), new Trade(this.getName(), ess));
		user.sendMessage(_("teleportTop"));
	}
}
