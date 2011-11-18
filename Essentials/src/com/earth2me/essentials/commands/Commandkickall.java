package com.earth2me.essentials.commands;

import com.earth2me.essentials.Util;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class Commandkickall extends EssentialsCommand
{
	public Commandkickall()
	{
		super("kickall");
	}

	@Override
	public void run(final Server server, final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		for (Player onlinePlaer : server.getOnlinePlayers())
		{
			if (sender instanceof Player && onlinePlaer.getName().equalsIgnoreCase(((Player)sender).getName()))
			{
				continue;
			}
			else
			{
				onlinePlaer.kickPlayer(args.length > 0 ? getFinalArg(args, 0) : Util.i18n("kickDefault"));
			}
		}
	}
}
