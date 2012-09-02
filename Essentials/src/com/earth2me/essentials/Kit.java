package com.earth2me.essentials;

import static com.earth2me.essentials.I18n._;
import static com.earth2me.essentials.I18n.capitalCase;
import com.earth2me.essentials.commands.NoChargeException;
import com.earth2me.essentials.craftbukkit.InventoryWorkaround;
import java.util.*;
import java.util.logging.Level;
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
				if (user == null || user.isAuthorized("essentials.kit." + kiteItem.toLowerCase(Locale.ENGLISH)))
				{
					list.append(" ").append(capitalCase(kiteItem));
				}
			}
			return list.toString().trim();
		}
		catch (Exception ex)
		{
			throw new Exception(_("kitError"), ex);
		}

	}

	public static void checkTime(final User user, final String kitName, final Map<String, Object> els) throws NoChargeException
	{
		if (user.isAuthorized("essentials.kit.exemptdelay")) {
			return;
		}
		
		final Calendar time = new GregorianCalendar();

		// Take the current time, and remove the delay from it.
		final double delay = els.containsKey("delay") ? ((Number)els.get("delay")).doubleValue() : 0L;
		final Calendar earliestTime = new GregorianCalendar();
		earliestTime.add(Calendar.SECOND, -(int)delay);
		earliestTime.add(Calendar.MILLISECOND, -(int)((delay * 1000.0) % 1000.0));
		// This value contains the most recent time a kit could have been used that would allow another use.
		final long earliestLong = earliestTime.getTimeInMillis();

		// When was the last kit used?
		final long lastTime = user.getKitTimestamp(kitName);

		if (lastTime < earliestLong)
		{
			user.setKitTimestamp(kitName, time.getTimeInMillis());
		}
		else if (lastTime > time.getTimeInMillis())
		{
			// This is to make sure time didn't get messed up on last kit use.
			// If this happens, let's give the user the benifit of the doubt.
			user.setKitTimestamp(kitName, time.getTimeInMillis());
		}
		else
		{
			time.setTimeInMillis(lastTime);
			time.add(Calendar.SECOND, (int)delay);
			time.add(Calendar.MILLISECOND, (int)((delay * 1000.0) % 1000.0));
			user.sendMessage(_("kitTimed", Util.formatDateDiff(time.getTimeInMillis())));
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
			throw new Exception(_("kitErrorHelp"), e);
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
						final Enchantment enchantment = Enchantments.getByName(split[0]);
						if (enchantment == null)
						{
							throw new Exception("Enchantment not found: " + split[0]);
						}
						int level;
						if (split.length > 1)
						{
							level = Integer.parseInt(split[1]);
						}
						else
						{
							level = enchantment.getMaxLevel();
						}
						try
						{
							stack.addEnchantment(enchantment, level);
						}
						catch (Exception ex)
						{
							throw new Exception("Enchantment " + enchantment.getName() + ": " + ex.getMessage(), ex);
						}
					}
				}

				final Map<Integer, ItemStack> overfilled;
				if (user.isAuthorized("essentials.oversizedstacks"))
				{
					overfilled = InventoryWorkaround.addItem(user.getInventory(), true, ess.getSettings().getOversizedStackSize(), stack);
				}
				else
				{
					overfilled = InventoryWorkaround.addItem(user.getInventory(), true, 0, stack);
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
			if (ess.getSettings().isDebug())
			{
				ess.getLogger().log(Level.WARNING, e.getMessage());
			}
			else
			{
				ess.getLogger().log(Level.WARNING, e.getMessage());
			}
			throw new Exception(_("kitError2"), e);
		}
	}
}
