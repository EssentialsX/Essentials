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
		final User player = getPlayer(server, args, 0);
		if (!player.isTeleportEnabled())
		{
			throw new Exception(_("teleportDisabled", player.getDisplayName()));
		}
		if (user.getWorld() != player.getWorld() && ess.getSettings().isWorldTeleportPermissions()
			&& !player.isAuthorized("essentials.world." + user.getWorld().getName()))
		{
			throw new Exception(_("noPerm", "essentials.world." + user.getWorld().getName()));
		}
		player.getTeleport().teleport(user, new Trade(this.getName(), ess), TeleportCause.COMMAND);
		user.sendMessage(_("teleporting"));
		player.sendMessage(_("teleporting"));
		throw new NoChargeException();
	}
}
