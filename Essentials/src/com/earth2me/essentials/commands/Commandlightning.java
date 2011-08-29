package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class Commandlightning extends EssentialsCommand
{
	public Commandlightning()
	{
		super("lightning");
	}

	@Override
	public void run(Server server, CommandSender sender, String commandLabel, String[] args) throws Exception
	{

		User user = null;
		if (sender instanceof Player)
		{
			user = ess.getUser(((Player)sender));
		}
		if (args.length < 1 & user != null)
		{
			user.getWorld().strikeLightning(user.getTargetBlock(null, 600).getLocation());
			return;
		}

		if (server.matchPlayer(args[0]).isEmpty())
		{
			throw new Exception(Util.i18n("playerNotFound"));
		}

		for (Player p : server.matchPlayer(args[0]))
		{
			sender.sendMessage(Util.format("lightningUse", p.getDisplayName()));
			p.getWorld().strikeLightning(p.getLocation());
			if (!ess.getUser(p).isGodModeEnabled()) {
				p.setHealth(p.getHealth() < 5 ? 0 : p.getHealth() - 5);
			}
			if (ess.getSettings().warnOnSmite())
			{
				p.sendMessage(Util.i18n("lightningSmited"));
			}
		}
	}
}
