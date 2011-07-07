package com.earth2me.essentials.commands;

import org.bukkit.Server;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;


public class Commandafk extends EssentialsCommand
{
	public Commandafk()
	{
		super("afk");
	}

	@Override
	public void run(Server server, User user, String commandLabel, String[] args) throws Exception
	{
		charge(user);

		if (args.length > 0 && user.isAuthorized("essentials.afk.others"))
		{
			User afkUser = ess.getUser(ess.getServer().matchPlayer(args[0]));
			if (afkUser != null)
			{
				toggleAfk(afkUser);
			}
		}
		else
		{
			toggleAfk(user);
		}
	}

	private final void toggleAfk(User user)
	{
		if (!user.toggleAfk())
		{
			user.sendMessage(Util.i18n("markedAsNotAway"));
			ess.broadcastMessage(user.getName(), Util.format("userIsNotAway", user.getDisplayName()));
		}
		else
		{
			user.sendMessage(Util.i18n("markedAsAway"));
			ess.broadcastMessage(user.getName(), Util.format("userIsAway", user.getDisplayName()));
		}
	}
}
