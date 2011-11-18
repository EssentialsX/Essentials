package com.earth2me.essentials.commands;

import java.util.List;
import org.bukkit.Server;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class Commandmail extends EssentialsCommand
{
	public Commandmail()
	{
		super("mail");
	}

	//TODO: Tidy this up
	@Override
	public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length >= 1 && "read".equalsIgnoreCase(args[0]))
		{
			final List<String> mail = user.getMails();
			if (mail.isEmpty())
			{
				user.sendMessage(Util.i18n("noMail"));
				throw new NoChargeException();
			}
			for (String messages : mail)
			{
				user.sendMessage(messages);
			}
			user.sendMessage(Util.i18n("mailClear"));
			return;
		}
		if (args.length >= 3 && "send".equalsIgnoreCase(args[0]))
		{
			if (!user.isAuthorized("essentials.mail.send"))
			{
				throw new Exception(Util.i18n("noMailSendPerm"));
			}

			Player player = server.getPlayer(args[1]);
			User u;
			if (player != null)
			{
				u = ess.getUser(player);
			}
			else
			{
				u = ess.getOfflineUser(args[1]);
			}
			if (u == null)
			{
				throw new Exception(Util.format("playerNeverOnServer", args[1]));
			}
			if (!u.isIgnoredPlayer(user.getName()))
			{
				u.addMail(ChatColor.stripColor(user.getDisplayName()) + ": " + getFinalArg(args, 2));
			}
			user.sendMessage(Util.i18n("mailSent"));
			return;
		}
		if (args.length >= 1 && "clear".equalsIgnoreCase(args[0]))
		{
			user.setMails(null);
			user.sendMessage(Util.i18n("mailCleared"));
			return;
		}
		throw new NotEnoughArgumentsException();
	}

	@Override
	protected void run(Server server, CommandSender sender, String commandLabel, String[] args) throws Exception
	{
		if (args.length >= 1 && "read".equalsIgnoreCase(args[0]))
		{
			throw new Exception(Util.format("onlyPlayers", commandLabel + " read"));
		}
		else if (args.length >= 1 && "clear".equalsIgnoreCase(args[0]))
		{
			throw new Exception(Util.format("onlyPlayers", commandLabel + " clear"));
		}
		else if (args.length >= 3 && "send".equalsIgnoreCase(args[0]))
		{
			Player player = server.getPlayer(args[1]);
			User u;
			if (player != null)
			{
				u = ess.getUser(player);
			}
			else
			{
				u = ess.getOfflineUser(args[1]);
			}
			if (u == null)
			{
				throw new Exception(Util.format("playerNeverOnServer", args[1]));
			}
			u.addMail("Server: " + getFinalArg(args, 2));
			sender.sendMessage(Util.i18n("mailSent"));
			return;
		}
		else if (args.length >= 2)
		{
			//allow sending from console without "send" argument, since it's the only thing the console can do
			Player player = server.getPlayer(args[0]);
			User u;
			if (player != null)
			{
				u = ess.getUser(player);
			}
			else
			{
				u = ess.getOfflineUser(args[0]);
			}
			if (u == null)
			{
				throw new Exception(Util.format("playerNeverOnServer", args[0]));
			}
			u.addMail("Server: " + getFinalArg(args, 1));
			sender.sendMessage(Util.i18n("mailSent"));
			return;
		}
		throw new NotEnoughArgumentsException();
	}
}
