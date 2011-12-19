package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.User;
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
		ess.getWarps().setWarp(args[0], loc);
		user.sendMessage(_("warpSet", args[0]));
	}
}
