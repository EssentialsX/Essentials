package com.earth2me.essentials.commands;

import com.earth2me.essentials.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import org.bukkit.entity.Player;


public class Commandtogglejail extends EssentialsCommand
{
	public Commandtogglejail()
	{
		super("togglejail");
	}

	@Override
	public void run(Server server, CommandSender sender, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}

		User p = getPlayer(server, args, 0, true);

		if (args.length >= 2 && !p.isJailed())
		{
			if (p.getBase() instanceof OfflinePlayer)
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
				if (p.isAuthorized("essentials.jail.exempt"))
				{
					sender.sendMessage(Util.i18n("mayNotJail"));
					return;
				}
			}
			if (!(p.getBase() instanceof OfflinePlayer))
			{
				ess.getJail().sendToJail(p, args[1]);
			}
			else
			{
				// Check if jail exists
				ess.getJail().getJail(args[1]);
			}
			p.setJailed(true);
			p.sendMessage(Util.i18n("userJailed"));
			p.setJail(null);
			p.setJail(args[1]);
			long timeDiff = 0;
			if (args.length > 2)
			{
				String time = getFinalArg(args, 2);
				timeDiff = Util.parseDateDiff(time, true);
				p.setJailTimeout(timeDiff);
			}
			sender.sendMessage((timeDiff > 0
								? Util.format("playerJailedFor", p.getName(), Util.formatDateDiff(timeDiff))
								: Util.format("playerJailed", p.getName())));
			return;
		}

		if (args.length >= 2 && p.isJailed() && !args[1].equalsIgnoreCase(p.getJail()))
		{
			sender.sendMessage(Util.format("jailAlreadyIncarcerated", p.getJail()));
			return;
		}

		if (args.length >= 2 && p.isJailed() && args[1].equalsIgnoreCase(p.getJail()))
		{
			String time = getFinalArg(args, 2);
			long timeDiff = Util.parseDateDiff(time, true);
			p.setJailTimeout(timeDiff);
			sender.sendMessage(Util.format("jailSentenceExtended", Util.formatDateDiff(timeDiff)));
			return;
		}

		if (args.length == 1 || (args.length == 2 && args[1].equalsIgnoreCase(p.getJail())))
		{
			if (!p.isJailed())
			{
				throw new NotEnoughArgumentsException();
			}
			p.setJailed(false);
			p.setJailTimeout(0);
			p.sendMessage(Util.format("jailReleasedPlayerNotify"));
			p.setJail(null);
			if (!(p.getBase() instanceof OfflinePlayer))
			{
				p.getTeleport().back();
			}
			sender.sendMessage(Util.format("jailReleased", p.getName()));
		}
	}
}
