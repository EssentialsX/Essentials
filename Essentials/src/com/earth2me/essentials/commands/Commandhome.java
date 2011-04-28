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
		if(args.length > 0 && user.isAuthorized("essentials.home.others"))
		{
			user.teleportToHome(this.getName(), args[0]);
			return;
		}
		user.teleportToHome(this.getName());
	}
}
