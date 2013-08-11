package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.ITarget;
import com.earth2me.essentials.PlayerTarget;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import org.bukkit.Server;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;


public class Commandtpaccept extends EssentialsCommand
{
	public Commandtpaccept()
	{
		super("tpaccept");
	}

	@Override
	public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{

		final User requester = ess.getUser(user.getTeleportRequest());

		if (requester == null || !requester.isOnline())
		{
			throw new Exception(_("noPendingRequest"));
		}

		if (user.isTpRequestHere() && ((!requester.isAuthorized("essentials.tpahere") && !requester.isAuthorized("essentials.tpaall"))
									   || (user.getWorld() != requester.getWorld() && ess.getSettings().isWorldTeleportPermissions()
										   && !user.isAuthorized("essentials.worlds." + user.getWorld().getName()))))
		{
			throw new Exception(_("noPendingRequest"));
		}

		if (!user.isTpRequestHere() && (!requester.isAuthorized("essentials.tpa")
										|| (user.getWorld() != requester.getWorld() && ess.getSettings().isWorldTeleportPermissions()
											&& !user.isAuthorized("essentials.worlds." + requester.getWorld().getName()))))
		{
			throw new Exception(_("noPendingRequest"));
		}

		if (args.length > 0 && !requester.getName().contains(args[0]))
		{
			throw new Exception(_("noPendingRequest"));
		}

		long timeout = ess.getSettings().getTpaAcceptCancellation();
		if (timeout != 0 && (System.currentTimeMillis() - user.getTeleportRequestTime()) / 1000 > timeout)
		{
			user.requestTeleport(null, false);
			throw new Exception(_("requestTimedOut"));
		}

		final Trade charge = new Trade(this.getName(), ess);
		user.sendMessage(_("requestAccepted"));
		requester.sendMessage(_("requestAcceptedFrom", user.getDisplayName()));

		try
		{
			if (user.isTpRequestHere())
			{
				requester.getTeleport().teleportPlayer(user, user.getTpRequestLocation(), charge, TeleportCause.COMMAND);
			}
			else
			{
				requester.getTeleport().teleport(user.getBase(), charge, TeleportCause.COMMAND);
			}
		}
		catch (Exception ex)
		{
			user.sendMessage(_("pendingTeleportCancelled"));
			ess.showError(requester.getBase(), ex, commandLabel);
		}
		user.requestTeleport(null, false);
		throw new NoChargeException();
	}
}
