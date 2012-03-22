package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Server;
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
				user.sendMessage(_("noMail"));
				throw new NoChargeException();
			}
			for (String messages : mail)
			{
				user.sendMessage(messages);
			}
			user.sendMessage(_("mailClear"));
			return;
		}
		if (args.length >= 3 && "send".equalsIgnoreCase(args[0]))
		{
			if (!user.isAuthorized("essentials.mail.send"))
			{
				throw new Exception(_("noPerm", "essentials.mail.send"));
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
				throw new Exception(_("playerNeverOnServer", args[1]));
			}
			if (!u.isIgnoredPlayer(user.getName()))
			{
				final String mail = Util.sanitizeString(Util.stripFormat(getFinalArg(args, 2)));
				u.addMail(user.getName() + ": " + mail);
			}
			user.sendMessage(_("mailSent"));
			return;
		}
		if (args.length > 1 && "sendall".equalsIgnoreCase(args[0]))
		{
			if (!user.isAuthorized("essentials.mail.sendall"))
			{
				throw new Exception(_("noPerm", "essentials.mail.sendall"));
			}
			ess.scheduleAsyncDelayedTask(new SendAll(user.getName() + ": " + Util.stripFormat(getFinalArg(args, 1))));
			user.sendMessage(_("mailSent"));
			return;
		}
		if (args.length >= 1 && "clear".equalsIgnoreCase(args[0]))
		{
			user.setMails(null);
			user.sendMessage(_("mailCleared"));
			return;
		}
		throw new NotEnoughArgumentsException();
	}

	@Override
	protected void run(Server server, CommandSender sender, String commandLabel, String[] args) throws Exception
	{
		if (args.length >= 1 && "read".equalsIgnoreCase(args[0]))
		{
			throw new Exception(_("onlyPlayers", commandLabel + " read"));
		}
		else if (args.length >= 1 && "clear".equalsIgnoreCase(args[0]))
		{
			throw new Exception(_("onlyPlayers", commandLabel + " clear"));
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
				throw new Exception(_("playerNeverOnServer", args[1]));
			}
			u.addMail("Server: " + getFinalArg(args, 2));
			sender.sendMessage(_("mailSent"));
			return;
		}
		else if (args.length >= 1 && "sendall".equalsIgnoreCase(args[0]))
		{
			ess.scheduleAsyncDelayedTask(new SendAll("Server: " + getFinalArg(args, 2)));
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
				throw new Exception(_("playerNeverOnServer", args[0]));
			}
			u.addMail("Server: " + getFinalArg(args, 1));
			sender.sendMessage(_("mailSent"));
			return;
		}
		throw new NotEnoughArgumentsException();
	}


	private class SendAll implements Runnable
	{
		String message;

		public SendAll(String message)
		{
			this.message = message;
		}

		@Override
		public void run()
		{
			for (String username : ess.getUserMap().getAllUniqueUsers())
			{
				User user = ess.getUserMap().getUser(username);
				if (user != null)
				{
					user.addMail(message);
				}
			}
		}
	}
}
