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
			sender.sendMessage(listGroupUsers(server, playerList, args[0].toLowerCase()));
		}
		else
		{
			sendGroupedList(server, sender, commandLabel, playerList);
		}
	}

	private String listSummary(Server server, boolean showHidden)
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

	private Map<String, List<User>> getPlayerLists(Server server, boolean showHidden)
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

	private String listGroupUsers(Server server, Map<String, List<User>> playerList, String groupName) throws Exception
	{
		final StringBuilder outputString = new StringBuilder();
		Set<String> configGroups = ess.getSettings().getListGroupConfig().keySet();
		final List<User> users = new ArrayList<User>();

		for (String key : configGroups)
		{
			String groupValue = ess.getSettings().getListGroupConfig().get(key).toString().trim();
			if (key.equalsIgnoreCase(groupName) && groupValue.contains(","))
			{
				String[] groups = groupValue.split(",");
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
					users.addAll(u);
				}
			}
		}
		List<User> groupUsers = playerList.get(groupName);
		if (groupUsers != null && !groupUsers.isEmpty())
		{
			users.addAll(groupUsers);
		}
		if (users == null || users.isEmpty())
		{
			throw new Exception(_("groupDoesNotExist"));
		}
		outputString.append(_("listGroupTag", Util.replaceFormat(groupName)));
		outputString.append(listUsers(users));
		outputString.setCharAt(0, Character.toTitleCase(outputString.charAt(0)));
		return outputString.toString();
	}

	private void sendGroupedList(Server server, CommandSender sender, String commandLabel, Map<String, List<User>> playerList)
	{
		final StringBuilder outputString = new StringBuilder();
		Set<String> configGroups = ess.getSettings().getListGroupConfig().keySet();
		List<String> usedGroups = new ArrayList<String>();
		List<String> usedGroupsAsterisk = new ArrayList<String>();
		Map<String, Boolean> asterisk = new HashMap<String, Boolean>();
		boolean hasAsterisk = false;
		
		for (String group : configGroups)
		{
			boolean userLimit = false;
			String groupValue = ess.getSettings().getListGroupConfig().get(group).toString().trim();
			
			// If the group value is an asterisk, then skip it, and handle it later
			if (groupValue.equals("*"))
			{
				asterisk.put(group, true);
				hasAsterisk = true;
				continue;
			}
			
			group = group.toLowerCase();
			usedGroups.add(group);
			
			// If the group value is hidden, we don't need to display it
			if (groupValue.equals("hidden"))
			{
				continue;
			}
			
			
			if (Util.isInt(groupValue))
			{
				userLimit = true;
			}
			
			final List<User> users = new ArrayList<User>();
			List<User> u = playerList.get(group);
			if (u != null && !u.isEmpty())
			{

				if (userLimit)
				{
					users.addAll(u);
					int limit = Integer.parseInt(groupValue);
					if (u.size() > limit)
					{
						outputString.append(_("listGroupTag", Util.replaceFormat(group)));
						outputString.append(_("groupNumber", u.size(), commandLabel, group));
						outputString.setCharAt(0, Character.toTitleCase(outputString.charAt(0)));
						sender.sendMessage(outputString.toString());
						outputString.setLength(0);
						continue;
					}
				}
			}

			if (groupValue.contains(",") || playerList.containsKey(groupValue.toLowerCase()))
			{
				if (playerList.containsKey(groupValue))
				{
					u = playerList.get(groupValue);
					if (u == null || u.isEmpty())
					{
						continue;
					}
					users.addAll(u);
					usedGroupsAsterisk.add(groupValue);
				}
				else
				{
					String[] groups = groupValue.split(",");
					for (String g : groups)
					{
						g = g.trim().toLowerCase();
						if (g == null || g.equals(""))
						{
							continue;
						}
						u = playerList.get(g);
						if (u == null || u.isEmpty())
						{
							continue;
						}
						users.addAll(u);
						usedGroupsAsterisk.add(g);
					}
				}
			}
			if (users == null || users.isEmpty())
			{
				continue;
			}
			if (ess.getPermissionsHandler().getName().equals("ConfigPermissions"))
			{
				group = _("connectedPlayers");
			}
			outputString.append(_("listGroupTag", Util.replaceFormat(group)));
			outputString.append(listUsers(users));
			outputString.setCharAt(0, Character.toTitleCase(outputString.charAt(0)));
			sender.sendMessage(outputString.toString());
			outputString.setLength(0);
		}

		final String[] groups = playerList.keySet().toArray(new String[0]);
		Arrays.sort(groups, String.CASE_INSENSITIVE_ORDER);
		List<User> asteriskUsers = new ArrayList<User>();
		String asteriskGroup = "";
		if (hasAsterisk)
		{
			for (String key : asterisk.keySet())
			{
				if (asterisk.get(key) == true)
				{
					asteriskGroup = key.toLowerCase();
					for (String group : groups)
					{
						group = group.toLowerCase().trim();
						if (usedGroups.contains(group) || usedGroupsAsterisk.contains(group))
						{
							continue;
						}
						asteriskUsers.addAll(playerList.get(group));
					}
				}
			}
		}
		for (String group : groups)
		{
			group = group.toLowerCase().trim();
			if (usedGroups.contains(group))
			{
				continue;
			}
			List<User> users = playerList.get(group);

			if (ess.getPermissionsHandler().getName().equals("ConfigPermissions"))
			{
				group = _("connectedPlayers");
			}
			if (hasAsterisk)
			{
				if (asteriskUsers == null || asteriskUsers.isEmpty())
				{
					break;
				}
				users = asteriskUsers;
				group = asteriskGroup;
			}
			outputString.append(_("listGroupTag", Util.replaceFormat(group)));

			outputString.append(listUsers(users));
			outputString.setCharAt(0, Character.toTitleCase(outputString.charAt(0)));
			sender.sendMessage(outputString.toString());
			outputString.setLength(0);
			if (hasAsterisk)
			{
				break;
			}
		}
	}

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
}
