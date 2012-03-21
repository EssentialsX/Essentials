package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import java.util.*;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class Commandlist extends EssentialsCommand
{
	public Commandlist()
	{
		super("list");
	}

	@Override
	public void run(final Server server, final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		boolean showhidden = false;
		if (sender instanceof Player)
		{
			if (ess.getUser(sender).isAuthorized("essentials.list.hidden"))
			{
				showhidden = true;
			}
		}
		else
		{
			showhidden = true;
		}
		int playerHidden = 0;
		for (Player onlinePlayer : server.getOnlinePlayers())
		{
			if (ess.getUser(onlinePlayer).isHidden())
			{
				playerHidden++;
			}
		}

		String online;
		if (showhidden && playerHidden > 0)
		{
			online = _("listAmountHidden", server.getOnlinePlayers().length - playerHidden, playerHidden, server.getMaxPlayers());
		}
		else
		{
			online = _("listAmount", server.getOnlinePlayers().length - playerHidden, server.getMaxPlayers());
		}
		sender.sendMessage(online);

		if (ess.getSettings().getSortListByGroups())
		{
			Map<String, List<User>> sort = new HashMap<String, List<User>>();
			for (Player OnlinePlayer : server.getOnlinePlayers())
			{
				final User player = ess.getUser(OnlinePlayer);
				if (player.isHidden() && !showhidden)
				{
					continue;
				}
				final String group = player.getGroup();
				List<User> list = sort.get(group);
				if (list == null)
				{
					list = new ArrayList<User>();
					sort.put(group, list);
				}
				list.add(player);
			}
			final String[] groups = sort.keySet().toArray(new String[0]);
			Arrays.sort(groups, String.CASE_INSENSITIVE_ORDER);
			for (String group : groups)
			{
				final StringBuilder groupString = new StringBuilder();
				groupString.append(_("listGroupTag", Util.replaceColor(group)));
				final List<User> users = sort.get(group);
				Collections.sort(users);
				boolean first = true;
				for (User user : users)
				{
					if (!first)
					{
						groupString.append(", ");
					}
					else
					{
						first = false;
					}
					if (user.isAfk())
					{
						groupString.append(_("listAfkTag"));
					}
					if (user.isHidden())
					{
						groupString.append(_("listHiddenTag"));
					}
					user.setDisplayNick();
					groupString.append(user.getDisplayName());
					groupString.append("§f");
				}
				sender.sendMessage(groupString.toString());
			}
		}
		else
		{
			final List<User> users = new ArrayList<User>();
			for (Player OnlinePlayer : server.getOnlinePlayers())
			{
				final User player = ess.getUser(OnlinePlayer);
				if (player.isHidden() && !showhidden)
				{
					continue;
				}
				users.add(player);
			}
			Collections.sort(users);

			final StringBuilder onlineUsers = new StringBuilder();
			onlineUsers.append(_("connectedPlayers"));
			boolean first = true;
			for (User user : users)
			{
				if (!first)
				{
					onlineUsers.append(", ");
				}
				else
				{
					first = false;
				}
				if (user.isAfk())
				{
					onlineUsers.append(_("listAfkTag"));
				}
				if (user.isHidden())
				{
					onlineUsers.append(_("listHiddenTag"));
				}
				user.setDisplayNick();
				onlineUsers.append(user.getDisplayName());
				onlineUsers.append("§f");
			}
			sender.sendMessage(onlineUsers.toString());
		}
	}
}
