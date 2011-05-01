package com.earth2me.essentials.commands;

import org.bukkit.Server;
import com.earth2me.essentials.User;


public class Commandhome extends EssentialsCommand
{
	public Commandhome()
	{
		super("home");
	}

	@Override
	public void run(Server server, User user, String commandLabel, String[] args) throws Exception
	{
		user.canAfford(this);
		if(args.length > 0 && user.isAuthorized("essentials.home.others"))
		{
			User u = getPlayer(server, args, 0);
			user.getTeleport().home(u, this.getName());
			return;
		}
		user.getTeleport().home(this.getName());
	}
}
