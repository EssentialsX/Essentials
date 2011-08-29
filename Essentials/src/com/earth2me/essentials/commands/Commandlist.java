package com.earth2me.essentials.commands;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.ChatColor;


public class Commandlist extends EssentialsCommand
{
	public Commandlist()
	{
		super("list");
	}

	@Override
	public void run(Server server, CommandSender sender, String commandLabel, String[] args) throws Exception
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
		for (Player p : server.getOnlinePlayers())
		{
			if (ess.getUser(p).isHidden())
			{
				playerHidden++;
			}
		}
		//TODO: move these to messages file
		StringBuilder online = new StringBuilder();
		online.append(ChatColor.BLUE).append("There are ").append(ChatColor.RED).append(server.getOnlinePlayers().length - playerHidden);
		if (showhidden && playerHidden > 0)
		{
			online.append(ChatColor.GRAY).append("/").append(playerHidden);
		}
		online.append(ChatColor.BLUE).append(" out of a maximum ").append(ChatColor.RED).append(server.getMaxPlayers());
		online.append(ChatColor.BLUE).append(" players online.");
		sender.sendMessage(online.toString());

		if (ess.getSettings().getSortListByGroups())
		{
			Map<String, List<User>> sort = new HashMap<String, List<User>>();
			for (Player p : server.getOnlinePlayers())
			{
				User u = ess.getUser(p);
				if (u.isHidden() && !showhidden)
				{
					continue;
				}
				String group = u.getGroup();
				List<User> list = sort.get(group);
				if (list == null)
				{
					list = new ArrayList<User>();
					sort.put(group, list);
				}
				list.add(u);
			}
			String[] groups = sort.keySet().toArray(new String[0]);
			Arrays.sort(groups, String.CASE_INSENSITIVE_ORDER);
			for (String group : groups)
			{
				StringBuilder groupString = new StringBuilder();
				groupString.append(group).append(": ");
				List<User> users = sort.get(group);
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
						groupString.append("§7[AFK]§f");
					}
					if (user.isHidden())
					{
						groupString.append("§7[HIDDEN]§f");
					}
					groupString.append(user.getDisplayName());
					groupString.append("§f");
				}
				sender.sendMessage(groupString.toString());
			}
		}
		else
		{
			List<User> users = new ArrayList<User>();
			for (Player p : server.getOnlinePlayers())
			{
				final User u = ess.getUser(p);
				if (u.isHidden() && !showhidden)
				{
					continue;
				}
				users.add(u);
			}
			Collections.sort(users);

			StringBuilder onlineUsers = new StringBuilder();
			onlineUsers.append(Util.i18n("connectedPlayers"));
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
					onlineUsers.append("§7[AFK]§f");
				}
				if (user.isHidden())
				{
					onlineUsers.append("§7[HIDDEN]§f");
				}
				onlineUsers.append(user.getDisplayName());
				onlineUsers.append("§f");
			}
			sender.sendMessage(onlineUsers.toString());
		}
	}
}
