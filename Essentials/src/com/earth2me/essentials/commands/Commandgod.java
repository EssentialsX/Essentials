package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
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

		user.sendMessage(Util.format("godMode", (user.toggleGodModeEnabled()?  Util.i18n("godEnabled") : Util.i18n("godDisabled"))));
	}

	private void godOtherPlayers(Server server, CommandSender sender, String name)
	{
		for (Player p : server.matchPlayer(name))
		{
			User u = ess.getUser(p);
			boolean enabled = u.toggleGodModeEnabled();
			u.sendMessage(Util.format("godMode", (enabled ? Util.i18n("godEnabled") : Util.i18n("godDisabled"))));
			sender.sendMessage(Util.format("godMode",Util.format(enabled ? Util.i18n("godEnabledFor"): Util.i18n("godDisabledFor"), p.getDisplayName())));
		}
	}
}
