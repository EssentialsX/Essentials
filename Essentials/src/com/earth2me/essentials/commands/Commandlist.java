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
		boolean showHidden = true;
		if (sender instanceof Player)
		{
			showHidden = ess.getUser(sender).isAuthorized("essentials.list.hidden");
		}

		sender.sendMessage(listSummary(server, showHidden));
		Map<String, List<User>> playerList = getPlayerLists(server, showHidden);

		if (args.length > 0)
		{
			sender.sendMessage(listGroupUsers(playerList, args[0].toLowerCase()));
		}
		else
		{
			sendGroupedList(sender, commandLabel, playerList);
		}
	}

	// Produce a user summary: There are 5 out of maximum 10 players online.
	private String listSummary(final Server server, final boolean showHidden)
	{
		int playerHidden = 0;
		for (Player onlinePlayer : server.getOnlinePlayers())
		{
			if (ess.getUser(onlinePlayer).isHidden())
			{
				playerHidden++;
			}
		}

		String online;
		if (showHidden && playerHidden > 0)
		{
			online = _("listAmountHidden", server.getOnlinePlayers().length - playerHidden, playerHidden, server.getMaxPlayers());
		}
		else
		{
			online = _("listAmount", server.getOnlinePlayers().length - playerHidden, server.getMaxPlayers());
		}
		return online;
	}

	// Build the basic player list, divided by groups.
	private Map<String, List<User>> getPlayerLists(final Server server, final boolean showHidden)
	{
		Map<String, List<User>> playerList = new HashMap<String, List<User>>();
		for (Player onlinePlayer : server.getOnlinePlayers())
		{
			final User onlineUser = ess.getUser(onlinePlayer);
			if (onlineUser.isHidden() && !showHidden)
			{
				continue;
			}
			final String group = onlineUser.getGroup().toLowerCase();
			List<User> list = playerList.get(group);
			if (list == null)
			{
				list = new ArrayList<User>();
				playerList.put(group, list);
			}
			list.add(onlineUser);
		}
		return playerList;
	}

	// Output a playerlist of just a single group, /list <groupname>
	private String listGroupUsers(final Map<String, List<User>> playerList, final String groupName) throws Exception
	{
		final List<User> users = getMergedList(playerList, groupName);

		List<User> groupUsers = playerList.get(groupName);
		if (groupUsers != null && !groupUsers.isEmpty())
		{
			users.addAll(groupUsers);
		}
		if (users == null || users.isEmpty())
		{
			throw new Exception(_("groupDoesNotExist"));
		}
	
		return  outputFormat(groupName, listUsers(users));
	}

	// Handle the merging of groups
	private List<User> getMergedList(final Map<String, List<User>> playerList, final String groupName)
	{
		Set<String> configGroups = ess.getSettings().getListGroupConfig().keySet();
		final List<User> users = new ArrayList<User>();

		for (String key : configGroups)
		{
			if (key.equalsIgnoreCase(groupName))
			{
				String[] groups = ess.getSettings().getListGroupConfig().get(key).toString().trim().split(" ");
				for (String g : groups)
				{
					if (g == null || g.equals(""))
					{
						continue;
					}
					List<User> u = playerList.get(g.trim());
					if (u == null || u.isEmpty())
					{
						continue;
					}
					playerList.remove(g);
					users.addAll(u);
				}
			}
		}
		return users;
	}

	// Output the standard /list output, when no group is specififed
	private void sendGroupedList(CommandSender sender, String commandLabel, Map<String, List<User>> playerList)
	{
		final StringBuilder outputString = new StringBuilder();
		Set<String> configGroups = ess.getSettings().getListGroupConfig().keySet();
		List<String> asterisk = new ArrayList<String>();

		// Loop through the custom defined groups and display them
		for (String group : configGroups)
		{
			String groupValue = ess.getSettings().getListGroupConfig().get(group).toString().trim();
			group = group.toLowerCase();

			// If the group value is an asterisk, then skip it, and handle it later
			if (groupValue.equals("*"))
			{
				asterisk.add(group);
				continue;
			}

			// If the group value is hidden, we don't need to display it
			if (groupValue.equalsIgnoreCase("hidden"))
			{
				playerList.remove(groupValue);
				continue;
			}

			List<User> users = new ArrayList<User>();
			List<User> u = playerList.get(group);

			// If the group value is an int, then we might need to truncate it
			if (Util.isInt(groupValue))
			{
				if (u != null && !u.isEmpty())
				{
					playerList.remove(group);
					users.addAll(u);
					int limit = Integer.parseInt(groupValue);
					if (u.size() > limit)
					{						
						sender.sendMessage(outputFormat(group, _("groupNumber", u.size(), commandLabel, group)));						
					}
					else {					
						sender.sendMessage(outputFormat(group, listUsers(users)));						
					}					
					continue;
				}
			}

			users = getMergedList(playerList, group);

			// If we have no users, than we don't need to continue parsing this group
			if (users == null || users.isEmpty())
			{
				continue;
			}

			sender.sendMessage(outputFormat(group, listUsers(users)));
		}

		String[] onlineGroups = playerList.keySet().toArray(new String[0]);
		Arrays.sort(onlineGroups, String.CASE_INSENSITIVE_ORDER);

		// If we have an asterisk group, then merge all remaining groups
		if (!asterisk.isEmpty())
		{
			List<User> asteriskUsers = new ArrayList<User>();
			for (String group : onlineGroups)
			{				
				asteriskUsers.addAll(playerList.get(group));
			}
			for (String key : asterisk)
			{
				playerList.put(key, asteriskUsers);
			}
			onlineGroups = asterisk.toArray(new String[0]);
		}

		// If we have any groups remaining after the custom groups loop through and display them
		for (String group : onlineGroups)
		{
			List<User> users = playerList.get(group);

			if (ess.getPermissionsHandler().getName().equals("ConfigPermissions"))
			{
				group = _("connectedPlayers");
			}
			
			if (users == null || users.isEmpty())
			{
				continue;
			}
			
			sender.sendMessage(outputFormat(group, listUsers(users)));
		}
	}

	// Cosmetic list formatting
	private String listUsers(List<User> users)
	{
		final StringBuilder groupString = new StringBuilder();
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
		return groupString.toString();
	}

	// Build the output string
	private String outputFormat(String group, String message)
	{
		final StringBuilder outputString = new StringBuilder();
		outputString.append(_("listGroupTag", Util.replaceFormat(group)));
		outputString.append(message);
		outputString.setCharAt(0, Character.toTitleCase(outputString.charAt(0)));
		return outputString.toString();		
	}
}
