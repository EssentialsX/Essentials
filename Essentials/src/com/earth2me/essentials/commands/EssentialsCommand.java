package com.earth2me.essentials.commands;

import net.ess3.api.IEssentials;
import com.earth2me.essentials.*;
import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.utils.FormatUtil;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public abstract class EssentialsCommand implements IEssentialsCommand
{
	private final transient String name;
	protected transient IEssentials ess;
	protected transient IEssentialsModule module;
	protected final static Logger logger = Logger.getLogger("Minecraft");

	protected EssentialsCommand(final String name)
	{
		this.name = name;
	}

	@Override
	public void setEssentials(final IEssentials ess)
	{
		this.ess = ess;
	}

	@Override
	public void setEssentialsModule(final IEssentialsModule module)
	{
		this.module = module;
	}

	@Override
	public String getName()
	{
		return name;
	}

	protected User getPlayer(final Server server, final User user, final String[] args, final int pos) throws NoSuchFieldException, NotEnoughArgumentsException
	{
		return getPlayer(server, user, args, pos, user.isAuthorized("essentials.vanish.interact"), false);
	}

	protected User getPlayer(final Server server, final CommandSender sender, final String[] args, final int pos) throws NoSuchFieldException, NotEnoughArgumentsException
	{
		if (sender instanceof Player)
		{
			User user = ess.getUser(sender);
			return getPlayer(server, user, args, pos);
		}
		return getPlayer(server, null, args, pos, true, false);
	}

	protected User getPlayer(final Server server, final String[] args, final int pos, boolean getHidden, final boolean getOffline) throws NoSuchFieldException, NotEnoughArgumentsException
	{
		return getPlayer(server, null, args, pos, getHidden, getOffline);
	}

	private User getPlayer(final Server server, final User sourceUser, final String[] args, final int pos, boolean getHidden, final boolean getOffline) throws NoSuchFieldException, NotEnoughArgumentsException
	{
		if (args.length <= pos)
		{
			throw new NotEnoughArgumentsException();
		}
		if (args[pos].isEmpty())
		{
			throw new PlayerNotFoundException();
		}
		final User user = ess.getUser(args[pos]);
		if (user != null)
		{
			if (!getOffline && !user.isOnline())
			{
				throw new PlayerNotFoundException();
			}
			if (!getHidden && user.isHidden() && !user.equals(sourceUser))
			{
				throw new PlayerNotFoundException();
			}
			return user;
		}
		final List<Player> matches = server.matchPlayer(args[pos]);

		if (matches.isEmpty())
		{
			final String matchText = args[pos].toLowerCase(Locale.ENGLISH);
			for (Player onlinePlayer : server.getOnlinePlayers())
			{
				final User userMatch = ess.getUser(onlinePlayer);
				if (getHidden || !userMatch.isHidden() || userMatch.equals(sourceUser))
				{
					final String displayName = FormatUtil.stripFormat(userMatch.getDisplayName()).toLowerCase(Locale.ENGLISH);
					if (displayName.contains(matchText))
					{
						return userMatch;
					}
				}
			}
		}
		else
		{
			for (Player player : matches)
			{
				final User userMatch = ess.getUser(player);
				if (userMatch.getDisplayName().startsWith(args[pos]) && (getHidden || !userMatch.isHidden() || userMatch.equals(sourceUser)))
				{
					return userMatch;
				}
			}
			final User userMatch = ess.getUser(matches.get(0));
			if (getHidden || !userMatch.isHidden() || userMatch.equals(sourceUser))
			{
				return userMatch;
			}
		}
		throw new PlayerNotFoundException();
	}

	@Override
	public final void run(final Server server,
						  final User user,
						  final String commandLabel,
						  final Command cmd,
						  final String[] args) throws Exception
	{
		final Trade charge = new Trade(this.getName(), ess);
		charge.isAffordableFor(user);
		run(server, user, commandLabel, args);
		charge.charge(user);
	}

	protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{
		run(server, (CommandSender)(user.getBase()), commandLabel, args);
	}

	@Override
	public final void run(final Server server, final CommandSender sender, final String commandLabel, final Command cmd, final String[] args) throws Exception
	{
		run(server, sender, commandLabel, args);
	}

	protected void run(final Server server, final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		throw new Exception(_("onlyPlayers", commandLabel));
	}

	public static String getFinalArg(final String[] args, final int start)
	{
		final StringBuilder bldr = new StringBuilder();
		for (int i = start; i < args.length; i++)
		{
			if (i != start)
			{
				bldr.append(" ");
			}
			bldr.append(args[i]);
		}
		return bldr.toString();
	}
}
