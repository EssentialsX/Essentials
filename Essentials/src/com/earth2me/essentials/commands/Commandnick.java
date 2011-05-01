package com.earth2me.essentials.commands;

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
	public void run(Server server, User user, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}

		if (args.length > 1)
		{
			if (!user.isAuthorized("essentials.nick.others"))
			{
				user.sendMessage("§cYou do not have permission to change the nickname of others");
				return;
			}

			setOthersNickname(server, user, args);
			return;
		}

		
		String nick = args[0];
		if ("off".equalsIgnoreCase(nick) || user.getName().equalsIgnoreCase(nick))
		{
			user.setDisplayName(user.getName());
			user.setNickname(null);
			user.sendMessage("§7You no longer have a nickname.");
			return;
		}

		if (nick.matches("[^a-zA-Z_0-9]"))
		{
			user.sendMessage("§cNicknames must be alphanumeric.");
			return;
		}

		for (Player p : server.getOnlinePlayers())
		{
			if (user == p)
			{
				continue;
			}
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
		user.setDisplayName(ess.getConfiguration().getString("nickname-prefix", "~") + nick);
		user.setNickname(nick);
		user.sendMessage("§7Your nickname is now §c" + user.getDisplayName() + "§7.");
	}

	@Override
	public void run(Server server, CommandSender sender, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 2)
		{
			throw new NotEnoughArgumentsException();
		}
		
		setOthersNickname(server, sender, args);
		
	}

	private void setOthersNickname(Server server, CommandSender sender, String[] args) throws Exception
	{
		User target = getPlayer(server, args, 0);
		String nick = args[1];
		if ("off".equalsIgnoreCase(nick) || target.getName().equalsIgnoreCase(nick))
		{
			target.setDisplayName(target.getName());
			target.setNickname(null);
			target.sendMessage("§7You no longer have a nickname.");
		}
		else
		{
			target.setDisplayName(ess.getSettings().getNicknamePrefix() + nick);
			target.setNickname(nick);
			target.sendMessage("§7Your nickname is now §c" + target.getDisplayName() + "§7.");
		}
		sender.sendMessage("Nickname changed.");
	}
}
