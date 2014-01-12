package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import org.bukkit.Server;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;


public class Commandtphere extends EssentialsCommand
{
	public Commandtphere()
	{
		super("tphere");
	}

	@Override
	public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{
		final User player = getPlayer(server, user, args, 0);
		if (!player.isTeleportEnabled())
		{
			throw new Exception(_("teleportDisabled", player.getDisplayName()));
		}
		if (user.getWorld() != player.getWorld() && ess.getSettings().isWorldTeleportPermissions()
			&& !user.isAuthorized("essentials.worlds." + user.getWorld().getName()))
		{
			throw new Exception(_("noPerm", "essentials.worlds." + user.getWorld().getName()));
		}
		user.getTeleport().teleportPlayer(player, user.getBase(), new Trade(this.getName(), ess), TeleportCause.COMMAND);
		throw new NoChargeException();
	}
}
