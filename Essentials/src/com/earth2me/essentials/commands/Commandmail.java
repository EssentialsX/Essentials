package com.earth2me.essentials.commands;

import java.util.List;
import org.bukkit.Server;
import com.earth2me.essentials.User;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;


public class Commandmail extends EssentialsCommand
{
	public Commandmail()
	{
		super("mail");
	}

	@Override
	public void run(Server server, User user, String commandLabel, String[] args) throws Exception
	{
		if (args.length >= 1 && "read".equalsIgnoreCase(args[0]))
		{
			List<String> mail = user.getMails();
			if (mail.isEmpty())
			{
				user.sendMessage("§cYou do not have any mail!");
				return;
			}
			for (String s : mail)
			{
				user.sendMessage(s);
			}
			user.sendMessage("§cTo mark your mail as read, type §c/mail clear");
			return;
		}
		if (args.length >= 3 && "send".equalsIgnoreCase(args[0]))
		{
			if (!user.isAuthorized("essentials.mail.send"))
			{
				user.sendMessage("§cYou do not have the §fessentials.mail.send§c permission.");
				return;
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
				user.sendMessage("§cPlayer " + args[1] + " never was on this server.");
				return;
			}
			charge(user);
			u.addMail(ChatColor.stripColor(user.getDisplayName()) + ": " + getFinalArg(args, 2));
			user.sendMessage("§7Mail sent!");
			return;
		}
		if (args.length >= 1 && "clear".equalsIgnoreCase(args[0]))
		{
			user.setMails(null);
			user.sendMessage("§7Mail cleared!");
			return;
		}
		user.sendMessage("§7Usage: /mail [read|clear|send [to] [message]]");
	}
}
