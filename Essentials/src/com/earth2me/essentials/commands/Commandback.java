package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import org.bukkit.Server;


public class Commandback extends EssentialsCommand
{
	public Commandback()
	{
		super("back");
	}

	@Override
	protected void run(Server server, User user, String commandLabel, String[] args) throws Exception
	{
		user.canAfford(this);
		user.sendMessage(Util.i18n("backUsageMsg"));
		user.getTeleport().back(this.getName());
	}
}
