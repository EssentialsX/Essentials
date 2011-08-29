package com.earth2me.essentials.commands;

import com.earth2me.essentials.Util;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;


public class Commandjails extends EssentialsCommand
{
	public Commandjails()
	{
		super("jails");
	}

	@Override
	protected void run(Server server, CommandSender sender, String commandLabel, String[] args) throws Exception
	{
		sender.sendMessage("§7" + Util.joinList(" ", ess.getJail().getJails()));
	}
}
