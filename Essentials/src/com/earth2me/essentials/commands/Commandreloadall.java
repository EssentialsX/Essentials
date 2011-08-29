package com.earth2me.essentials.commands;

import com.earth2me.essentials.Util;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;


public class Commandreloadall extends EssentialsCommand
{
	public Commandreloadall()
	{
		super("reloadall");
	}

	@Override
	public void run(Server server, CommandSender sender, String commandLabel, String[] args) throws Exception
	{
		server.reload();
		sender.sendMessage(Util.i18n("reloadAllPlugins"));
	}
}
