package com.earth2me.essentials.commands;

import org.bukkit.Server;
import com.earth2me.essentials.Essentials;
import org.bukkit.entity.Player;
import com.earth2me.essentials.User;


public class Commandrealname extends EssentialsCommand
{
	public Commandrealname()
	{
		super("realname");
	}

	@Override
	public String[] getTriggers()
	{
		return new String[] { getName(), "realnick" };
	}

	@Override
	public void run(Server server, Essentials parent, User user, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 1)
		{
			user.sendMessage("§cUsage: /whois [nickname]");
			return;
		}
		String whois = args[0].toLowerCase();
		user.charge(this);
		for (Player p : server.getOnlinePlayers())
		{
			User u = User.get(p);
			String dn = u.getDisplayName().toLowerCase();
			if (!whois.equals(dn) && !whois.equals(parent.getSettings().getNicknamePrefix() + dn) && !whois.equals(u.getName().toLowerCase())) continue;
			user.sendMessage(u.getDisplayName() + " is " + u.getName());
		}
	}
}
