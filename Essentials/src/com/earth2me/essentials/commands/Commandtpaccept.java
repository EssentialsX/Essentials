package com.earth2me.essentials.commands;

import com.earth2me.essentials.OfflinePlayer;
import com.earth2me.essentials.Trade;
import org.bukkit.Server;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;


public class Commandtpaccept extends EssentialsCommand
{
	public Commandtpaccept()
	{
		super("tpaccept");
	}

	@Override
	public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{

		final User target = user.getTeleportRequest();
		if (target == null
			|| target.getBase() instanceof OfflinePlayer
			|| (user.isTeleportRequestHere() && !target.isAuthorized("essentials.tpahere")))
		{
			throw new Exception(Util.i18n("noPendingRequest"));
		}

		final Trade charge = new Trade(this.getName(), ess);
		if (user.isTeleportRequestHere())
		{
			charge.isAffordableFor(user);
		}
		else
		{
			charge.isAffordableFor(target);
		}
		user.sendMessage(Util.i18n("requestAccepted"));
		target.sendMessage(Util.format("requestAcceptedFrom", user.getDisplayName()));

		if (user.isTeleportRequestHere())
		{
			user.getTeleport().teleport(target, charge);
		}
		else
		{
			target.getTeleport().teleport(user, charge);
		}
		user.requestTeleport(null, false);
	}
}
