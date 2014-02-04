package com.earth2me.essentials;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.utils.FormatUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.bukkit.Server;
import org.bukkit.entity.Player;


public class PlayerList
{
	// Cosmetic list formatting
	public static String listUsers(final IEssentials ess, final List<User> users, final String seperator)
	{
		final StringBuilder groupString = new StringBuilder();
		Collections.sort(users);
		boolean needComma = false;
		for (User user : users)
		{
			if (needComma)
			{
				groupString.append(seperator);
			}
			needComma = true;
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
			groupString.append("\u00a7f");
		}
		return groupString.toString();
	}

	// Produce a user summary: There are 5 out of maximum 10 players online.
	public static String listSummary(final IEssentials ess, final boolean showHidden)
	{
		Server server = ess.getServer();
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
	public static Map<String, List<User>> getPlayerLists(final IEssentials ess, final boolean showHidden)
	{
		Server server = ess.getServer();
		final Map<String, List<User>> playerList = new HashMap<String, List<User>>();
		for (Player onlinePlayer : server.getOnlinePlayers())
		{
			final User onlineUser = ess.getUser(onlinePlayer);
			if (onlineUser.isHidden() && !showHidden)
			{
				continue;
			}
			final String group = FormatUtil.stripFormat(FormatUtil.stripEssentialsFormat(onlineUser.getGroup().toLowerCase()));
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

	// Handle the merging of groups
	public static List<User> getMergedList(final IEssentials ess, final Map<String, List<User>> playerList, final String groupName)
	{
		final Set<String> configGroups = ess.getSettings().getListGroupConfig().keySet();
		final List<User> users = new ArrayList<User>();
		for (String configGroup : configGroups)
		{
			if (configGroup.equalsIgnoreCase(groupName))
			{
				String[] groupValues = ess.getSettings().getListGroupConfig().get(configGroup).toString().trim().split(" ");
				for (String groupValue : groupValues)
				{
					groupValue = groupValue.toLowerCase(Locale.ENGLISH);
					if (groupValue == null || groupValue.isEmpty())
					{
						continue;
					}
					List<User> u = playerList.get(groupValue.trim());
					if (u == null || u.isEmpty())
					{
						continue;
					}
					playerList.remove(groupValue);
					users.addAll(u);
				}
			}
		}
		return users;
	}

	// Output a playerlist of just a single group, /list <groupname>
	public static String listGroupUsers(final IEssentials ess, final Map<String, List<User>> playerList, final String groupName) throws Exception
	{
		final List<User> users = getMergedList(ess, playerList, groupName);
		final List<User> groupUsers = playerList.get(groupName);
		if (groupUsers != null && !groupUsers.isEmpty())
		{
			users.addAll(groupUsers);
		}
		if (users == null || users.isEmpty())
		{
			throw new Exception(_("groupDoesNotExist"));
		}
		final StringBuilder displayGroupName = new StringBuilder();
		displayGroupName.append(Character.toTitleCase(groupName.charAt(0)));
		displayGroupName.append(groupName.substring(1));
		return outputFormat(displayGroupName.toString(), listUsers(ess, users, ", "));
	}

	// Build the output string
	public static String outputFormat(final String group, final String message)
	{
		final StringBuilder outputString = new StringBuilder();
		outputString.append(_("listGroupTag", FormatUtil.replaceFormat(group)));
		outputString.append(message);
		return outputString.toString();
	}
}
