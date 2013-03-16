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


		Map<String, List<User>> sort = new HashMap<String, List<User>>();
		for (Player OnlinePlayer : server.getOnlinePlayers())
		{
			final User player = ess.getUser(OnlinePlayer);
			if (player.isHidden() && !showhidden)
			{
				continue;
			}
			final String group = player.getGroup().toLowerCase();
			List<User> list = sort.get(group);
			if (list == null)
			{
				list = new ArrayList<User>();
				sort.put(group, list);
			}
			list.add(player);
		}
		final StringBuilder groupString = new StringBuilder();
		Set<String> keys = ess.getSettings().getListGroupConfig().keySet();
		
		if (args.length > 0)
		{
			
			final List<User> users = new ArrayList<User>();
			String group = args[0].toLowerCase();
			for (String key : keys)
			{
				String groupValue = ess.getSettings().getListGroupConfig().get(key).toString().trim();
				if(key.equalsIgnoreCase(group) && groupValue.contains(","))
				{
					String[] groups = groupValue.split(",");
					for (String g : groups)
					{
						if (g == null || g.equals(""))
						{
							continue;
						}
						List<User> u = sort.get(g.trim());
						if (u == null || u.isEmpty())
						{
							continue;
						}
						users.addAll(u);
					}
				}
			}
			List<User> groupUsers = sort.get(group);
			if (groupUsers != null && !groupUsers.isEmpty())
			{
				users.addAll(groupUsers);
			}
			if (users == null || users.isEmpty())
			{
				throw new Exception(_("groupDoesNotExist"));
			}
			groupString.append(_("listGroupTag", Util.replaceFormat(group)));
			groupString.append(listUsers(users));
			groupString.setCharAt(0, Character.toTitleCase(groupString.charAt(0)));
			sender.sendMessage(groupString.toString());
			groupString.setLength(0);
		}
		else
		{
			List<String> usedGroups = new ArrayList<String>();
			List<String> usedGroupsAsterisk = new ArrayList<String>();
			Map<String, Boolean> asterisk = new HashMap<String, Boolean>();
			boolean hasAsterisk = false;
			for (String group : keys)
			{
				boolean userLimit = false;
				String groupValue = ess.getSettings().getListGroupConfig().get(group).toString().trim();
				if (groupValue.equals("*"))
				{
					asterisk.put(group, true);
					hasAsterisk = true;
					continue;
				}
				usedGroups.add(group.toLowerCase());
				if (groupValue.equals("hidden"))
				{
					continue;
				}
				if (Util.isInt(groupValue))
				{
					userLimit = true;
				}
				group = group.toLowerCase();
				final List<User> users = new ArrayList<User>();
				List<User> u = sort.get(group);
				if (u != null && !u.isEmpty())
				{
					
					if (userLimit)
					{
						users.addAll(u);
						int limit = Integer.parseInt(groupValue);
						if (u.size() > limit)
						{
							groupString.append(_("listGroupTag", Util.replaceFormat(group)));
							groupString.append(_("groupNumber", u.size(), commandLabel, group));
							groupString.setCharAt(0, Character.toTitleCase(groupString.charAt(0)));
							sender.sendMessage(groupString.toString());
							groupString.setLength(0);
							continue;
						}
					}
				}

				if (groupValue.contains(",") || sort.containsKey(groupValue.toLowerCase()))
				{
					if (sort.containsKey(groupValue))
					{
						u = sort.get(groupValue);
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
							u = sort.get(g);
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
				groupString.append(_("listGroupTag", Util.replaceFormat(group)));
				groupString.append(listUsers(users));
				groupString.setCharAt(0, Character.toTitleCase(groupString.charAt(0)));
				sender.sendMessage(groupString.toString());
				groupString.setLength(0);
			}
			final String[] groups = sort.keySet().toArray(new String[0]);
			Arrays.sort(groups, String.CASE_INSENSITIVE_ORDER);
			List<User> asteriskUsers = new ArrayList<User>();
			String asteriskGroup = "";
			if (hasAsterisk)
			{
				for(String key : asterisk.keySet())
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
							asteriskUsers.addAll(sort.get(group));
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
				List<User> users = sort.get(group);

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
				groupString.append(_("listGroupTag", Util.replaceFormat(group)));
				
				groupString.append(listUsers(users));
				groupString.setCharAt(0, Character.toTitleCase(groupString.charAt(0)));
				sender.sendMessage(groupString.toString());
				groupString.setLength(0);
				if (hasAsterisk)
				{
					break;
				}
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
