package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import org.bukkit.Server;


public class Commandsuicide extends EssentialsCommand
{
	public Commandsuicide()
	{
		super("suicide");
	}

	@Override
	public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{
		user.setHealth(0);
		user.sendMessage(Util.i18n("suicideMessage"));
		ess.broadcastMessage(user,
							 Util.format("suicideSuccess", user.getDisplayName()));
	}
}
