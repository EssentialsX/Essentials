package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import org.bukkit.Server;


public class Commandpowertooltoggle extends EssentialsCommand
{
	public Commandpowertooltoggle()
	{
		super("powertooltoggle");
	}

	@Override
	protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{
		if(!user.hasPowerTools())
		{
			user.sendMessage(Util.i18n("noPowerTools"));
			return;
		}		
		user.sendMessage(user.togglePowerToolsEnabled()
						 ? Util.i18n("powerToolsEnabled")
						 : Util.i18n("powerToolsDisabled"));		
	}
		
		
}
