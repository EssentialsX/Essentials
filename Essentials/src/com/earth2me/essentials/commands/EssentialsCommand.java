package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.api.IEssentials;
import com.earth2me.essentials.api.IEssentialsModule;
import com.earth2me.essentials.api.IUser;
import com.earth2me.essentials.perm.AbstractSuperpermsPermission;
import java.util.List;
import java.util.logging.Logger;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;


public abstract class EssentialsCommand extends AbstractSuperpermsPermission implements IEssentialsCommand
{
	protected transient String commandName;
	protected transient IEssentials ess;
	protected transient IEssentialsModule module;
	protected transient Server server;
	protected final static Logger logger = Logger.getLogger("Minecraft");
	private transient String permission;
	private transient Permission bukkitPerm;

	public void init(final IEssentials ess, final String commandName)
	{
		this.ess = ess;
		this.server = ess.getServer();
		this.commandName = commandName;
		this.permission = "essentials." + commandName;
	}

	@Override
	public void setEssentialsModule(final IEssentialsModule module)
	{
		this.module = module;
	}

	protected IUser getPlayer(final String[] args, final int pos) throws NoSuchFieldException, NotEnoughArgumentsException
	{
		return getPlayer(args, pos, false);
	}

	protected IUser getPlayer(final String[] args, final int pos, final boolean getOffline) throws NoSuchFieldException, NotEnoughArgumentsException
	{
		if (args.length <= pos)
		{
			throw new NotEnoughArgumentsException();
		}
		if (args[pos].isEmpty())
		{
			throw new NoSuchFieldException(_("playerNotFound"));
		}
		final IUser user = ess.getUser(args[pos]);
		if (user != null)
		{
			if (!getOffline && (!user.isOnline() || user.isHidden()))
			{
				throw new NoSuchFieldException(_("playerNotFound"));
			}
			return user;
		}
		final List<Player> matches = server.matchPlayer(args[pos]);

		if (!matches.isEmpty())
		{
			for (Player player : matches)
			{
				final IUser userMatch = ess.getUser(player);
				if (userMatch.getDisplayName().startsWith(args[pos]) && (getOffline || !userMatch.isHidden()))
				{
					return userMatch;
				}
			}
			final IUser userMatch = ess.getUser(matches.get(0));
			if (getOffline || !userMatch.isHidden())
			{
				return userMatch;
			}
		}
		throw new NoSuchFieldException(_("playerNotFound"));
	}

	@Override
	public final void run(final IUser user, final Command cmd, final String[] args) throws Exception
	{
		final Trade charge = new Trade(commandName, ess);
		charge.isAffordableFor(user);
		run(user, args);
		charge.charge(user);
	}

	protected void run(final IUser user, final String[] args) throws Exception
	{
		run((CommandSender)user.getBase(), args);
	}

	@Override
	public final void run(final CommandSender sender, final Command cmd, final String[] args) throws Exception
	{
		run(sender, args);
	}

	protected void run(final CommandSender sender, final String[] args) throws Exception
	{
		throw new Exception(_("onlyPlayers", commandName));
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

	@Override
	public String getPermission()
	{
		return permission;
	}
}
