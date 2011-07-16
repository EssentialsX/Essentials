package com.earth2me.essentials.spawn;

import com.earth2me.essentials.Trade;
import org.bukkit.Server;
import com.earth2me.essentials.User;
import com.earth2me.essentials.commands.EssentialsCommand;


public class Commandspawn extends EssentialsCommand
{
	public Commandspawn()
	{
		super("spawn");
	}

	@Override
	public void run(Server server, User user, String commandLabel, String[] args) throws Exception
	{
		final Trade charge = new Trade(this.getName(), ess);
		charge.isAffordableFor(user);
		user.getTeleport().respawn(ess.getSpawn(), charge);
	}
}
