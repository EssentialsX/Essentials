package com.earth2me.essentials.commands;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Server;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import java.util.GregorianCalendar;
import org.bukkit.inventory.ItemStack;


public class Commandkit extends EssentialsCommand
{
	static private final Map<User, Map<String, Long>> kitPlayers = new HashMap<User, Map<String, Long>>();

	public Commandkit()
	{
		super("kit");
	}

	@Override
	@SuppressWarnings("unchecked")
	public void run(Server server, User user, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 1)
		{
			try
			{
				Map<String, Object> kits = (Map<String, Object>)ess.getConfiguration().getProperty("kits");
				StringBuilder list = new StringBuilder();
				for (String k : kits.keySet())
				{
					if (user.isAuthorized("essentials.kit." + k.toLowerCase()))
					{
						list.append(" ").append(k);
					}
				}
				if (list.length() > 0)
				{
					user.sendMessage(Util.format("kits", list.toString()));
				}
				else
				{
					user.sendMessage(Util.i18n("noKits"));
				}
			}
			catch (Exception ex)
			{
				user.sendMessage(Util.i18n("kitError"));
			}
		}
		else
		{
			try
			{
				String kitName = args[0].toLowerCase();
				Object kit = ess.getSettings().getKit(kitName);
				List<String> items;

				if (!user.isAuthorized("essentials.kit." + kitName))
				{
					user.sendMessage(Util.format("noKitPermission", "essentials.kit." + kitName));
					return;
				}

				try
				{

					//System.out.println("Kit is timed");
					Map<String, Object> els = (Map<String, Object>)kit;
					items = (List<String>)els.get("items");
					double delay = els.containsKey("delay") ? ((Number)els.get("delay")).doubleValue() : 0L;
					Calendar c = new GregorianCalendar();
					c.add(Calendar.SECOND, (int)delay);
					c.add(Calendar.MILLISECOND, (int)((delay*1000.0)%1000.0));
			
					long time = c.getTimeInMillis();
					Calendar now = new GregorianCalendar();

					Map<String, Long> kitTimes;
					if (!kitPlayers.containsKey(user))
					{
						kitTimes = new HashMap<String, Long>();
						kitTimes.put(kitName, time);
						kitPlayers.put(user, kitTimes);
					}
					else
					{
						kitTimes = kitPlayers.get(user);
						if (!kitTimes.containsKey(kitName))
						{
							kitTimes.put(kitName, time);
						}
						else if (kitTimes.get(kitName) < now.getTimeInMillis())
						{
							kitTimes.put(kitName, time);
						}
						else
						{
							user.sendMessage(Util.format("kitTimed", Util.formatDateDiff(kitTimes.get(kitName))));
							return;
						}
					}
				}
				catch (Exception ex)
				{
					items = (List<String>)kit;
				}

				try
				{
					user.canAfford("kit-" + kitName);
				}
				catch (Exception ex)
				{
					user.sendMessage(ex.getMessage());
					return;
				}

				boolean spew = false;
				for (String d : items)
				{
					String[] parts = d.split("[^0-9]+", 3);
					int id = Integer.parseInt(parts[0]);
					int amount = parts.length > 1 ? Integer.parseInt(parts[parts.length > 2 ? 2 : 1]) : 1;
					short data = parts.length > 2 ? Short.parseShort(parts[1]) : 0;
					HashMap<Integer,ItemStack> overfilled = user.getInventory().addItem(new ItemStack(id, amount, data));
					for (ItemStack itemStack : overfilled.values())
					{
						user.getWorld().dropItemNaturally(user.getLocation(), itemStack);
						spew = true;
					}
				}
				if (spew)
				{
					user.sendMessage(Util.i18n("kitInvFull"));
				}
				try
				{
					user.charge(this);
					user.charge("kit-" + kitName);
				}
				catch (Exception ex)
				{
					user.sendMessage(ex.getMessage());
				}
				user.sendMessage(Util.format("kitGive", kitName));
			}
			catch (Exception ex)
			{
				user.sendMessage(Util.i18n("kitError2"));
				user.sendMessage(Util.i18n("kitErrorHelp"));
			}
		}
	}
}
