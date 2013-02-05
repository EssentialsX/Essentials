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

		godOtherPlayers(server, sender, args);
	}

	@Override
	protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length > 0 && args[0].trim().length() > 2 && user.isAuthorized("essentials.god.others"))
		{
			godOtherPlayers(server, user, args);
			return;
		}
		user.setGodModeEnabled(!user.isGodModeEnabled());
		user.sendMessage(_("godMode", (user.isGodModeEnabled() ? _("enabled") : _("disabled"))));
	}

	private void godOtherPlayers(final Server server, final CommandSender sender, final String[] args)
	{
		for (Player matchPlayer : server.matchPlayer(args[0]))
		{
			final User player = ess.getUser(matchPlayer);
			if (player.isHidden())
			{
				continue;
			}

			if (args.length > 1)
			{
				if (args[1].contains("on") || args[1].contains("ena") || args[1].equalsIgnoreCase("1"))
				{
					player.setGodModeEnabled(true);
				}
				else
				{
					player.setGodModeEnabled(false);
				}
			}
			else
			{
				player.setGodModeEnabled(!player.isGodModeEnabled());
			}

			final boolean enabled = player.isGodModeEnabled();
			if (enabled)
			{
				player.setHealth(player.getMaxHealth());
				player.setFoodLevel(20);
			}

			player.sendMessage(_("godMode", (enabled ? _("enabled") : _("disabled"))));
			sender.sendMessage(_("godMode", _(enabled ? "godEnabledFor" : "godDisabledFor", matchPlayer.getDisplayName())));
		}
	}
}
