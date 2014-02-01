package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import org.bukkit.Location;
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
		final User requester;
		try
		{
			requester = ess.getUser(user.getTeleportRequest());
		}
		catch (Exception ex)
		{
			throw new Exception(_("noPendingRequest"));
		}

		if (!requester.isOnline())
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
				final Location loc = user.getTpRequestLocation();
				requester.getTeleport().teleportPlayer(user, user.getTpRequestLocation(), charge, TeleportCause.COMMAND);
				requester.sendMessage(_("teleporting", loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
			}
			else
			{
				requester.getTeleport().teleport(user.getBase(), charge, TeleportCause.COMMAND);
			}
		}
		catch (Exception ex)
		{
			user.sendMessage(_("pendingTeleportCancelled"));
			ess.showError(requester.getSource(), ex, commandLabel);
		}
		user.requestTeleport(null, false);
		throw new NoChargeException();
	}

}
