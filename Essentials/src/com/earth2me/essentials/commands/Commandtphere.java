package com.earth2me.essentials.commands;

import com.earth2me.essentials.Trade;
import org.bukkit.Server;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;


public class Commandtphere extends EssentialsCommand
{
	public Commandtphere()
	{
		super("tphere");
	}

	@Override
	public void run(Server server, User user, String commandLabel, String[] args) throws Exception
	{
		User p = getPlayer(server, args, 0);
		if (!p.isTeleportEnabled())
		{
			throw new Exception(Util.format("teleportDisabled", p.getDisplayName()));
		}
		p.getTeleport().teleport(user, new Trade(this.getName(), ess));
		user.sendMessage(Util.i18n("teleporting"));
		p.sendMessage(Util.i18n("teleporting"));
		throw new NoChargeException();
	}
}
