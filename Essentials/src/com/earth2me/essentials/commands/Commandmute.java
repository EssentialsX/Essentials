package com.earth2me.essentials.commands;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;


public class Commandmute extends EssentialsCommand
{
	public Commandmute()
	{
		super("mute");
	}

	@Override
	public void run(Server server, Essentials parent, User user, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 1)
		{
			user.sendMessage("§7Usage: /" + commandLabel + " [player] <reason>");
			return;
		}

		String[] sects2 = args[0].split(" +");
		User p;
		try
		{
			p = User.get(server.matchPlayer(args[0]).get(0));
		}
		catch (Exception ex)
		{
			user.sendMessage("§cThat player does not exist!");
			return;
		}
	
		user.sendMessage("§7Player " + p.getName() + " " + (p.toggleMuted() ? "muted." : "unmuted."));
	}

	@Override
	public void run(Server server, Essentials parent, CommandSender sender, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 1)
		{
			sender.sendMessage("Usage: /" + commandLabel + " [player] <reason>");
			return;
		}

		String[] sects2 = args[0].split(" +");
		User p;
		try
		{
			p = User.get(server.matchPlayer(args[0]).get(0));
		}
		catch (Exception ex)
		{
			sender.sendMessage("§cThat player does not exist!");
			return;
		}

		sender.sendMessage("Player " + p.getName() + " " + (p.toggleMuted() ? "muted." : "unmuted."));

	}
}
