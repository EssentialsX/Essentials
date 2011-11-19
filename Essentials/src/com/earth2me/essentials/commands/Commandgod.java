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
	protected void run(final Server server, final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}

		godOtherPlayers(server, sender, args[0]);
	}

	@Override
	protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length > 0 && user.isAuthorized("essentials.god.others"))
		{
			godOtherPlayers(server, user, args[0]);
			return;
		}

		user.sendMessage(Util.format("godMode", (user.toggleGodModeEnabled()?  Util.i18n("enabled") : Util.i18n("disabled"))));
	}

	private void godOtherPlayers(final Server server, final CommandSender sender, final String name)
	{
		for (Player matchPlayer : server.matchPlayer(name))
		{
			final User player = ess.getUser(matchPlayer);
			if (player.isHidden())
			{
				continue;
			}
			final boolean enabled = player.toggleGodModeEnabled();
			player.sendMessage(Util.format("godMode", (enabled ? Util.i18n("enabled") : Util.i18n("disabled"))));
			sender.sendMessage(Util.format("godMode",Util.format(enabled ? "godEnabledFor": "godDisabledFor", matchPlayer.getDisplayName())));
		}
	}
}
