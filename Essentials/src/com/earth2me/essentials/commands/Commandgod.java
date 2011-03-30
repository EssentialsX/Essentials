package com.earth2me.essentials.commands;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import org.bukkit.Server;
import org.bukkit.entity.Player;


public class Commandgod extends EssentialsCommand
{
	public Commandgod()
	{
		super("god");
	}

	@Override
	protected void run(Server server, Essentials parent, User user, String commandLabel, String[] args) throws Exception
	{
		if (args.length > 0 && (user.isAuthorized("essentials.god.others") || user.isOp()))
		{
			for (Player p : server.matchPlayer(args[0]))
			{
				User u = User.get(p);
				boolean enabled = u.toggleGodMode();
				u.sendMessage("§7God mode " + (enabled ? "enabled." : "disabled."));
				user.sendMessage("§7God mode " + (enabled ? "enabled for " : "disabled for ") + p.getDisplayName() + ".");
			}
			return;
		}
		user.sendMessage("§7God mode " + (user.toggleGodMode() ? "enabled." : "disabled."));
	}
}
