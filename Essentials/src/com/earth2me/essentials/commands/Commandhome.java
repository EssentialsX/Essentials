package com.earth2me.essentials.commands;

import org.bukkit.Server;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;


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
			User u;
			try
			{
				u = getPlayer(server, args, 0);
			}
			catch(NoSuchFieldException ex)
			{
				u = ess.getOfflineUser(args[0]);
			}
			if (u == null)
			{
				throw new Exception(Util.i18n("playerNotFound"));
			}
			user.getTeleport().home(u, this.getName());
			return;
		}
		user.getTeleport().home(this.getName());
	}
}
