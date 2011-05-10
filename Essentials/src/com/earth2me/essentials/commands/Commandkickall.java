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
	public void run(Server server, CommandSender sender, String commandLabel, String[] args) throws Exception
	{
		charge(sender);

		for (Player p : server.getOnlinePlayers())
		{
			if (sender instanceof Player && p.getName().equalsIgnoreCase(((Player)sender).getName()))
			{
				continue;
			}
			else
			{
				p.kickPlayer(args.length < 1 ? getFinalArg(args, 0) : Util.i18n("kickDefault"));
			}
		}
	}
}
