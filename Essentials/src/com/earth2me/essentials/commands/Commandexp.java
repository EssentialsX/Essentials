package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.User;
import com.earth2me.essentials.craftbukkit.SetExpFix;
import com.earth2me.essentials.utils.NumberUtil;
import java.util.List;
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
			showExp(user.getBase(), user);
		}
		else if (args.length > 1 && args[0].equalsIgnoreCase("set") && user.isAuthorized("essentials.exp.set"))
		{
			if (args.length == 3 && user.isAuthorized("essentials.exp.set.others"))
			{
				expMatch(server, user.getBase(), args[1], args[2], false);
			}
			else
			{
				setExp(user.getBase(), user, args[1], false);
			}
		}
		else if (args.length > 1 && args[0].equalsIgnoreCase("give") && user.isAuthorized("essentials.exp.give"))
		{
			if (args.length == 3 && user.isAuthorized("essentials.exp.give.others"))
			{
				expMatch(server, user.getBase(), args[1], args[2], true);
			}
			else
			{
				setExp(user.getBase(), user, args[1], true);
			}
		}
		else if (args[0].equalsIgnoreCase("show"))
		{
			if (args.length >= 2 && user.isAuthorized("essentials.exp.others"))
			{
				String match = args[1].trim();
				showMatch(server, user.getBase(), match);
			}
			else
			{
				showExp(user.getBase(), user);
			}
		}
		else
		{
			if (args.length >= 1 && NumberUtil.isInt(args[0].toLowerCase(Locale.ENGLISH).replace("l", "")) && user.isAuthorized("essentials.exp.give"))
			{
				if (args.length >= 2 && user.isAuthorized("essentials.exp.give.others"))
				{
					expMatch(server, user.getBase(), args[1], args[0], true);
				}
				else
				{
					setExp(user.getBase(), user, args[0], true);
				}
			}
			else if (args.length >= 1 && user.isAuthorized("essentials.exp.others"))
			{
				String match = args[0].trim();
				showMatch(server, user.getBase(), match);
			}
			else
			{
				showExp(user.getBase(), user);
			}
		}
	}

	@Override
	public void run(final Server server, final CommandSender sender, final String commandLabel, final String[] args) throws Exception
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
			if (args.length >= 2 && NumberUtil.isInt(args[0].toLowerCase(Locale.ENGLISH).replace("l", "")))
			{
				match = args[1].trim();
				expMatch(server, sender, match, args[0], true);
			}
			else if (args.length == 1)
			{
				match = args[0].trim();
			}
			showMatch(server, sender, match);
		}
	}

	private void showMatch(final Server server, final CommandSender sender, final String match) throws PlayerNotFoundException
	{
		boolean skipHidden = sender instanceof Player && !ess.getUser(sender).isAuthorized("essentials.vanish.interact");
		boolean foundUser = false;
		final List<Player> matchedPlayers = server.matchPlayer(match);
		for (Player matchPlayer : matchedPlayers)
		{
			final User player = ess.getUser(matchPlayer);
			if (skipHidden && player.isHidden())
			{
				continue;
			}
			foundUser = true;
			showExp(sender, player);
		}
		if (!foundUser)
		{
			throw new PlayerNotFoundException();
		}
	}

	private void expMatch(final Server server, final CommandSender sender, final String match, String amount, final boolean give) throws NotEnoughArgumentsException, PlayerNotFoundException
	{
		boolean skipHidden = sender instanceof Player && !ess.getUser(sender).isAuthorized("essentials.vanish.interact");
		boolean foundUser = false;
		final List<Player> matchedPlayers = server.matchPlayer(match);
		for (Player matchPlayer : matchedPlayers)
		{
			final User player = ess.getUser(matchPlayer);
			if (skipHidden && player.isHidden())
			{
				continue;
			}
			foundUser = true;
			setExp(sender, player, amount, give);
		}
		if (!foundUser)
		{
			throw new PlayerNotFoundException();
		}
	}

	private void showExp(final CommandSender sender, final User target)
	{
		sender.sendMessage(_("exp", target.getDisplayName(), SetExpFix.getTotalExperience(target.getBase()), target.getLevel(), SetExpFix.getExpUntilNextLevel(target.getBase())));
	}

	//TODO: Limit who can give negative exp?
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
			SetExpFix.setTotalExperience(target.getBase(), 0);
		}
		else
		{
			amount = Long.parseLong(strAmount);
			if (amount > Integer.MAX_VALUE || amount < Integer.MIN_VALUE)
			{
				throw new NotEnoughArgumentsException();
			}
		}

		if (give)
		{
			amount += SetExpFix.getTotalExperience(target.getBase());
		}
		if (amount > Integer.MAX_VALUE)
		{
			amount = (long)Integer.MAX_VALUE;
		}
		if (amount < 0l)
		{
			amount = 0l;
		}
		SetExpFix.setTotalExperience(target.getBase(), (int)amount);
		sender.sendMessage(_("expSet", target.getDisplayName(), amount));
	}
}
