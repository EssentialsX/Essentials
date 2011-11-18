package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import org.bukkit.Server;


public class Commandtptoggle extends EssentialsCommand
{
	public Commandtptoggle()
	{
		super("tptoggle");
	}

	@Override
	public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{
		user.sendMessage(user.toggleTeleportEnabled()
						 ? Util.i18n("teleportationEnabled")
						 : Util.i18n("teleportationDisabled"));
	}
}
