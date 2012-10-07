package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import com.earth2me.essentials.Warps;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;


public class Commandwarp extends EssentialsCommand
{
	private final static int WARPS_PER_PAGE = 20;

	public Commandwarp()
	{
		super("warp");
	}

	@Override
	public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length == 0 || args[0].matches("[0-9]+"))
		{
			if (!user.isAuthorized("essentials.warp.list"))
			{
				throw new Exception(_("warpListPermission"));
			}
			warpList(user, args);
			throw new NoChargeException();
		}
		if (args.length > 0)
		{
			//TODO: Remove 'otherplayers' permission.
			User otherUser = null;
			if (args.length == 2 && (user.isAuthorized("essentials.warp.otherplayers") || user.isAuthorized("essentials.warp.others")))
			{
				otherUser = ess.getUser(server.getPlayer(args[1]));
				if (otherUser == null)
				{
					throw new Exception(_("playerNotFound"));
				}
				warpUser(user, otherUser, args[0]);
				throw new NoChargeException();
			}
			warpUser(user, user, args[0]);
			throw new NoChargeException();
		}
	}

	@Override
	public void run(final Server server, final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 2 || Util.isInt(args[0]))
		{
			warpList(sender, args);
			throw new NoChargeException();
		}
		User otherUser = ess.getUser(server.getPlayer(args[1]));
		if (otherUser == null)
		{
			throw new Exception(_("playerNotFound"));
		}
		otherUser.getTeleport().warp(args[0], null, TeleportCause.COMMAND);
		throw new NoChargeException();

	}

	//TODO: Use one of the new text classes, like /help ?
	private void warpList(final CommandSender sender, final String[] args) throws Exception
	{
		final Warps warps = ess.getWarps();
		if (warps.isEmpty())
		{
			throw new Exception(_("noWarpsDefined"));
		}
		final List<String> warpNameList = new ArrayList<String>(warps.getWarpNames());

		if (sender instanceof User)
		{
			final Iterator<String> iterator = warpNameList.iterator();
			while (iterator.hasNext())
			{
				final String warpName = iterator.next();
				if (ess.getSettings().getPerWarpPermission() && !((User)sender).isAuthorized("essentials.warps." + warpName))
				{
					iterator.remove();
				}
			}
		}
		int page = 1;
		if (args.length > 0 && Util.isInt(args[0]))
		{
			page = Integer.parseInt(args[0]);
		}

		final int warpPage = (page - 1) * WARPS_PER_PAGE;
		final String warpList = Util.joinList(warpNameList.subList(warpPage, warpPage + Math.min(warpNameList.size() - warpPage, WARPS_PER_PAGE)));

		if (warpNameList.size() > WARPS_PER_PAGE)
		{
			sender.sendMessage(_("warpsCount", warpNameList.size(), page, (int)Math.ceil(warpNameList.size() / (double)WARPS_PER_PAGE)));
			sender.sendMessage(_("warpList", warpList));
		}
		else
		{
			sender.sendMessage(_("warps", warpList));
		}
	}

	private void warpUser(final User owner, final User user, final String name) throws Exception
	{
		final Trade chargeWarp = new Trade("warp-" + name.toLowerCase(Locale.ENGLISH).replace('_', '-'), ess);
		final Trade chargeCmd = new Trade(this.getName(), ess);
		final double fullCharge = chargeWarp.getCommandCost(user) + chargeCmd.getCommandCost(user);
		final Trade charge = new Trade(fullCharge, ess);
		charge.isAffordableFor(owner);
		if (ess.getSettings().getPerWarpPermission() && !owner.isAuthorized("essentials.warps." + name))
		{
			throw new Exception(_("warpUsePermission"));
		}
		user.getTeleport().warp(name, charge, TeleportCause.COMMAND);
	}
}