package com.earth2me.essentials.commands;

import com.earth2me.essentials.utils.StringUtil;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;


public class Commandjails extends EssentialsCommand
{
	public Commandjails()
	{
		super("jails");
	}

	@Override
	protected void run(final Server server, final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		sender.sendMessage("§7" + StringUtil.joinList(" ", ess.getJails().getList()));
	}
}
