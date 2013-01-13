package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;


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
		final float pitch = user.getLocation().getPitch();
		final float yaw = user.getLocation().getYaw();
		final Location location = new Location(user.getWorld(), topX, user.getWorld().getMaxHeight(), topZ, yaw, pitch);		
		user.getTeleport().teleport(location, new Trade(this.getName(), ess), TeleportCause.COMMAND);
		user.sendMessage(_("teleportTop"));
	}
}
