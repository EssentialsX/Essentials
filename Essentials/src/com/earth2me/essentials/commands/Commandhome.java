package com.earth2me.essentials.commands;

import org.bukkit.Server;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;

public class Commandhome extends EssentialsCommand
{
	public Commandhome()
	{
		super("home");
	}

	@Override
	public void run(Server server, Essentials parent, User user, String commandLabel, String[] args) throws Exception
	{
		user.canAfford(this);
		user.teleportCooldown();
		user.teleportToHome(this.getName());
	}
}
