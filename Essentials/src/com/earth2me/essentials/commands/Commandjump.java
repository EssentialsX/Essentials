package com.earth2me.essentials.commands;

import com.earth2me.essentials.TargetBlock;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import org.bukkit.Location;
import org.bukkit.Server;


public class Commandjump extends EssentialsCommand
{
	public Commandjump()
	{
		super("jump");
	}

	//TODO: Update to use new target methods
	@Override
	public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{
		Location loc;
		final Location cloc = user.getLocation();

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

		final Trade charge = new Trade(this.getName(), ess);
		charge.isAffordableFor(user);
		user.getTeleport().teleport(loc, charge);
	}
}
