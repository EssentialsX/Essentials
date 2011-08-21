package com.earth2me.essentials.commands;

import com.earth2me.essentials.Trade;
import org.bukkit.Server;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import com.earth2me.essentials.Warps;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


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
				user.sendMessage(Util.i18n("warpListPermission"));
				return;
			}

			Warps warps = ess.getWarps();
			if (warps.isEmpty())
			{
				throw new Exception(Util.i18n("noWarpsDefined"));
			}
			final List<String> warpNameList = new ArrayList<String>(warps.getWarpNames());
			final Iterator<String> iterator = warpNameList.iterator();
			while (iterator.hasNext())
			{
				final String warpName = iterator.next();
				if (ess.getSettings().getPerWarpPermission() && !user.isAuthorized("essentials.warp." + warpName))
				{
					iterator.remove();
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
			final StringBuilder sb = new StringBuilder();
			for (int i = 0; i < Math.min(warpNameList.size() - warpPage, WARPS_PER_PAGE); i++)
			{
				if (i > 0)
				{
					sb.append(", ");
				}
				sb.append(warpNameList.get(i + warpPage));
			}
			user.sendMessage(sb.toString());
			return;
		}
		if (args.length > 0)
		{
			User otherUser = null;
			if (args.length == 2 && user.isAuthorized("essentials.warp.otherplayers"))
			{
				otherUser = ess.getUser(server.getPlayer(args[1]));
				if (otherUser == null)
				{
					user.sendMessage(Util.i18n("playerNotFound"));
					return;
				}
				warpUser(otherUser, args[0]);
				return;
			}
			warpUser(user, args[0]);
		}
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
			user.sendMessage(Util.i18n("warpUsePermission"));
			return;
		}
		user.getTeleport().warp(name, charge);
	}
}