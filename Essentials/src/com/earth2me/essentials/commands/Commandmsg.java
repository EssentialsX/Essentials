package com.earth2me.essentials.commands;

import com.earth2me.essentials.Console;
import com.earth2me.essentials.IReplyTo;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import java.util.List;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


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

		if (sender instanceof Player)
		{
			User user = ess.getUser(sender);
			if (user.isMuted())
			{
				throw new Exception(Util.i18n("voiceSilenced"));
			}
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
			throw new Exception(Util.i18n("playerNotFound"));
		}

		int i = 0;
		for (Player p : matches)
		{
			final User u = ess.getUser(p);
			if (u.isHidden())
			{
				i++;
			}
		}
		if (i == matches.size())
		{
			throw new Exception(Util.i18n("playerNotFound"));
		}

		for (Player p : matches)
		{
			sender.sendMessage(Util.format("msgFormat", translatedMe, p.getDisplayName(), message));
			final User u = ess.getUser(p);
			if (sender instanceof Player && (u.isIgnoredPlayer(((Player)sender).getName()) || u.isHidden()))
			{
				continue;
			}
			p.sendMessage(Util.format("msgFormat", senderName, translatedMe, message));
			replyTo.setReplyTo(ess.getUser(p));
			ess.getUser(p).setReplyTo(sender);
		}
	}
}
