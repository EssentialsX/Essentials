package com.earth2me.essentials.commands;

import org.bukkit.Server;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;


public class Commandping extends EssentialsCommand
{
	public Commandping()
	{
		super("ping");
	}

	@Override
	public void run(Server server, User player, String commandLabel, String[] args) throws Exception
	{
		player.sendMessage(Util.i18n("pong"));
	}
}
