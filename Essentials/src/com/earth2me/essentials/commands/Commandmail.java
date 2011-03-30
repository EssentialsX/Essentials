package com.earth2me.essentials.commands;

import java.util.List;
import org.bukkit.Server;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;


public class Commandmail extends EssentialsCommand
{
	public Commandmail()
	{
		super("mail");
	}

	@Override
	public void run(Server server, Essentials parent, User user, String commandLabel, String[] args) throws Exception
	{
		if (args.length >= 1 && "read".equalsIgnoreCase(args[0]))
		{
			List<String> mail = Essentials.readMail(user);
			if (mail.isEmpty())
			{
				user.sendMessage("§cYou do not have any mail!");
				return;
			}
			for (String s : mail) user.sendMessage(s);
			user.sendMessage("§cTo mark your mail as read, type §c/mail clear");
			return;
		}
		if(args.length >= 3 && "send".equalsIgnoreCase(args[0]))
		{
			if (!user.isAuthorized("essentials.mail.send"))
			{
				user.sendMessage("§cYou do not have the §fessentials.mail.send§c permission.");
				return;
			}

			user.charge(this);
			Essentials.sendMail(user, args[1], getFinalArg(args, 2));
			user.sendMessage("§7Mail sent!");
			return;
		}
		if (args.length >= 1 && "clear".equalsIgnoreCase(args[0]))
		{
			Essentials.clearMail(user);
			user.sendMessage("§7Mail cleared!");
			return;
		}
		user.sendMessage("§7Usage: /mail [read|clear|send [to] [message]]");
	}
}
