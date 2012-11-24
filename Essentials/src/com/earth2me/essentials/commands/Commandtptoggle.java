package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.User;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class Commandtptoggle extends EssentialsCommand
{
	public Commandtptoggle()
	{
		super("tptoggle");
	}

	@Override
	public void run(final Server server, final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}

		toggleOtherPlayers(server, sender, args);
	}

	@Override
	public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length > 0 && args[0].trim().length() > 2 && user.isAuthorized("essentials.tptoggle.others"))
		{
			toggleOtherPlayers(server, user, args);
			return;
		}

		user.sendMessage(user.toggleTeleportEnabled() ? _("teleportationEnabled") : _("teleportationDisabled"));
	}

	private void toggleOtherPlayers(final Server server, final CommandSender sender, final String[] args)
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
					player.setTeleportEnabled(true);
				}
				else
				{
					player.setTeleportEnabled(false);
				}
			}
			else
			{
				player.toggleTeleportEnabled();
			}

			final boolean enabled = player.isTeleportEnabled();


			player.sendMessage(enabled ? _("teleportationEnabled") : _("teleportationDisabled"));
			sender.sendMessage(enabled ? _("teleportationEnabledFor", matchPlayer.getDisplayName()) : _("teleportationDisabledFor", matchPlayer.getDisplayName()));
		}
	}
}
