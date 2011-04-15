package com.earth2me.essentials.commands;

import java.util.List;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import com.earth2me.essentials.Essentials;
import org.bukkit.entity.Player;
import com.earth2me.essentials.User;


public class Commandnick extends EssentialsCommand
{
	public Commandnick()
	{
		super("nick");
	}

	@Override
	public void run(Server server, Essentials parent, User user, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 1)
		{
			user.sendMessage("§cUsage: /" + commandLabel + " <target> [nickname]");
			return;
		}

		if (args.length > 1)
		{
			if (!user.isAuthorized("essentials.nick.others"))
			{
				user.sendMessage("§cYou do not have permission to change the nickname of others");
				return;
			}

			List<Player> matches = server.matchPlayer(args[0]);
			if (matches.isEmpty())
			{
				user.sendMessage("§cThat player does not exist.");
				return;
			}

			User target = User.get(matches.get(0));
			String nick = args[1];
			if ("off".equalsIgnoreCase(nick) || target.getName().equalsIgnoreCase(nick))
			{
				target.setDisplayName(target.getName());
				parent.saveNickname(target, target.getName());
				target.sendMessage("§7You no longer have a nickname.");
			}
			else
			{
				user.charge(this);
				target.setDisplayName(parent.getConfiguration().getString("nickname-prefix", "~") + nick);
				parent.saveNickname(target, nick);
				target.sendMessage("§7Your nickname is now §c" + target.getDisplayName() + "§7.");
			}
			user.sendMessage("§7Nickname changed.");
		}
		else
		{
			String nick = args[0];
			if ("off".equalsIgnoreCase(nick) || user.getName().equalsIgnoreCase(nick))
			{
				user.setDisplayName(user.getName());
				parent.saveNickname(user, user.getName());
				user.sendMessage("§7You no longer have a nickname.");
			}
			else
			{
				if (nick.matches("[^a-zA-Z_0-9]"))
				{
					user.sendMessage("§cNicknames must be alphanumeric.");
					return;
				}

				for (Player p : server.getOnlinePlayers())
				{
					if (user == p) continue;
					String dn = p.getDisplayName().toLowerCase();
					String n = p.getName().toLowerCase();
					String nk = nick.toLowerCase();
					if (nk.equals(dn) || nk.equals(n))
					{
						user.sendMessage("§cThat name is already in use.");
						return;
					}
				}

				user.charge(this);
				user.setDisplayName(parent.getConfiguration().getString("nickname-prefix", "~") + nick);
				parent.saveNickname(user, nick);
				user.sendMessage("§7Your nickname is now §c" + user.getDisplayName() + "§7.");
			}
		}
	}

	@Override
	public void run(Server server, Essentials parent, CommandSender sender, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 2)
		{
			sender.sendMessage("Usage: /" + commandLabel + " [target] [nickname]");
			return;
		}

		List<Player> matches = server.matchPlayer(args[0]);
		if (matches.isEmpty())
		{
			sender.sendMessage("That player does not exist.");
			return;
		}

		User target = User.get(matches.get(0));
		String nick = args[1];
		if ("off".equalsIgnoreCase(nick) || target.getName().equalsIgnoreCase(nick))
		{
			target.setDisplayName(target.getName());
			parent.saveNickname(target, target.getName());
			target.sendMessage("§7You no longer have a nickname.");
		}
		else
		{
			target.setDisplayName(parent.getConfiguration().getString("nickname-prefix", "~") + nick);
			parent.saveNickname(target, nick);
			target.sendMessage("§7Your nickname is now §c" + target.getDisplayName() + "§7.");
		}
		sender.sendMessage("Nickname changed.");
	}
}
