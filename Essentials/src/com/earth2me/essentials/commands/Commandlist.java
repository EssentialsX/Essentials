package com.earth2me.essentials.commands;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import com.earth2me.essentials.Essentials;
import org.bukkit.entity.Player;
import com.earth2me.essentials.User;
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
	public void run(Server server, Essentials parent, CommandSender sender, String commandLabel, String[] args) throws Exception
	{
		User.charge(sender, this);
		StringBuilder online = new StringBuilder();
		online.append(ChatColor.BLUE).append("There are ").append(ChatColor.RED).append(server.getOnlinePlayers().length);
		online.append(ChatColor.BLUE).append(" out of a maximum ").append(ChatColor.RED).append(server.getMaxPlayers());
		online.append(ChatColor.BLUE).append(" players online.");
		sender.sendMessage(online.toString());
		
		if (Essentials.getSettings().getSortListByGroups()) {
			Map<String, List<User>> sort = new HashMap<String, List<User>>();
			for (Player p : server.getOnlinePlayers())
			{
				User u = User.get(p);
				String group = u.getGroup();
				List<User> list = sort.get(group);
				if (list == null) {
					list = new ArrayList<User>();
					sort.put(group, list);
				}
				list.add(u);
			}
			String[] groups = sort.keySet().toArray(new String[0]);
			Arrays.sort(groups, String.CASE_INSENSITIVE_ORDER);
			for (String group : groups) {
				StringBuilder groupString = new StringBuilder();
				groupString.append(group).append(": ");
				List<User> users = sort.get(group);
				Collections.sort(users);
				boolean first = true;
				for (User user : users) {
					if (!first) { 
						groupString.append(", ");
					} else {
						first = false;
					}
					if (parent.away.contains(user)) {
						groupString.append("§7[AFK]");
					}
					groupString.append(user.getDisplayName());
				}
				sender.sendMessage(groupString.toString());
			}
		} else {
			List<User> users = new ArrayList<User>();
			for (Player p : server.getOnlinePlayers())
			{
				users.add(User.get(p));
			}
			Collections.sort(users);
			
			StringBuilder onlineUsers = new StringBuilder();
			onlineUsers.append("Connected players: ");
			boolean first = true;
			for (User user : users) {
				if (!first) { 
					onlineUsers.append(", ");
				} else {
					first = false;
				}
				if (parent.away.contains(user)) {
					onlineUsers.append("§7[AFK]");
				}
				onlineUsers.append(user.getDisplayName());
			}
			sender.sendMessage(onlineUsers.toString());
		}
	}
}
