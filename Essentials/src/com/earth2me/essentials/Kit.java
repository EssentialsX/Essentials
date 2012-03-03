package com.earth2me.essentials;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.commands.NoChargeException;
import com.earth2me.essentials.craftbukkit.InventoryWorkaround;
import java.util.*;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
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
					list.append(" ").append(kiteItem);
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
		if (kit == null) {
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
				final String[] parts = d.split("[^0-9]+", 3);
				final int id = Material.getMaterial(Integer.parseInt(parts[0])).getId();
				final int amount = parts.length > 1 ? Integer.parseInt(parts[parts.length > 2 ? 2 : 1]) : 1;
				final short data = parts.length > 2 ? Short.parseShort(parts[1]) : 0;
				final Map<Integer, ItemStack> overfilled;
				if (user.isAuthorized("essentials.oversizedstacks"))
				{
					overfilled = InventoryWorkaround.addItem(user.getInventory(), true, ess.getSettings().getOversizedStackSize(), new ItemStack(id, amount, data));
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
