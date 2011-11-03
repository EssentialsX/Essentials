package com.earth2me.essentials.commands;

import com.earth2me.essentials.Trade;
import org.bukkit.Server;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import com.earth2me.essentials.Warps;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.bukkit.command.CommandSender;


public class Commandwarp extends EssentialsCommand
{
	private final static int WARPS_PER_PAGE = 20;

	public Commandwarp()
	{
		super("warp");
	}

	@Override
	public void run(Server server, User user, String commandLabel, String[] args) throws Exception
	{
		if (args.length == 0 || args[0].matches("[0-9]+"))
		{
			if (!user.isAuthorized("essentials.warp.list"))
			{
				throw new Exception(Util.i18n("warpListPermission"));
			}
			warpList(user, args);
			throw new NoChargeException();
		}
		if (args.length > 0)
		{
			User otherUser = null;
			if (args.length == 2 && user.isAuthorized("essentials.warp.otherplayers"))
			{
				otherUser = ess.getUser(server.getPlayer(args[1]));
				if (otherUser == null)
				{
					throw new Exception(Util.i18n("playerNotFound"));
				}
				warpUser(otherUser, args[0]);
				throw new NoChargeException();
			}
			warpUser(user, args[0]);
			throw new NoChargeException();
		}
	}

	public void run(Server server, CommandSender sender, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 2 || args[0].matches("[0-9]+"))
		{
			warpList(null, args);
			throw new NoChargeException();
		}
		User otherUser = ess.getUser(server.getPlayer(args[1]));
		if (otherUser == null)
		{
			throw new Exception(Util.i18n("playerNotFound"));
		}
		warpUser(otherUser, args[0]);
		throw new NoChargeException();

	}

	private void warpList(User user, String[] args) throws Exception
	{
		Warps warps = ess.getWarps();
		if (warps.isEmpty())
		{
			throw new Exception(Util.i18n("noWarpsDefined"));
		}
		final List<String> warpNameList = new ArrayList<String>(warps.getWarpNames());

		if (user != null)
		{
			final Iterator<String> iterator = warpNameList.iterator();
			while (iterator.hasNext())
			{
				final String warpName = iterator.next();
				if (ess.getSettings().getPerWarpPermission() && !user.isAuthorized("essentials.warp." + warpName))
				{
					iterator.remove();
				}
			}
		}
		int page = 1;
		if (args.length > 0)
		{
			page = Integer.parseInt(args[0]);
		}
		if (warpNameList.size() > WARPS_PER_PAGE)
		{
			user.sendMessage(Util.format("warpsCount", warpNameList.size(), page, (int)Math.ceil(warpNameList.size() / (double)WARPS_PER_PAGE)));
		}
		final int warpPage = (page - 1) * WARPS_PER_PAGE;
		user.sendMessage(Util.joinList(warpNameList.subList(warpPage, warpPage + Math.min(warpNameList.size() - warpPage, WARPS_PER_PAGE))));
	}

	private void warpUser(User user, String name) throws Exception
	{
		Trade charge = new Trade(this.getName(), ess);
		charge.isAffordableFor(user);
		if (ess.getSettings().getPerWarpPermission())
		{
			if (user.isAuthorized("essentials.warp." + name))
			{
				user.getTeleport().warp(name, charge);
				return;
			}
			throw new Exception(Util.i18n("warpUsePermission"));
		}
		user.getTeleport().warp(name, charge);
	}
}