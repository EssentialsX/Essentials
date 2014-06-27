package com.earth2me.essentials.commands;

import com.earth2me.essentials.*;
import static com.earth2me.essentials.I18n.tl;
import com.earth2me.essentials.utils.FormatUtil;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.logging.Logger;
import net.ess3.api.IEssentials;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;


public abstract class EssentialsCommand implements IEssentialsCommand
{
	private final transient String name;
	protected transient IEssentials ess;
	protected transient IEssentialsModule module;
	protected static final Logger logger = Logger.getLogger("Essentials");

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

	// Get online players - only show vanished if source has permission
	protected User getPlayer(final Server server, final CommandSource sender, final String[] args, final int pos) throws PlayerNotFoundException, NotEnoughArgumentsException
	{
		if (sender.isPlayer())
		{
			User user = ess.getUser(sender.getPlayer());
			return getPlayer(server, user, args, pos);
		}
		return getPlayer(server, args, pos, true, false);
	}

	// Get online players - only show vanished if source has permission
	protected User getPlayer(final Server server, final CommandSource sender, final String searchTerm) throws PlayerNotFoundException, NotEnoughArgumentsException
	{
		if (sender.isPlayer())
		{
			User user = ess.getUser(sender.getPlayer());
			return getPlayer(server, user, searchTerm, user.canInteractVanished(), false);
		}
		return getPlayer(server, searchTerm, true, false);
	}

	// Get online players - only show vanished if source has permission
	protected User getPlayer(final Server server, final User user, final String[] args, final int pos) throws PlayerNotFoundException, NotEnoughArgumentsException
	{
		return getPlayer(server, user, args, pos, user.canInteractVanished(), false);
	}

	// Get online or offline players, this method allows for raw access
	protected User getPlayer(final Server server, final String[] args, final int pos, boolean getHidden, final boolean getOffline) throws PlayerNotFoundException, NotEnoughArgumentsException
	{
		return getPlayer(server, null, args, pos, getHidden, getOffline);
	}

	private User getPlayer(final Server server, final User sourceUser, final String[] args, final int pos, boolean getHidden, final boolean getOffline) throws PlayerNotFoundException, NotEnoughArgumentsException
	{
		if (args.length <= pos)
		{
			throw new NotEnoughArgumentsException();
		}
		if (args[pos].isEmpty())
		{
			throw new PlayerNotFoundException();
		}
		return getPlayer(server, sourceUser, args[pos], getHidden, getOffline);
	}

	// Get online or offline players, this method allows for raw access
	protected User getPlayer(final Server server, final String searchTerm, boolean getHidden, final boolean getOffline) throws PlayerNotFoundException
	{
		return getPlayer(server, null, searchTerm, getHidden, getOffline);
	}

	private User getPlayer(final Server server, final User sourceUser, final String searchTerm, boolean getHidden, final boolean getOffline) throws PlayerNotFoundException
	{
		final User user;
		Player exPlayer;

		try
		{
			exPlayer = server.getPlayer(UUID.fromString(searchTerm));
		}
		catch (IllegalArgumentException ex)
		{
			exPlayer = server.getPlayer(searchTerm);
		}

		if (exPlayer != null)
		{
			user = ess.getUser(exPlayer);
		}
		else
		{
			user = ess.getUser(searchTerm);
		}

		if (user != null)
		{
			if (!getOffline && !user.getBase().isOnline())
			{
				throw new PlayerNotFoundException();
			}

			if (getHidden || canInteractWith(sourceUser, user))
			{
				return user;
			}
			throw new PlayerNotFoundException();
		}
		final List<Player> matches = server.matchPlayer(searchTerm);

		if (matches.isEmpty())
		{
			final String matchText = searchTerm.toLowerCase(Locale.ENGLISH);
			for (Player onlinePlayer : server.getOnlinePlayers())
			{
				final User userMatch = ess.getUser(onlinePlayer);

				if (getHidden || canInteractWith(sourceUser, userMatch))
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
				if (userMatch.getDisplayName().startsWith(searchTerm) && (getHidden || canInteractWith(sourceUser, userMatch)))
				{
					return userMatch;
				}
			}
			final User userMatch = ess.getUser(matches.get(0));
			if (getHidden || canInteractWith(sourceUser, userMatch))
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
		run(server, user.getSource(), commandLabel, args);
	}

	@Override
	public final void run(final Server server, final CommandSource sender, final String commandLabel, final Command cmd, final String[] args) throws Exception
	{
		run(server, sender, commandLabel, args);
	}

	protected void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception
	{
		throw new Exception(tl("onlyPlayers", commandLabel));
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

	private static boolean canInteractWith(User interactor, User interactee)
	{
		if (interactor == null)
		{
			return !interactee.isHidden();
		}

		if (interactor.equals(interactee))
		{
			return true;
		}

		return interactor.getBase().canSee(interactee.getBase());
	}
}
