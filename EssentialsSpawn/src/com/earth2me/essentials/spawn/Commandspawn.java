package com.earth2me.essentials.spawn;

import org.bukkit.Server;
import com.earth2me.essentials.Essentials;
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
		user.canAfford(this);
		user.getTeleport().respawn(Essentials.getSpawn(), this.getName());
	}
}
