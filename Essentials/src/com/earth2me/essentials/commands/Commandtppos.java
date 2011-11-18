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
		if (args.length > 3) {
			l.setYaw(Float.parseFloat(args[3]));
		}
		if (args.length > 4) {
			l.setPitch(Float.parseFloat(args[4]));
		}
		Trade charge = new Trade(this.getName(), ess);
		charge.isAffordableFor(user);
		user.sendMessage(Util.i18n("teleporting"));
		user.getTeleport().teleport(l, charge);
		throw new NoChargeException();
	}
}