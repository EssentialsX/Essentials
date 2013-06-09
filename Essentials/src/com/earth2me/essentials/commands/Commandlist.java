package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.FormatUtil;
import com.earth2me.essentials.utils.NumberUtil;
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
			showHidden = ess.getUser(sender).isAuthorized("essentials.list.hidden") || ess.getUser(sender).isAuthorized("essentials.vanish.interact");
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
			final String group = FormatUtil.stripFormat(onlineUser.getGroup().toLowerCase());
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

		return outputFormat(groupName, listUsers(users));
	}

	// Handle the merging of groups
	private List<User> getMergedList(final Map<String, List<User>> playerList, final String groupName)
	{
		Set<String> configGroups = ess.getSettings().getListGroupConfig().keySet();
		final List<User> users = new ArrayList<User>();

		for (String configGroup : configGroups)
		{
			if (configGroup.equalsIgnoreCase(groupName))
			{
				String[] groupValues = ess.getSettings().getListGroupConfig().get(configGroup).toString().trim().split(" ");
				for (String groupValue : groupValues)
				{
					if (groupValue == null || groupValue.equals(""))
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

	// Output the standard /list output, when no group is specified
	private void sendGroupedList(CommandSender sender, String commandLabel, Map<String, List<User>> playerList)
	{
		Set<String> configGroups = ess.getSettings().getListGroupConfig().keySet();
		List<String> asterisk = new ArrayList<String>();

		// Loop through the custom defined groups and display them
		for (String oConfigGroup : configGroups)
		{
			String groupValue = ess.getSettings().getListGroupConfig().get(oConfigGroup).toString().trim();
			String configGroup = oConfigGroup.toLowerCase();

			// If the group value is an asterisk, then skip it, and handle it later
			if (groupValue.equals("*"))
			{
				asterisk.add(oConfigGroup);
				continue;
			}

			// If the group value is hidden, we don't need to display it
			if (groupValue.equalsIgnoreCase("hidden"))
			{
				playerList.remove(groupValue);
				continue;
			}

			List<User> outputUserList = new ArrayList<User>();
			List<User> matchedList = playerList.get(configGroup);

			// If the group value is an int, then we might need to truncate it
			if (NumberUtil.isInt(groupValue))
			{
				if (matchedList != null && !matchedList.isEmpty())
				{
					playerList.remove(configGroup);
					outputUserList.addAll(matchedList);
					int limit = Integer.parseInt(groupValue);
					if (matchedList.size() > limit)
					{
						sender.sendMessage(outputFormat(oConfigGroup, _("groupNumber", matchedList.size(), commandLabel, FormatUtil.stripFormat(configGroup))));
					}
					else
					{
						sender.sendMessage(outputFormat(oConfigGroup, listUsers(outputUserList)));
					}
					continue;
				}
			}

			outputUserList = getMergedList(playerList, configGroup);

			// If we have no users, than we don't need to continue parsing this group
			if (outputUserList == null || outputUserList.isEmpty())
			{
				continue;
			}

			sender.sendMessage(outputFormat(oConfigGroup, listUsers(outputUserList)));
		}

		String[] onlineGroups = playerList.keySet().toArray(new String[0]);
		Arrays.sort(onlineGroups, String.CASE_INSENSITIVE_ORDER);

		// If we have an asterisk group, then merge all remaining groups
		if (!asterisk.isEmpty())
		{
			List<User> asteriskUsers = new ArrayList<User>();
			for (String onlineGroup : onlineGroups)
			{
				asteriskUsers.addAll(playerList.get(onlineGroup));
			}
			for (String key : asterisk)
			{
				playerList.put(key, asteriskUsers);
			}
			onlineGroups = asterisk.toArray(new String[0]);
		}

		// If we have any groups remaining after the custom groups loop through and display them
		for (String onlineGroup : onlineGroups)
		{
			List<User> users = playerList.get(onlineGroup);
			String groupName = asterisk.isEmpty() ? users.get(0).getGroup() : onlineGroup;

			if (ess.getPermissionsHandler().getName().equals("ConfigPermissions"))
			{
				groupName = _("connectedPlayers");
			}
			if (users == null || users.isEmpty())
			{
				continue;
			}

			sender.sendMessage(outputFormat(groupName, listUsers(users)));
		}
	}

	// Cosmetic list formatting
	private String listUsers(List<User> users)
	{
		final StringBuilder groupString = new StringBuilder();
		Collections.sort(users);
		boolean needComma = false;
		for (User user : users)
		{
			if (needComma)
			{
				groupString.append(", ");
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
			groupString.append("§f");
		}
		return groupString.toString();
	}

	// Build the output string
	private String outputFormat(String group, String message)
	{
		final StringBuilder outputString = new StringBuilder();
		outputString.append(_("listGroupTag", FormatUtil.replaceFormat(group)));
		outputString.append(message);
		outputString.setCharAt(0, Character.toTitleCase(outputString.charAt(0)));
		return outputString.toString();
	}
}
