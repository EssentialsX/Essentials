package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.Util;
import com.earth2me.essentials.api.IUser;
import com.earth2me.essentials.user.UserData.TimestampType;
import lombok.Cleanup;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class Commandtogglejail extends EssentialsCommand
{
	@Override
	public void run(final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}

		@Cleanup
		final IUser player = getPlayer(args, 0, true);
		player.acquireReadLock();

		if (args.length >= 2 && !player.getData().isJailed())
		{
			if (player.getBase() instanceof OfflinePlayer)
			{
				if (sender instanceof Player
					&& !ess.getUser((Player)sender).isAuthorized("essentials.togglejail.offline"))
				{
					sender.sendMessage(_("mayNotJail"));
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
			if (!(player.getBase() instanceof OfflinePlayer))
			{
				ess.getJails().sendToJail(player, args[1]);
			}
			else
			{
				// Check if jail exists
				ess.getJails().getJail(args[1]);
			}
			player.acquireWriteLock();
			player.getData().setJailed(true);
			player.sendMessage(_("userJailed"));
			player.getData().setJail(args[1]);
			long timeDiff = 0;
			if (args.length > 2)
			{
				final String time = getFinalArg(args, 2);
				timeDiff = Util.parseDateDiff(time, true);
				player.setTimestamp(TimestampType.JAIL, timeDiff);
			}
			sender.sendMessage((timeDiff > 0
								? _("playerJailedFor", player.getName(), Util.formatDateDiff(timeDiff))
								: _("playerJailed", player.getName())));
			return;
		}

		if (args.length >= 2 && player.getData().isJailed() && !args[1].equalsIgnoreCase(player.getData().getJail()))
		{
			sender.sendMessage(_("jailAlreadyIncarcerated", player.getData().getJail()));
			return;
		}

		if (args.length >= 2 && player.getData().isJailed() && args[1].equalsIgnoreCase(player.getData().getJail()))
		{
			final String time = getFinalArg(args, 2);
			final long timeDiff = Util.parseDateDiff(time, true);
			player.acquireWriteLock();
			player.setTimestamp(TimestampType.JAIL, timeDiff);
			sender.sendMessage(_("jailSentenceExtended", Util.formatDateDiff(timeDiff)));
			return;
		}

		if (args.length == 1 || (args.length == 2 && args[1].equalsIgnoreCase(player.getData().getJail())))
		{
			if (!player.getData().isJailed())
			{
				throw new NotEnoughArgumentsException();
			}
			player.acquireWriteLock();
			player.getData().setJailed(false);
			player.setTimestamp(TimestampType.JAIL, 0);
			player.sendMessage(_("jailReleasedPlayerNotify"));
			player.getData().setJail(null);
			if (!(player.getBase() instanceof OfflinePlayer))
			{
				player.getTeleport().back();
			}
			sender.sendMessage(_("jailReleased", player.getName()));
		}
	}
}
