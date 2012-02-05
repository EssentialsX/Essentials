package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.api.IUser;
import com.earth2me.essentials.perm.Permissions;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class Commandgod extends EssentialsCommand
{
	@Override
	protected void run(final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}

		godOtherPlayers(sender, args[0]);
	}

	@Override
	protected void run(final IUser user, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length > 0 && !args[0].trim().isEmpty() && Permissions.GOD_OTHERS.isAuthorized(user))
		{
			godOtherPlayers(user, args[0]);
			return;
		}

		user.sendMessage(_("godMode", (user.toggleGodModeEnabled() ? _("enabled") : _("disabled"))));
	}

	private void godOtherPlayers(final CommandSender sender, final String name)
	{
		for (Player matchPlayer : server.matchPlayer(name))
		{
			final IUser player = ess.getUser(matchPlayer);
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
