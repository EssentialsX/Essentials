package com.earth2me.essentials.commands;

import com.earth2me.essentials.Util;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;


public class Commandmotd extends EssentialsCommand
{
	public Commandmotd()
	{
		super("motd");
	}
	
	@Override
	public void run(Server server, CommandSender sender, String commandLabel, String[] args) throws Exception
	{
		charge(sender);
		for (String m : ess.getMotd(sender, Util.i18n("noMotd")))
		{
			sender.sendMessage(m);
		}
	}
}
