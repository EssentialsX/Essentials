package com.earth2me.essentials.commands;

import com.earth2me.essentials.OfflinePlayer;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class Commandtogglejail extends EssentialsCommand
{
	public Commandtogglejail()
	{
		super("togglejail");
	}

	@Override
	public void run(final Server server, final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}

		final User player = getPlayer(server, args, 0, true);

		if (args.length >= 2 && !player.isJailed())
		{
			if (player.getBase() instanceof OfflinePlayer)
			{
				if (sender instanceof Player
					&& !ess.getUser(sender).isAuthorized("essentials.togglejail.offline"))
				{
					sender.sendMessage(Util.i18n("mayNotJail"));
					return;
				}
			}
			else
			{
				if (player.isAuthorized("essentials.jail.exempt"))
				{
					sender.sendMessage(Util.i18n("mayNotJail"));
					return;
				}
			}
			if (!(player.getBase() instanceof OfflinePlayer))
			{
				ess.getJail().sendToJail(player, args[1]);
			}
			else
			{
				// Check if jail exists
				ess.getJail().getJail(args[1]);
			}
			player.setJailed(true);
			player.sendMessage(Util.i18n("userJailed"));
			player.setJail(null);
			player.setJail(args[1]);
			long timeDiff = 0;
			if (args.length > 2)
			{
				final String time = getFinalArg(args, 2);
				timeDiff = Util.parseDateDiff(time, true);
				player.setJailTimeout(timeDiff);
			}
			sender.sendMessage((timeDiff > 0
								? Util.format("playerJailedFor", player.getName(), Util.formatDateDiff(timeDiff))
								: Util.format("playerJailed", player.getName())));
			return;
		}

		if (args.length >= 2 && player.isJailed() && !args[1].equalsIgnoreCase(player.getJail()))
		{
			sender.sendMessage(Util.format("jailAlreadyIncarcerated", player.getJail()));
			return;
		}

		if (args.length >= 2 && player.isJailed() && args[1].equalsIgnoreCase(player.getJail()))
		{
			final String time = getFinalArg(args, 2);
			final long timeDiff = Util.parseDateDiff(time, true);
			player.setJailTimeout(timeDiff);
			sender.sendMessage(Util.format("jailSentenceExtended", Util.formatDateDiff(timeDiff)));
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
			player.sendMessage(Util.format("jailReleasedPlayerNotify"));
			player.setJail(null);
			if (!(player.getBase() instanceof OfflinePlayer))
			{
				player.getTeleport().back();
			}
			sender.sendMessage(Util.format("jailReleased", player.getName()));
		}
	}
}
