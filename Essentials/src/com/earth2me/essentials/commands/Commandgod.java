package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
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
		if (args.length > 0 && args[0].trim().length() > 2 && user.isAuthorized("essentials.god.others"))
		{
			godOtherPlayers(server, user, args[0]);
			return;
		}

		user.sendMessage(_("godMode", (user.toggleGodModeEnabled() ? _("enabled") : _("disabled"))));
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
			player.sendMessage(_("godMode", (enabled ? _("enabled") : _("disabled"))));
			sender.sendMessage(_("godMode", _(enabled ? "godEnabledFor" : "godDisabledFor", matchPlayer.getDisplayName())));
		}
	}
}
