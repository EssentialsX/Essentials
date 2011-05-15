package com.earth2me.essentials.commands;

import org.bukkit.Server;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import com.earth2me.essentials.Warps;
import org.bukkit.command.CommandSender;


public class Commandwarp extends EssentialsCommand
{
	public Commandwarp()
	{
		super("warp");
	}

	@Override
	public void run(Server server, User user, String commandLabel, String[] args) throws Exception
	{

		if (args.length == 0)
		{
			if (!user.isAuthorized("essentials.warp.list"))
			{
				user.sendMessage(Util.i18n("warpListPermission"));
				return;
			}

			Warps warps = Essentials.getWarps();
			if (warps.isEmpty())
			{
				throw new Exception(Util.i18n("noWarpsDefined"));
			}
			StringBuilder sb = new StringBuilder();
			int i = 0;
			for (String warpName : warps.getWarpNames())
			{
				if (ess.getSettings().getPerWarpPermission())
				{
					if (user.isAuthorized("essentials.warp." + warpName))
					{
						if (i++ > 0) sb.append(", ");
						sb.append(warpName);
					}
				}
				else
				{
					if (i++ > 0) sb.append(", ");
					sb.append(warpName);
				}

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
				if(otherUser == null)
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
		if (ess.getSettings().getPerWarpPermission())
		{
			if (user.isAuthorized("essentials.warp." + name))
			{
				user.getTeleport().warp(name, this.getName());
				return;
			}
			user.sendMessage(Util.i18n("warpUsePermission"));
			return;
		}
		user.getTeleport().warp(name, this.getName());
	}
}