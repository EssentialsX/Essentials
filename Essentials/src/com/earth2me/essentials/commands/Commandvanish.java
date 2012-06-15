package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.User;
import org.bukkit.Server;
import org.bukkit.entity.Player;


public class Commandvanish extends EssentialsCommand
{
	public Commandvanish()
	{
		super("vanish");
	}

	@Override
	protected void run(Server server, User user, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 1)
		{
			user.toggleVanished();
			if (!user.isVanished())
			{
				user.sendMessage(_("unvanished"));
			}
			else
			{
				user.sendMessage(_("vanished"));
			}
		}
		if (args.length > 0)
		{
			if (args[1].contains("on") && !user.isVanished())
			{
				user.toggleVanished();
				user.sendMessage(_("vanished"));
			}
			if (args[1].contains("off") && user.isVanished())
			{
				user.toggleVanished();
				user.sendMessage(_("unvanished"));
			}
		}
	}
}
