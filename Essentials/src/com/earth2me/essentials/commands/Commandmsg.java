package com.earth2me.essentials.commands;

import java.util.List;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import com.earth2me.essentials.Console;
import com.earth2me.essentials.IReplyTo;
import com.earth2me.essentials.Util;
import org.bukkit.command.CommandSender;


public class Commandmsg extends EssentialsCommand
{
	public Commandmsg()
	{
		super("msg");
	}

	@Override
	public void run(Server server, CommandSender sender, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 2 || args[0].trim().isEmpty() || args[1].trim().isEmpty())
		{
			throw new NotEnoughArgumentsException();
		}

		String message = getFinalArg(args, 1);
		String translatedMe = Util.i18n("me");

		IReplyTo replyTo = sender instanceof Player ? ess.getUser((Player)sender) : Console.getConsoleReplyTo();
		String senderName = sender instanceof Player ? ((Player)sender).getDisplayName() : Console.NAME;

		if (args[0].equalsIgnoreCase(Console.NAME))
		{
			sender.sendMessage(Util.format("msgFormat", translatedMe, Console.NAME, message));
			CommandSender cs = Console.getCommandSender(server);
			cs.sendMessage(Util.format("msgFormat", senderName, translatedMe, message));
			replyTo.setReplyTo(cs);
			Console.getConsoleReplyTo().setReplyTo(sender);
			return;
		}

		List<Player> matches = server.matchPlayer(args[0]);

		if (matches.isEmpty())
		{
			sender.sendMessage(Util.i18n("playerNotFound"));
			return;
		}

		charge(sender);
		for (Player p : matches)
		{	
			sender.sendMessage(Util.format("msgFormat", translatedMe, p.getDisplayName(), message));
			if (sender instanceof Player && ess.getUser(p).isIgnoredPlayer(((Player)sender).getName()))
			{
				continue;
			}
			p.sendMessage(Util.format("msgFormat", senderName, translatedMe, message));
			replyTo.setReplyTo(ess.getUser(p));
			ess.getUser(p).setReplyTo(sender);
		}
	}
}
