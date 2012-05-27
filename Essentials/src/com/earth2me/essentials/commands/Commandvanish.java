package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.User;
import org.bukkit.ChatColor;
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
		if (user.isVanished())
		{
			for (Player p : server.getOnlinePlayers())
			{
				p.showPlayer(user);
			}
			user.sendMessage(_("vanished"));
		}
		else
		{
			for (Player p : server.getOnlinePlayers())
			{
				if (!ess.getUser(p).isAuthorized("essentials.vanish.see"))
				{
					p.hidePlayer(user);
				}
				user.sendMessage(_("unvanished"));
			}
		}
		user.toggleVanished();
	}
}
