package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.OfflinePlayer;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import org.bukkit.Server;


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
			throw new Exception(_("noPendingRequest"));
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
		user.sendMessage(_("requestAccepted"));
		target.sendMessage(_("requestAcceptedFrom", user.getDisplayName()));

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
