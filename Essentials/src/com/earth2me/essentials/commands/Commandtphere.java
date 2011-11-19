package com.earth2me.essentials.commands;

import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import org.bukkit.Server;


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
			throw new Exception(Util.format("teleportDisabled", player.getDisplayName()));
		}
		player.getTeleport().teleport(user, new Trade(this.getName(), ess));
		user.sendMessage(Util.i18n("teleporting"));
		player.sendMessage(Util.i18n("teleporting"));
		throw new NoChargeException();
	}
}
