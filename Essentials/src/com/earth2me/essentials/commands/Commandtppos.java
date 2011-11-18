package com.earth2me.essentials.commands;

import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import org.bukkit.Location;
import org.bukkit.Server;


public class Commandtppos extends EssentialsCommand
{
	public Commandtppos()
	{
		super("tppos");
	}

	@Override
	public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 3)
		{
			throw new NotEnoughArgumentsException();
		}

		final int x = Integer.parseInt(args[0]);
		final int y = Integer.parseInt(args[1]);
		final int z = Integer.parseInt(args[2]);
		final Location location = new Location(user.getWorld(), x, y, z);
		if (args.length > 3) {
			location.setYaw(Float.parseFloat(args[3]));
		}
		if (args.length > 4) {
			location.setPitch(Float.parseFloat(args[4]));
		}
		final Trade charge = new Trade(this.getName(), ess);
		charge.isAffordableFor(user);
		user.sendMessage(Util.i18n("teleporting"));
		user.getTeleport().teleport(location, charge);
		throw new NoChargeException();
	}
}