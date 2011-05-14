package com.earth2me.essentials.commands;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;


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

		User p = getPlayer(server, args, 0);

		if (p.isAuthorized("essentials.jail.exempt"))
		{
			sender.sendMessage(Util.i18n("mayNotJail"));
			return;
		}

		if (args.length >= 2 && !p.isJailed())
		{
			charge(sender);
			p.setJailed(true);
			p.sendMessage(Util.i18n("userJailed"));
			p.setJail(null);
			Essentials.getJail().sendToJail(p, args[1]);
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

		if (args.length == 2 && p.isJailed() && !args[1].equalsIgnoreCase(p.getJail()))
		{
			sender.sendMessage("§cPerson is already in jail " + p.getJail());
			return;
		}

		if (args.length >= 2 && p.isJailed() && !args[1].equalsIgnoreCase(p.getJail()))
		{
			String time = getFinalArg(args, 2);
			long timeDiff = Util.parseDateDiff(time, true);
			p.setJailTimeout(timeDiff);
			sender.sendMessage("Jail time extend to " + Util.formatDateDiff(timeDiff));
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
			p.sendMessage("§7You have been released");
			p.setJail(null);
			p.getTeleport().back();
			sender.sendMessage("§7Player " + p.getName() + " unjailed.");
		}
	}
}
