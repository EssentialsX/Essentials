package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
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
		for (Player onlinePlayer : server.getOnlinePlayers())
		{
			if (sender instanceof Player && onlinePlayer.getName().equalsIgnoreCase(((Player)sender).getName()))
			{
				continue;
			}
			else
			{
				onlinePlayer.kickPlayer(args.length > 0 ? getFinalArg(args, 0) : _("kickDefault"));
			}
		}
		sender.sendMessage(_("kickedAll"));
	}
}
