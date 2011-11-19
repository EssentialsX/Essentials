package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import org.bukkit.Server;


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
