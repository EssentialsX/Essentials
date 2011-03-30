package com.earth2me.essentials.commands;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import org.bukkit.ChatColor;


public class Commandkick extends EssentialsCommand
{
	public Commandkick()
	{
		super("kick");
	}

	@Override
	public void run(Server server, Essentials parent, CommandSender sender, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 1)
		{
			sender.sendMessage(ChatColor.RED + "Usage: /" + commandLabel + " [player] <reason>");
			return;
		}

		User p;
		try
		{
			p = User.get(server.matchPlayer(args[0]).get(0));
		}
		catch (Throwable ex)
		{
			sender.sendMessage(ChatColor.RED + "That player does not exist!");
			return;
		}

		charge(sender);
		p.kickPlayer(args.length > 1 ? getFinalArg(args, 1) : "Kicked from server");

	}
}
