package com.earth2me.essentials.commands;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Server;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import org.bukkit.inventory.ItemStack;


public class Commandkit extends EssentialsCommand
{
	static private final Map<User, Map<String, Long>> kitPlayers = new HashMap<User, Map<String, Long>>();

	public Commandkit()
	{
		super("kit");
	}

	@Override
	public void run(Server server, Essentials parent, User user, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 1)
		{
			try
			{
				Map<String, Object> kits = (Map<String, Object>)parent.getConfiguration().getProperty("kits");
				StringBuilder list = new StringBuilder();
				for (String k : kits.keySet())
				{
					if (user.isAuthorized("essentials.kit." + k))
					{
						list.append(" ").append(k);
					}
				}
				if (list.length() > 0)
				{
					user.sendMessage("§7Kits:" + list.toString());
				}
				else
				{
					user.sendMessage("§7There are no kits available yet");
				}
			}
			catch (Exception ex)
			{
				user.sendMessage("§cThere are no valid kits.");
			}
		}
		else
		{
			try
			{
				String kitName = args[0].toLowerCase();
				Object kit = Essentials.getSettings().getKit(kitName);
				List<String> items;

				if (!user.isAuthorized("essentials.kit." + kitName))
				{
					user.sendMessage("§cYou need the §fessentials.kit." + kitName + "§c permission to use that kit.");
					return;
				}

				try
				{

					System.out.println("Kit is timed");
					Map<String, Object> els = (Map<String, Object>)kit;
					items = (List<String>)els.get("items");
					long delay = els.containsKey("delay") ? (Integer)els.get("delay") * 1000L : 0L;
					long time = Calendar.getInstance().getTimeInMillis();

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
						else if (kitTimes.get(kitName) + delay <= time)
						{
							kitTimes.put(kitName, time);
						}
						else
						{
							long left = kitTimes.get(kitName) + delay - time;
							user.sendMessage("§cYou can't use that kit again for another " + Essentials.FormatTime(left) + ".");

							return;
						}
					}
				}
				catch (Exception ex)
				{
					items = (List<String>)kit;
				}

				try {
					user.canAfford("kit-" + kitName);
				} catch (Exception ex) {
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
					if(user.getInventory().firstEmpty() != -1)
					{
					user.getInventory().addItem(new ItemStack(id, amount, data));
					}
					else
					{
						spew = true;
						user.getWorld().dropItemNaturally(user.getLocation(), new ItemStack(id, amount, data));
					}
				}
				if(spew)
				{
					user.sendMessage("§7Your inventory was full, placing kit on the floor");
				}
				try {
					user.charge(this);
					user.charge("kit-" + kitName);
				} catch (Exception ex) {
					user.sendMessage(ex.getMessage());
				}
				user.sendMessage("§7Giving kit " + args[0].toLowerCase() + ".");
			}
			catch (Exception ex)
			{
				user.sendMessage("§cThat kit does not exist or is improperly defined.");
				user.sendMessage("§cPerhaps an item is missing a quantity in the configuration?");
			}
		}
	}
}
