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
			Map<String, String> usedGroups = new HashMap();
			for (String group : keys)
			{
				boolean userLimit = false;
				String groupValue = ess.getSettings().getListGroupConfig().get(group).toString().trim();
				usedGroups.put(group.toLowerCase(), groupValue);
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
					users.addAll(u);
					if (userLimit)
					{
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
					} 
					else 
					{
						String[] groups = groupValue.split(",");
						for (String g : groups)
						{
							if (g == null || g.equals(""))
							{
								continue;
							}
							u = sort.get(g.trim());
							if (u == null || u.isEmpty())
							{
								continue;
							}
							users.addAll(u);
						}
					}
				}
				if (users == null || users.isEmpty())
				{
					continue;
				}
				groupString.append(_("listGroupTag", Util.replaceFormat(group)));
				groupString.append(listUsers(users));
				groupString.setCharAt(0, Character.toTitleCase(groupString.charAt(0)));
				sender.sendMessage(groupString.toString());
				groupString.setLength(0);
			}
			final String[] groups = sort.keySet().toArray(new String[0]);
			Arrays.sort(groups, String.CASE_INSENSITIVE_ORDER);
			for (String group : groups)
			{
				group = group.toLowerCase();
				if (usedGroups.containsKey(group))
				{
					continue;
				}
				groupString.append(_("listGroupTag", Util.replaceFormat(group)));
				final List<User> users = sort.get(group);
				groupString.append(listUsers(users));
				groupString.setCharAt(0, Character.toTitleCase(groupString.charAt(0)));
				sender.sendMessage(groupString.toString());
				groupString.setLength(0);
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
