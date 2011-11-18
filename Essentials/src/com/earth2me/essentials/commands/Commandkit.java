package com.earth2me.essentials.commands;

import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import java.util.*;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;


public class Commandkit extends EssentialsCommand
{
	public Commandkit()
	{
		super("kit");
	}

	@Override
	public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			try
			{
				final Map<String, Object> kits = ess.getSettings().getKits();
				final StringBuilder list = new StringBuilder();
				for (String kiteItem : kits.keySet())
				{
					if (user.isAuthorized("essentials.kit." + kiteItem.toLowerCase()))
					{
						list.append(" ").append(kiteItem);
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
				final String kitName = args[0].toLowerCase();
				final Object kit = ess.getSettings().getKit(kitName);
				List<String> items;

				if (!user.isAuthorized("essentials.kit." + kitName))
				{
					user.sendMessage(Util.format("noKitPermission", "essentials.kit." + kitName));
					return;
				}

				try
				{

					//System.out.println("Kit is timed");
					final Map<String, Object> els = (Map<String, Object>)kit;
					items = (List<String>)els.get("items");
					final double delay = els.containsKey("delay") ? ((Number)els.get("delay")).doubleValue() : 0L;
					final Calendar c = new GregorianCalendar();
					c.add(Calendar.SECOND, -(int)delay);
					c.add(Calendar.MILLISECOND, -(int)((delay*1000.0)%1000.0));

					final long mintime = c.getTimeInMillis();

					final Long lastTime = user.getKitTimestamp(kitName);
					if (lastTime == null || lastTime < mintime) {
						final Calendar now = new GregorianCalendar();
						user.setKitTimestamp(kitName, now.getTimeInMillis());
					} else {
						final Calendar future = new GregorianCalendar();
						future.setTimeInMillis(lastTime);
						future.add(Calendar.SECOND, (int)delay);
						future.add(Calendar.MILLISECOND, (int)((delay*1000.0)%1000.0));
						user.sendMessage(Util.format("kitTimed", Util.formatDateDiff(future.getTimeInMillis())));
						return;
					}
				}
				catch (Exception ex)
				{
					items = (List<String>)kit;
				}

				final Trade charge = new Trade("kit-" + kitName, ess);
				try
				{
					charge.isAffordableFor(user);
				}
				catch (Exception ex)
				{
					user.sendMessage(ex.getMessage());
					return;
				}

				boolean spew = false;
				for (String d : items)
				{
					final String[] parts = d.split("[^0-9]+", 3);
					final int id = Material.getMaterial(Integer.parseInt(parts[0])).getId();
					final int amount = parts.length > 1 ? Integer.parseInt(parts[parts.length > 2 ? 2 : 1]) : 1;
					final short data = parts.length > 2 ? Short.parseShort(parts[1]) : 0;
					final HashMap<Integer,ItemStack> overfilled = user.getInventory().addItem(new ItemStack(id, amount, data));
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
					charge.charge(user);
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
