package com.earth2me.essentials.commands;

import org.bukkit.Server;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import org.bukkit.entity.Player;


public class Commandtpall extends EssentialsCommand
{
	public Commandtpall()
	{
		super("tpall");
	}

	@Override
	public void run(Server server, Essentials parent, User user, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 1)
		{
			user.charge(this);
			user.sendMessage("§7Teleporting...");
			for (Player player : server.getOnlinePlayers()) {
				User p = User.get(player);
				if (p == user) {
					continue;
				}
				p.teleportToNow(user);
			}
		}
		else
		{
			User p = getPlayer(server, args, 0);
			user.charge(this);
			user.sendMessage("§7Teleporting...");
			for (Player player : server.getOnlinePlayers()) {
				User u = User.get(player);
				if (p == u) {
					continue;
				}
				u.teleportToNow(p);
			}
		}
	}
}
