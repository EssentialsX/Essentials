package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class Commandgod extends EssentialsCommand
{
	public Commandgod()
	{
		super("god");
	}

	@Override
	protected void run(Server server, CommandSender sender, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}

		godOtherPlayers(server, sender, args[0]);
	}

	@Override
	protected void run(Server server, User user, String commandLabel, String[] args) throws Exception
	{
		charge(user);
		if (args.length > 0 && user.isAuthorized("essentials.god.others"))
		{
			godOtherPlayers(server, user, args[0]);
			return;
		}

		user.sendMessage("§7God mode " + (user.toggleGodModeEnabled() ? "enabled." : "disabled."));
	}

	private void godOtherPlayers(Server server, CommandSender sender, String name)
	{
		for (Player p : server.matchPlayer(name))
		{
			User u = ess.getUser(p);
			boolean enabled = u.toggleGodModeEnabled();
			u.sendMessage("§7God mode " + (enabled ? "enabled." : "disabled."));
			sender.sendMessage("§7God mode " + (enabled ? "enabled for " : "disabled for ") + p.getDisplayName() + ".");
		}
	}
}
