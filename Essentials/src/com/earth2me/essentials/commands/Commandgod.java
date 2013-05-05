package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.User;
import java.util.List;
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
		if (args.length > 0 && user.isAuthorized("essentials.god.others"))
		{
			if (args[0].trim().length() < 2)
			{
				throw new Exception(_("playerNotFound"));
			}
			godOtherPlayers(server, user, args);
			return;
		}
		godPlayer(user, !user.isGodModeEnabled());
		user.sendMessage(_("godMode", (user.isGodModeEnabled() ? _("enabled") : _("disabled"))));
	}

	private void godPlayer(User player, boolean enabled)
	{
		player.setGodModeEnabled(enabled);
		if (enabled && player.getHealth() != 0)
		{
			player.setHealth(player.getMaxHealth());
			player.setFoodLevel(20);
		}
	}

	private void godOtherPlayers(final Server server, final CommandSender sender, final String[] args) throws NotEnoughArgumentsException
	{
		boolean skipHidden = sender instanceof Player && !ess.getUser(sender).isAuthorized("essentials.vanish.interact");
		boolean foundUser = false;
		final List<Player> matchedPlayers = server.matchPlayer(args[0]);
		for (Player matchPlayer : matchedPlayers)
		{
			final User player = ess.getUser(matchPlayer);
			if (skipHidden && player.isHidden())
			{
				continue;
			}
			foundUser = true;
			boolean enabled;
			if (args.length > 1)
			{
				if (args[1].contains("on") || args[1].contains("ena") || args[1].equalsIgnoreCase("1"))
				{
					enabled = true;
				}
				else
				{
					enabled = false;
				}
			}
			else
			{
				enabled = !player.isGodModeEnabled();
			}

			godPlayer(player, enabled);
			player.sendMessage(_("godMode", (enabled ? _("enabled") : _("disabled"))));
			sender.sendMessage(_("godMode", _(enabled ? "godEnabledFor" : "godDisabledFor", matchPlayer.getDisplayName())));
		}
		if (!foundUser)
		{
			throw new NotEnoughArgumentsException(_("playerNotFound"));
		}
	}
}
