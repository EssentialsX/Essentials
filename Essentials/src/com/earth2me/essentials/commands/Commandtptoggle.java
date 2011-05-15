package com.earth2me.essentials.commands;

import org.bukkit.Server;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;


public class Commandtptoggle extends EssentialsCommand
{
	public Commandtptoggle()
	{
		super("tptoggle");
	}

	@Override
	public void run(Server server, User user, String commandLabel, String[] args) throws Exception
	{
		charge(user);
		user.sendMessage(user.toggleTeleportEnabled()
						 ? Util.i18n("teleportationEnabled")
						 : Util.i18n("teleportationDisabled"));
	}
}
