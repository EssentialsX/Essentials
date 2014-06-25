package com.earth2me.essentials;

import static com.earth2me.essentials.I18n.tl;
import static com.earth2me.essentials.I18n.capitalCase;
import com.earth2me.essentials.Trade.OverflowType;
import com.earth2me.essentials.commands.NoChargeException;
import com.earth2me.essentials.craftbukkit.InventoryWorkaround;
import com.earth2me.essentials.textreader.IText;
import com.earth2me.essentials.textreader.KeywordReplacer;
import com.earth2me.essentials.textreader.SimpleTextInput;
import com.earth2me.essentials.utils.DateUtil;
import com.earth2me.essentials.utils.NumberUtil;
import java.math.BigDecimal;
import java.util.*;
import java.util.logging.Level;
import net.ess3.api.IEssentials;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;


public class Kit
{
	final IEssentials ess;
	final String kitName;
	final Map<String, Object> kit;
	final Trade charge;

	public Kit(final String kitName, final IEssentials ess) throws Exception
	{
		this.kitName = kitName;
		this.ess = ess;
		this.kit = ess.getSettings().getKit(kitName);
		this.charge = new Trade("kit-" + kitName, new Trade("kit-kit", ess), ess);

		if (kit == null)
		{
			throw new Exception(tl("kitNotFound"));
		}
	}

	//TODO: Convert this to use one of the new text classes?
	public static String listKits(final IEssentials ess, final User user) throws Exception
	{
		try
		{
			final ConfigurationSection kits = ess.getSettings().getKits();
			final StringBuilder list = new StringBuilder();
			for (String kitItem : kits.getKeys(false))
			{
				if (user == null)
				{
					list.append(" ").append(capitalCase(kitItem));
				}
				else if (user.isAuthorized("essentials.kits." + kitItem.toLowerCase(Locale.ENGLISH)))
				{
					String cost = "";
					String name = capitalCase(kitItem);
					BigDecimal costPrice = new Trade("kit-" + kitItem.toLowerCase(Locale.ENGLISH), ess).getCommandCost(user);
					if (costPrice.signum() > 0)
					{
						cost = tl("kitCost", NumberUtil.displayCurrency(costPrice, ess));
					}

					Kit kit = new Kit(kitItem, ess);
					if (kit.getNextUse(user) != 0)
					{
						name = tl("kitDelay", name);
					}

					list.append(" ").append(name).append(cost);
				}
			}
			return list.toString().trim();
		}
		catch (Exception ex)
		{
			throw new Exception(tl("kitError"), ex);
		}

	}

	public String getName()
	{
		return kitName;
	}

	public void checkPerms(final User user) throws Exception
	{
		if (!user.isAuthorized("essentials.kits." + kitName))
		{
			throw new Exception(tl("noKitPermission", "essentials.kits." + kitName));
		}
	}

	public void checkDelay(final User user) throws Exception
	{
		long nextUse = getNextUse(user);

		if (nextUse == 0L)
		{
			return;
		}
		else if (nextUse < 0L)
		{
			user.sendMessage(tl("kitOnce"));
			throw new NoChargeException();
		}
		else
		{
			user.sendMessage(tl("kitTimed", DateUtil.formatDateDiff(nextUse)));
			throw new NoChargeException();
		}
	}

	public void checkAffordable(final User user) throws Exception
	{
		charge.isAffordableFor(user);
	}

	public void setTime(final User user) throws Exception
	{
		final Calendar time = new GregorianCalendar();
		user.setKitTimestamp(kitName, time.getTimeInMillis());
	}

	public void chargeUser(final User user) throws Exception
	{
		charge.charge(user);
	}

	public long getNextUse(final User user) throws Exception
	{
		if (user.isAuthorized("essentials.kit.exemptdelay"))
		{
			return 0L;
		}

		final Calendar time = new GregorianCalendar();

		double delay = 0;
		try
		{
			// Make sure delay is valid
			delay = kit.containsKey("delay") ? ((Number)kit.get("delay")).doubleValue() : 0.0d;
		}
		catch (Exception e)
		{
			throw new Exception(tl("kitError2"));
		}

		// When was the last kit used?
		final long lastTime = user.getKitTimestamp(kitName);

		// When can be use the kit again?
		final Calendar delayTime = new GregorianCalendar();
		delayTime.setTimeInMillis(lastTime);
		delayTime.add(Calendar.SECOND, (int)delay);
		delayTime.add(Calendar.MILLISECOND, (int)((delay * 1000.0) % 1000.0));

		if (lastTime == 0L || lastTime > time.getTimeInMillis())
		{
			// If we have no record of kit use, or its corrupted, give them benifit of the doubt.
			return 0L;
		}
		else if (delay < 0d)
		{
			// If the kit has a negative kit time, it can only be used once.
			return -1;
		}
		else if (delayTime.before(time))
		{
			// If the kit was used in the past, but outside the delay time, it can be used.
			return 0L;
		}
		else
		{
			// If the kit has been used recently, return the next time it can be used.
			return delayTime.getTimeInMillis();
		}
	}

	public List<String> getItems(final User user) throws Exception
	{
		if (kit == null)
		{
			throw new Exception(tl("kitNotFound"));
		}
		try
		{
			final List<String> itemList = new ArrayList<String>();
			final Object kitItems = kit.get("items");
			if (kitItems instanceof List)
			{
				for (Object item : (List)kitItems)
				{
					if (item instanceof String)
					{
						itemList.add(item.toString());
						continue;
					}
					throw new Exception("Invalid kit item: " + item.toString());
				}
				return itemList;
			}
			throw new Exception("Invalid item list");
		}
		catch (Exception e)
		{
			ess.getLogger().log(Level.WARNING, "Error parsing kit " + kitName + ": " + e.getMessage());
			throw new Exception(tl("kitError2"), e);
		}
	}

	public void expandItems(final User user) throws Exception
	{
		expandItems(user, getItems(user));
	}

	public void expandItems(final User user, final List<String> items) throws Exception
	{
		try
		{
			IText input = new SimpleTextInput(items);
			IText output = new KeywordReplacer(input, user.getSource(), ess);

			boolean spew = false;
			final boolean allowUnsafe = ess.getSettings().allowUnsafeEnchantments();
			for (String kitItem : output.getLines())
			{
				if (kitItem.startsWith(ess.getSettings().getCurrencySymbol()))
				{
					BigDecimal value = new BigDecimal(kitItem.substring(ess.getSettings().getCurrencySymbol().length()).trim());
					Trade t = new Trade(value, ess);
					t.pay(user, OverflowType.DROP);
					continue;
				}

				final String[] parts = kitItem.split(" +");
				final ItemStack parseStack = ess.getItemDb().get(parts[0], parts.length > 1 ? Integer.parseInt(parts[1]) : 1);

				if (parseStack.getType() == Material.AIR)
				{
					continue;
				}

				final MetaItemStack metaStack = new MetaItemStack(parseStack);

				if (parts.length > 2)
				{
					// We pass a null sender here because kits should not do perm checks
					metaStack.parseStringMeta(null, allowUnsafe, parts, 2, ess);
				}

				final Map<Integer, ItemStack> overfilled;
				final boolean allowOversizedStacks = user.isAuthorized("essentials.oversizedstacks");
				if (allowOversizedStacks)
				{
					overfilled = InventoryWorkaround.addOversizedItems(user.getBase().getInventory(), ess.getSettings().getOversizedStackSize(), metaStack.getItemStack());
				}
				else
				{
					overfilled = InventoryWorkaround.addItems(user.getBase().getInventory(), metaStack.getItemStack());
				}
				for (ItemStack itemStack : overfilled.values())
				{
					int spillAmount = itemStack.getAmount();
					if (!allowOversizedStacks)
					{
						itemStack.setAmount(spillAmount < itemStack.getMaxStackSize() ? spillAmount : itemStack.getMaxStackSize());
					}
					while (spillAmount > 0)
					{
						user.getWorld().dropItemNaturally(user.getLocation(), itemStack);
						spillAmount -= itemStack.getAmount();
					}
					spew = true;
				}
			}
			user.getBase().updateInventory();
			if (spew)
			{
				user.sendMessage(tl("kitInvFull"));
			}
		}
		catch (Exception e)
		{
			user.getBase().updateInventory();
			ess.getLogger().log(Level.WARNING, e.getMessage());
			throw new Exception(tl("kitError2"), e);
		}
	}
}
