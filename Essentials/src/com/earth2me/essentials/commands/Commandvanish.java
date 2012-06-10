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
}
