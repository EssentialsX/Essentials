package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Warps;
import org.bukkit.Location;
import org.bukkit.Server;


public class Commandsetwarp extends EssentialsCommand
{
	public Commandsetwarp()
	{
		super("setwarp");
	}

	@Override
	public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}
		
		if (args[0].matches("[0-9]+")) {
			throw new NotEnoughArgumentsException();
		}

		final Location loc = user.getLocation();
		final Warps warps = ess.getWarps();
		Location warpLoc = null;

		try
		{
			warpLoc = warps.getWarp(args[0]);
		}
		catch (WarpNotFoundException ex)
		{
		}

		if (warpLoc == null || user.hasPermission("essentials.warp.overwrite." + args[0]))
		{
			warps.setWarp(args[0], loc);
		}
		else
		{
			throw new Exception(_("warpOverwrite"));
		}
		user.sendMessage(_("warpSet", args[0]));
	}
}
