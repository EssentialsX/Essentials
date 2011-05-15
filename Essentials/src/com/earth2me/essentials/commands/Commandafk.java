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

		if (!user.toggleAfk())
		{
			user.sendMessage(Util.i18n("markedAsNotAway"));
			ess.broadcastMessage(user.getName(), Util.format("userIsNotAway", user.getDisplayName()));
		} else {
			user.sendMessage(Util.i18n("markedAsAway"));
			ess.broadcastMessage(user.getName(), Util.format("userIsAway", user.getDisplayName()));
		}
	}
}
