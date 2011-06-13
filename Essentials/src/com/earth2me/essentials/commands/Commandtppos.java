package com.earth2me.essentials.commands;

import com.earth2me.essentials.Trade;
import org.bukkit.Location;
import org.bukkit.Server;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;


public class Commandtppos extends EssentialsCommand
{
	public Commandtppos()
	{
		super("tppos");
	}

	@Override
	public void run(Server server, User user, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 3)
		{
			throw new NotEnoughArgumentsException();
		}

		int x = Integer.parseInt(args[0]);
		int y = Integer.parseInt(args[1]);
		int z = Integer.parseInt(args[2]);
		Location l = new Location(user.getWorld(), x, y, z);
		Trade charge = new Trade(this.getName(), ess);
		charge.isAffordableFor(user);
		user.sendMessage(Util.i18n("teleporting"));
		user.getTeleport().teleport(l, charge);
	}
}