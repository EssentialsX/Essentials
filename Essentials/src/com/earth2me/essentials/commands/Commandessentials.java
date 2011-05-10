package com.earth2me.essentials.commands;

import com.earth2me.essentials.Util;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;


public class Commandessentials extends EssentialsCommand
{
	public Commandessentials()
	{
		super("essentials");
	}

	@Override
	public void run(Server server, CommandSender sender, String commandLabel, String[] args) throws Exception
	{
		ess.reload();
		charge(sender);
		sender.sendMessage(Util.format("essentialsReload", ess.getDescription().getVersion()));
	}
}
