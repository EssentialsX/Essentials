package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.api.IUser;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;


public class Commandtop extends EssentialsCommand
{
	@Override
	public void run(final IUser user, final String[] args) throws Exception
	{
		final int topX = user.getLocation().getBlockX();
		final int topZ = user.getLocation().getBlockZ();
		final int topY = user.getWorld().getHighestBlockYAt(topX, topZ);
		user.getTeleport().teleport(new Location(user.getWorld(), user.getLocation().getX(), topY + 1, user.getLocation().getZ()), new Trade(commandName, ess), TeleportCause.COMMAND);
		user.sendMessage(_("teleportTop"));
	}
}
