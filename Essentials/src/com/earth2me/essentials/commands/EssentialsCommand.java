package com.earth2me.essentials.commands;

import com.earth2me.essentials.Trade;
import java.util.List;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.OfflinePlayer;
import org.bukkit.entity.Player;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import java.util.logging.Logger;


public abstract class EssentialsCommand implements IEssentialsCommand
{
	private final String name;
	protected IEssentials ess;
	protected final static Logger logger = Logger.getLogger("Minecraft");

	protected EssentialsCommand(String name)
	{
		this.name = name;
	}

	public void setEssentials(IEssentials ess)
	{
		this.ess = ess;
	}

	public String getName()
	{
		return name;
	}

	protected User getPlayer(Server server, String[] args, int pos) throws NoSuchFieldException, NotEnoughArgumentsException
	{
		return getPlayer(server, args, pos, false);
	}

	protected User getPlayer(Server server, String[] args, int pos, boolean getOffline) throws NoSuchFieldException, NotEnoughArgumentsException
	{
		if (args.length <= pos) throw new NotEnoughArgumentsException();
		User user = ess.getAllUsers().get(args[pos].toLowerCase());
		if (user != null)
		{
			if(!getOffline && (user.getBase() instanceof OfflinePlayer || user.isHidden()))
			{
				throw new NoSuchFieldException(Util.i18n("playerNotFound"));
			}
			return user;
		}	
		List<Player> matches = server.matchPlayer(args[pos]);

		if (matches.size() < 1)
		{
			if (!getOffline) throw new NoSuchFieldException(Util.i18n("playerNotFound"));
			User u = ess.getOfflineUser(args[pos]);
			if (u == null) throw new NoSuchFieldException(Util.i18n("playerNotFound"));
			return u;
		}

		for (Player p : matches)
		{
			final User u = ess.getUser(p);
			if (u.getDisplayName().startsWith(args[pos]) && (getOffline || !u.isHidden()))
			{
				return u;
			}
		}
		final User u = ess.getUser(matches.get(0));
		if (!getOffline && u.isHidden())
		{
			throw new NoSuchFieldException(Util.i18n("playerNotFound"));
		}
		return u;
	}

	@Override
	public final void run(Server server, User user, String commandLabel, Command cmd, String[] args) throws Exception
	{
		run(server, user, commandLabel, args);
	}

	protected void run(Server server, User user, String commandLabel, String[] args) throws Exception
	{
		run(server, (CommandSender)user.getBase(), commandLabel, args);
	}

	@Override
	public final void run(Server server, CommandSender sender, String commandLabel, Command cmd, String[] args) throws Exception
	{
		run(server, sender, commandLabel, args);
	}

	protected void run(Server server, CommandSender sender, String commandLabel, String[] args) throws Exception
	{
		throw new Exception(Util.format("onlyPlayers", commandLabel));
	}

	public static String getFinalArg(String[] args, int start)
	{
		StringBuilder bldr = new StringBuilder();
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

	protected void charge(CommandSender sender) throws Exception
	{
		if (sender instanceof Player)
		{
			Trade charge = new Trade(this.getName(), ess);
			charge.charge(ess.getUser((Player)sender));
		}
	}
}
