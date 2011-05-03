package com.earth2me.essentials.commands;

import org.bukkit.Server;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Warps;


public class Commandwarp extends EssentialsCommand
{
	public Commandwarp()
	{
		super("warp");
	}

	@Override
	public void run(Server server, User user, String commandLabel, String[] args) throws Exception
	{
		boolean perWarpPermission = ess.getSettings().getPerWarpPermission();
		if (args.length == 0)
		{
			if (!user.isAuthorized("essentials.warp.list"))
			{
				user.sendMessage("§cYou do not have Permission to list that warps.");
				return;
			}

			Warps warps = Essentials.getWarps();
			if (warps.isEmpty())
			{
				throw new Exception("No warps defined");
			}
			StringBuilder sb = new StringBuilder();
			int i = 0;
			for (String warpName : warps.getWarpNames())
			{
				if (perWarpPermission)
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

		try
		{
			if (perWarpPermission)
			{
				if (user.isAuthorized("essentials.warp." + args[0]))
				{
					user.charge(this);
					user.getTeleport().warp(args[0], this.getName());
					return;
				}
				user.sendMessage("§cYou do not have Permission to use that warp.");
				return;
			}
			user.charge(this);
			user.getTeleport().warp(args[0], this.getName());
		}
		catch (Exception ex)
		{
			user.sendMessage(ex.getMessage());
		}
	}
}
