package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.api.IUser;
import org.bukkit.Location;


public class Commandsetwarp extends EssentialsCommand
{
	@Override
	public void run(final IUser user, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}

		if (args[0].matches("[0-9]+"))
		{
			throw new NotEnoughArgumentsException();
		}

		final Location loc = user.getLocation();
		ess.getWarps().setWarp(args[0], loc);
		user.sendMessage(_("warpSet", args[0]));
	}
}
