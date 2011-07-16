package com.earth2me.essentials.commands;

import com.earth2me.essentials.Trade;
import org.bukkit.Location;
import org.bukkit.Server;
import com.earth2me.essentials.TargetBlock;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;


public class Commandjump extends EssentialsCommand
{
	public Commandjump()
	{
		super("jump");
	}

	@Override
	public void run(Server server, User user, String commandLabel, String[] args) throws Exception
	{
		Location loc;
		Location cloc = user.getLocation();

		try
		{
			loc = new TargetBlock(user, 100, 2.65).getTargetBlock().getLocation();
			loc.setYaw(cloc.getYaw());
			loc.setPitch(cloc.getPitch());
			loc = new TargetBlock(loc).getPreviousBlock().getLocation();
			loc.setYaw(cloc.getYaw());
			loc.setPitch(cloc.getPitch());
			loc.setY(loc.getY() + 1);
		}
		catch (NullPointerException ex)
		{
			throw new Exception(Util.i18n("jumpError"), ex);
		}

		Trade charge = new Trade(this.getName(), ess);
		charge.isAffordableFor(user);
		user.getTeleport().teleport(loc, charge);
	}
}
