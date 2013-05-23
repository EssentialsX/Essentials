package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.User;
import java.util.List;
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
		if (args.length > 0 && user.isAuthorized("essentials.tptoggle.others"))
		{
			if (args[0].trim().length() < 2)
			{
				throw new Exception(_("playerNotFound"));
			}
			toggleOtherPlayers(server, user, args);
			return;
		}

		user.sendMessage(user.toggleTeleportEnabled() ? _("teleportationEnabled") : _("teleportationDisabled"));
	}

	private void toggleOtherPlayers(final Server server, final CommandSender sender, final String[] args) throws NotEnoughArgumentsException
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
		if (!foundUser)
		{
			throw new NotEnoughArgumentsException(_("playerNotFound"));
		}
	}
}
