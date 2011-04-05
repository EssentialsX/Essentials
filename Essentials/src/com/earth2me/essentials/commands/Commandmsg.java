package com.earth2me.essentials.commands;

import java.util.List;
import org.bukkit.Server;
import com.earth2me.essentials.Essentials;
import org.bukkit.entity.Player;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Console;
import com.earth2me.essentials.IReplyTo;
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
	public void run(Server server, Essentials parent, CommandSender sender, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 2 || args[0].trim().length() == 0 || args[1].trim().length() == 0)
		{
			sender.sendMessage("§cUsage: /" + commandLabel + " [player] [message]");
			return;
		}

		String message = getFinalArg(args, 1);
		
		IReplyTo replyTo = sender instanceof Player?User.get((Player)sender):Console.getConsoleReplyTo();
		String senderName = sender instanceof Player?((Player)sender).getDisplayName():Console.NAME;
		
		if (args[0].equalsIgnoreCase(Console.NAME))
		{
			sender.sendMessage("[Me -> " + senderName + "§f] " + message);
			CommandSender cs = Console.getCommandSender(server);
			cs.sendMessage("[" + senderName + " -> Me§f] " + message);
			replyTo.setReplyTo(cs);
			Console.getConsoleReplyTo().setReplyTo(sender);
		}
		
		List<Player> matches = server.matchPlayer(args[0]);

		if (matches.isEmpty())
		{
			sender.sendMessage("§cThere are no players matching that name.");
			return;
		}

		charge(sender);
		for (Player p : matches)
		{
			sender.sendMessage("[Me -> " + p.getDisplayName() + "§f] " + message);
			p.sendMessage("[" + senderName + " -> Me§f] " + message);
			replyTo.setReplyTo(User.get(p));
			User.get(p).setReplyTo(sender);
		}
	}
}
