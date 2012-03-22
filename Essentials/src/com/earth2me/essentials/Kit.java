package com.earth2me.essentials;

import static com.earth2me.essentials.I18n._;
import static com.earth2me.essentials.I18n.capitalCase;
import com.earth2me.essentials.commands.Commandenchant;
import com.earth2me.essentials.commands.NoChargeException;
import com.earth2me.essentials.craftbukkit.InventoryWorkaround;
import java.util.*;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;


public class Kit
{
	//TODO: Convert this to use one of the new text classes?
	public static String listKits(final IEssentials ess, final User user) throws Exception
	{
		try
		{
			final ConfigurationSection kits = ess.getSettings().getKits();
			final StringBuilder list = new StringBuilder();
			for (String kiteItem : kits.getKeys(false))
			{
				if (user.isAuthorized("essentials.kit." + kiteItem.toLowerCase(Locale.ENGLISH)))
				{
					list.append(" ").append(capitalCase(kiteItem));
				}
			}
			return list.toString().trim();
		}
		catch (Exception ex)
		{
			throw new Exception(_("kitError"));
		}

	}

	public static void checkTime(final User user, final String kitName, final Map<String, Object> els) throws NoChargeException
	{
		final double delay = els.containsKey("delay") ? ((Number)els.get("delay")).doubleValue() : 0L;
		final Calendar c = new GregorianCalendar();
		c.add(Calendar.SECOND, -(int)delay);
		c.add(Calendar.MILLISECOND, -(int)((delay * 1000.0) % 1000.0));

		final long mintime = c.getTimeInMillis();

		final Long lastTime = user.getKitTimestamp(kitName);
		if (lastTime == null || lastTime < mintime)
		{
			final Calendar now = new GregorianCalendar();
			user.setKitTimestamp(kitName, now.getTimeInMillis());
		}
		else
		{
			final Calendar future = new GregorianCalendar();
			future.setTimeInMillis(lastTime);
			future.add(Calendar.SECOND, (int)delay);
			future.add(Calendar.MILLISECOND, (int)((delay * 1000.0) % 1000.0));
			user.sendMessage(_("kitTimed", Util.formatDateDiff(future.getTimeInMillis())));
			throw new NoChargeException();
		}
	}

	public static List<String> getItems(final User user, final Map<String, Object> kit) throws Exception
	{
		if (kit == null)
		{
			throw new Exception(_("kitError2"));
		}

		try
		{
			return (List<String>)kit.get("items");
		}
		catch (Exception e)
		{
			user.sendMessage(_("kitError2"));
			throw new Exception(_("kitErrorHelp"));
		}
	}

	public static void expandItems(final IEssentials ess, final User user, final List<String> items) throws Exception
	{
		try
		{
			boolean spew = false;
			for (String d : items)
			{
				final String[] parts = d.split(" ");
				final String[] item = parts[0].split("[:+',;.]", 2);
				final int id = Material.getMaterial(Integer.parseInt(item[0])).getId();
				final short data = item.length > 1 ? Short.parseShort(item[1]) : 0;
				final int amount = parts.length > 1 ? Integer.parseInt(parts[1]) : 1;

				final ItemStack stack = new ItemStack(id, amount, data);
				if (parts.length > 2)
				{
					for (int i = 2; i < parts.length; i++)
					{
						final String[] split = parts[i].split("[:+',;.]", 2);
						if (split.length < 1)
						{
							continue;
						}
						final Enchantment enchantment = Commandenchant.getEnchantment(split[0], user);
						int level;
						if (split.length > 1)
						{
							level = Integer.parseInt(split[1]);
						}
						else
						{
							level = enchantment.getMaxLevel();
						}
						stack.addEnchantment(enchantment, level);
					}
				}

				final Map<Integer, ItemStack> overfilled;
				if (user.isAuthorized("essentials.oversizedstacks"))
				{
					overfilled = InventoryWorkaround.addItem(user.getInventory(), true, ess.getSettings().getOversizedStackSize(), stack);
				}
				else
				{
					overfilled = InventoryWorkaround.addItem(user.getInventory(), true, new ItemStack(id, amount, data));
				}
				for (ItemStack itemStack : overfilled.values())
				{
					user.getWorld().dropItemNaturally(user.getLocation(), itemStack);
					spew = true;
				}
			}
			user.updateInventory();
			if (spew)
			{
				user.sendMessage(_("kitInvFull"));
			}
		}
		catch (Exception e)
		{
			user.updateInventory();
			throw new Exception(_("kitError2"));
		}
	}
}
