package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.DateUtil;
import org.bukkit.Server;


public class Commandtogglejail extends EssentialsCommand
{
	public Commandtogglejail()
	{
		super("togglejail");
	}

	@Override
	public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}

		final User player = getPlayer(server, args, 0, true, true);

		if (args.length >= 2 && !player.isJailed())
		{
			if (!player.isOnline())
			{
				if (sender.isPlayer()
					&& !ess.getUser(sender.getPlayer()).isAuthorized("essentials.togglejail.offline"))
				{
					sender.sendMessage(_("mayNotJailOffline"));
					return;
				}
			}
			else
			{
				if (player.isAuthorized("essentials.jail.exempt"))
				{
					sender.sendMessage(_("mayNotJail"));
					return;
				}
			}
			if (player.isOnline())
			{
				ess.getJails().sendToJail(player, args[1]);
			}
			else
			{
				// Check if jail exists
				ess.getJails().getJail(args[1]);
			}
			player.setJailed(true);
			player.sendMessage(_("userJailed"));
			player.setJail(null);
			player.setJail(args[1]);
			long timeDiff = 0;
			if (args.length > 2)
			{
				final String time = getFinalArg(args, 2);
				timeDiff = DateUtil.parseDateDiff(time, true);
				player.setJailTimeout(timeDiff);
			}
			sender.sendMessage((timeDiff > 0
								? _("playerJailedFor", player.getName(), DateUtil.formatDateDiff(timeDiff))
								: _("playerJailed", player.getName())));
			return;
		}

		if (args.length >= 2 && player.isJailed() && !args[1].equalsIgnoreCase(player.getJail()))
		{
			sender.sendMessage(_("jailAlreadyIncarcerated", player.getJail()));
			return;
		}

		if (args.length >= 2 && player.isJailed() && args[1].equalsIgnoreCase(player.getJail()))
		{
			final String time = getFinalArg(args, 2);
			final long timeDiff = DateUtil.parseDateDiff(time, true);
			player.setJailTimeout(timeDiff);
			sender.sendMessage(_("jailSentenceExtended", DateUtil.formatDateDiff(timeDiff)));
			return;
		}

		if (args.length == 1 || (args.length == 2 && args[1].equalsIgnoreCase(player.getJail())))
		{
			if (!player.isJailed())
			{
				throw new NotEnoughArgumentsException();
			}
			player.setJailed(false);
			player.setJailTimeout(0);
			player.sendMessage(_("jailReleasedPlayerNotify"));
			player.setJail(null);
			if (player.isOnline())
			{
				player.getTeleport().back();
			}
			sender.sendMessage(_("jailReleased", player.getName()));
		}
	}
}
