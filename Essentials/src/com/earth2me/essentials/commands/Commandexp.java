package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import com.earth2me.essentials.craftbukkit.SetExpFix;
import java.util.Locale;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class Commandexp extends EssentialsCommand
{
	public Commandexp()
	{
		super("exp");
	}

	@Override
	public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length == 0)
		{
			showExp(user, user);
		}
		else if (Util.isInt(args[0].toLowerCase().replace("l", ""))) // check vanilla syntax
		{
			final String lowerArgs0 = args[0].toLowerCase();
			final int lvl = Integer.parseInt(lowerArgs0.replace("l", ""));
			final boolean containsL = lowerArgs0.contains("l");
			if (args.length > 1 && user.isAuthorized("essentials.exp.give.others"))
			{
				if (containsL)
				{
					addLevel(server, user, lvl, args[1]);
				}
				else
				{
					expMatch(server, user, args[1], args[0], true);
				}
			}
			else
			{
				if (containsL)
				{
					addLevel(server, user, lvl, user.getName());
				}
				else
				{
					expMatch(server, user, user.getName(), args[0], true);
				}
			}

		}
		else if (args[0].equalsIgnoreCase("set") && user.isAuthorized("essentials.exp.set"))
		{
			if (args.length == 3 && user.isAuthorized("essentials.exp.set.others"))
			{
				expMatch(server, user, args[1], args[2], false);
			}
			else
			{
				setExp(user, user, args[1], false);
			}
		}
		else if (args[0].equalsIgnoreCase("give") && user.isAuthorized("essentials.exp.give"))
		{
			if (args.length == 3 && user.isAuthorized("essentials.exp.give.others"))
			{
				expMatch(server, user, args[1], args[2], true);
			}
			else
			{
				setExp(user, user, args[1], true);
			}
		}
		else
		{
			String match = args[0].trim();
			if (args.length == 2)
			{
				match = args[1].trim();
			}
			if (match.equalsIgnoreCase("show") || !user.isAuthorized("essentials.exp.others"))
			{
				showExp(user, user);
			}
			else
			{
				showMatch(server, user, match);
			}
		}
	}

	@Override
	public void run(final Server server, final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		final String parseLevel = args[0].toLowerCase().replace("l", "");
		if (Util.isInt(parseLevel))
		{
			final int lvl = Integer.parseInt(parseLevel);
			if (args[0].toLowerCase(Locale.ENGLISH).contains("l"))
			{
				addLevel(server, sender, lvl, args[1]);
			}
			else
			{
				expMatch(server, sender, args[1], args[0], true);
			}

		}
		else
		{
			if (args.length < 1)
			{
				throw new NotEnoughArgumentsException();
			}
			else if (args.length > 2 && args[0].equalsIgnoreCase("set"))
			{
				expMatch(server, sender, args[1], args[2], false);
			}
			else if (args.length > 2 && args[0].equalsIgnoreCase("give"))
			{
				expMatch(server, sender, args[1], args[2], true);
			}
			else
			{
				String match = args[0].trim();
				if (args.length == 2)
				{
					match = args[1].trim();
				}
				showMatch(server, sender, match);
			}
		}
	}

	private void showMatch(final Server server, final CommandSender sender, final String match) throws NotEnoughArgumentsException
	{
		boolean foundUser = false;
		for (Player matchPlayer : server.matchPlayer(match))
		{
			foundUser = true;
			final User target = ess.getUser(matchPlayer);
			showExp(sender, target);
		}
		if (!foundUser)
		{
			throw new NotEnoughArgumentsException(_("playerNotFound"));
		}
	}

	private void expMatch(final Server server, final CommandSender sender, final String match, String amount, final boolean toggle) throws NotEnoughArgumentsException
	{
		boolean foundUser = false;
		for (Player matchPlayer : server.matchPlayer(match))
		{
			final User target = ess.getUser(matchPlayer);
			setExp(sender, target, amount, toggle);
			foundUser = true;
		}
		if (!foundUser)
		{
			throw new NotEnoughArgumentsException(_("playerNotFound"));
		}
	}

	private void addLevel(final Server server, final CommandSender sender, final int level, final String target) throws NotEnoughArgumentsException
	{
		boolean foundUser = false;
		for (Player matchPlayer : server.matchPlayer(target))
		{
			final User user = ess.getUser(matchPlayer);
			final int curLevel = user.getLevel();
			final int fLevel = curLevel + level;
			if (fLevel < 0)
			{
				user.setLevel(0);
				user.setExp(0F);
			}
			else
			{
				user.setLevel(fLevel);
			}
			foundUser = true;
		}
		if (!foundUser)
		{
			throw new NotEnoughArgumentsException(_("playerNotFound"));
		}
	}

	private void showExp(final CommandSender sender, final User target)
	{
		sender.sendMessage(_("exp", target.getDisplayName(), SetExpFix.getTotalExperience(target), target.getLevel(), SetExpFix.getExpUntilNextLevel(target)));
	}

	private void setExp(final CommandSender sender, final User target, String strAmount, final boolean give) throws NotEnoughArgumentsException
	{
		long amount;
		strAmount = strAmount.toLowerCase(Locale.ENGLISH);
		if (strAmount.contains("l"))
		{
			strAmount = strAmount.replaceAll("l", "");
			int neededLevel = Integer.parseInt(strAmount);
			if (give)
			{
				neededLevel += target.getLevel();
			}
			amount = (long)SetExpFix.getExpToLevel(neededLevel);
			SetExpFix.setTotalExperience(target, 0);
		}
		else
		{
			amount = Long.parseLong(strAmount);
			if (amount < 0 || amount > Integer.MAX_VALUE)
			{
				throw new NotEnoughArgumentsException();
			}
		}

		if (give)
		{
			amount += SetExpFix.getTotalExperience(target);
		}
		if (amount > Integer.MAX_VALUE)
		{
			amount = (long)Integer.MAX_VALUE;
		}
		SetExpFix.setTotalExperience(target, (int)amount);
		sender.sendMessage(_("expSet", target.getDisplayName(), amount));
	}
}
