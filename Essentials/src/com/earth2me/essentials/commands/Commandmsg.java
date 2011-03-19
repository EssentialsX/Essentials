package com.earth2me.essentials.commands;

import java.util.List;
import org.bukkit.Server;
import com.earth2me.essentials.Essentials;
import org.bukkit.entity.Player;
import com.earth2me.essentials.User;
import org.bukkit.command.CommandSender;

public class Commandmsg extends EssentialsCommand
{
	public Commandmsg()
	{
		super("msg");
	}

	@Override
	public String[] getTriggers()
	{
		return new String[] { getName(), "m", "tell", "whisper" };
	}

	@Override
	public void run(Server server, Essentials parent, User user, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 2 || args[0].trim().length() == 0 || args[1].trim().length() == 0)
		{
			user.sendMessage("§cUsage: /" + commandLabel + " [player] [message]");
			return;
		}

		StringBuilder message = new StringBuilder();
		for (int i = 1; i < args.length; i++)
		{
			message.append(args[i]);
			message.append(' ');
		}
		
		List<Player> matches = server.matchPlayer(args[0]);

		if (matches.isEmpty())
		{
			user.sendMessage("§cThere are no players matching that name.");
			return;
		}

		user.charge(this);
		for (Player p : matches)
		{
			user.sendMessage("[Me -> " + p.getDisplayName() + "§f] " + message);
			p.sendMessage("[" + user.getDisplayName() + " -> Me§f] " + message);
			user.setReplyTo(User.get(p));
			User.get(p).setReplyTo(user);
		}
	}

	@Override
	public void run(Server server, Essentials parent, CommandSender sender, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 2 || args[0].trim().length() == 0 || args[1].trim().length() == 0)
		{
			sender.sendMessage("§cUsage: /" + commandLabel + " [player] [message]");
			return;
		}

		StringBuilder message = new StringBuilder();
		for (int i = 1; i < args.length; i++)
		{
			message.append(args[i]);
			message.append(' ');
		}
		List<Player> matches = server.matchPlayer(args[0]);

		if (matches.isEmpty())
		{
			sender.sendMessage("§cThere are no players matching that name.");
		}

		for (Player p : matches)
		{
			sender.sendMessage("[§2Me -> " + p.getDisplayName() + "§f] " + message);
			p.sendMessage("[§2{Console} -> Me§f] " + message);
		}
	}
}
