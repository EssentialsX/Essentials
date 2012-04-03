package com.earth2me.essentials;

import com.earth2me.essentials.craftbukkit.InventoryWorkaround;
import com.earth2me.essentials.craftbukkit.SetExpFix;
import static com.earth2me.essentials.I18n._;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;


public class Trade
{
	private final transient String command;
	private final transient String fallbackCommand;
	private final transient Double money;
	private final transient ItemStack itemStack;
	private final transient Integer exp;
	private final transient IEssentials ess;

	public Trade(final String command, final IEssentials ess)
	{
		this(command, null, null, null, null, ess);
	}

	public Trade(final String command, final String fallback, final IEssentials ess)
	{
		this(command, fallback, null, null, null, ess);
	}

	public Trade(final double money, final IEssentials ess)
	{
		this(null, null, money, null, null, ess);
	}

	public Trade(final ItemStack items, final IEssentials ess)
	{
		this(null, null, null, items, null, ess);
	}

	public Trade(final int exp, final IEssentials ess)
	{
		this(null, null, null, null, exp, ess);
	}

	private Trade(final String command, final String fallback, final Double money, final ItemStack item, final Integer exp, final IEssentials ess)
	{
		this.command = command;
		this.fallbackCommand = fallback;
		this.money = money;
		this.itemStack = item;
		this.exp = exp;
		this.ess = ess;
	}

	public void isAffordableFor(final IUser user) throws ChargeException
	{
		if (getMoney() != null
			&& getMoney() > 0
			&& !user.canAfford(getMoney()))
		{
			throw new ChargeException(_("notEnoughMoney"));
		}

		if (getItemStack() != null
			&& !InventoryWorkaround.containsItem(user.getInventory(), true, true, itemStack))
		{
			throw new ChargeException(_("missingItems", getItemStack().getAmount(), getItemStack().getType().toString().toLowerCase(Locale.ENGLISH).replace("_", " ")));
		}

		double money;
		if (command != null && !command.isEmpty()
			&& 0 < (money = getCommandCost(user))
			&& !user.canAfford(money))
		{
			throw new ChargeException(_("notEnoughMoney"));
		}

		if (exp != null && exp > 0
			&& SetExpFix.getTotalExperience(user) < exp)
		{
			throw new ChargeException(_("notEnoughExperience"));
		}
	}

	public void pay(final IUser user)
	{
		pay(user, true);
	}

	public boolean pay(final IUser user, final boolean dropItems)
	{
		boolean success = true;
		if (getMoney() != null && getMoney() > 0)
		{
			user.giveMoney(getMoney());
		}
		if (getItemStack() != null)
		{
			if (dropItems)
			{
				final Map<Integer, ItemStack> leftOver = InventoryWorkaround.addItem(user.getInventory(), true, getItemStack());
				final Location loc = user.getLocation();
				for (ItemStack itemStack : leftOver.values())
				{
					final int maxStackSize = itemStack.getType().getMaxStackSize();
					final int stacks = itemStack.getAmount() / maxStackSize;
					final int leftover = itemStack.getAmount() % maxStackSize;
					final Item[] itemStacks = new Item[stacks + (leftover > 0 ? 1 : 0)];
					for (int i = 0; i < stacks; i++)
					{
						final ItemStack stack = itemStack.clone();
						stack.setAmount(maxStackSize);
						itemStacks[i] = loc.getWorld().dropItem(loc, stack);
					}
					if (leftover > 0)
					{
						final ItemStack stack = itemStack.clone();
						stack.setAmount(leftover);
						itemStacks[stacks] = loc.getWorld().dropItem(loc, stack);
					}
				}
			}
			else
			{
				success = InventoryWorkaround.addAllItems(user.getInventory(), true, getItemStack());
			}
			user.updateInventory();
		}
		if (getExperience() != null)
		{
			SetExpFix.setTotalExperience(user, SetExpFix.getTotalExperience(user) + getExperience());
		}
		return success;
	}

	public void charge(final IUser user) throws ChargeException
	{
		if (getMoney() != null)
		{
			if (!user.canAfford(getMoney()) && getMoney() > 0)
			{
				throw new ChargeException(_("notEnoughMoney"));
			}
			user.takeMoney(getMoney());
		}
		if (getItemStack() != null)
		{
			if (!InventoryWorkaround.containsItem(user.getInventory(), true, true, itemStack))
			{
				throw new ChargeException(_("missingItems", getItemStack().getAmount(), getItemStack().getType().toString().toLowerCase(Locale.ENGLISH).replace("_", " ")));
			}
			InventoryWorkaround.removeItem(user.getInventory(), true, true, getItemStack());
			user.updateInventory();
		}
		if (command != null)
		{
			final double cost = getCommandCost(user);
			if (!user.canAfford(cost) && cost > 0)
			{
				throw new ChargeException(_("notEnoughMoney"));
			}
			user.takeMoney(cost);
		}
		if (getExperience() != null)
		{
			final int experience = SetExpFix.getTotalExperience(user);
			if (experience < getExperience() && getExperience() > 0)
			{
				throw new ChargeException(_("notEnoughExperience"));
			}
			SetExpFix.setTotalExperience(user, experience - getExperience());
		}
	}

	public Double getMoney()
	{
		return money;
	}

	public ItemStack getItemStack()
	{
		return itemStack;
	}

	public Integer getExperience()
	{
		return exp;
	}

	public Double getCommandCost(final IUser user)
	{
		double cost = 0d;
		if (command != null && !command.isEmpty()
			&& !user.isAuthorized("essentials.nocommandcost.all")
			&& !user.isAuthorized("essentials.nocommandcost." + command))
		{
			cost = ess.getSettings().getCommandCost(command.charAt(0) == '/' ? command.substring(1) : command);
			if (cost == 0.0 && fallbackCommand != null && !fallbackCommand.isEmpty())
			{
				cost = ess.getSettings().getCommandCost(fallbackCommand.charAt(0) == '/' ? fallbackCommand.substring(1) : fallbackCommand);
			}
		}
		return cost;
	}
	private static FileWriter fw = null;

	public static void log(String type, String subtype, String event, String sender, Trade charge, String receiver, Trade pay, Location loc, IEssentials ess)
	{
		if ((loc == null && !ess.getSettings().isEcoLogUpdateEnabled())
			|| (loc != null && !ess.getSettings().isEcoLogEnabled()))
		{
			return;
		}
		if (fw == null)
		{
			try
			{
				fw = new FileWriter(new File(ess.getDataFolder(), "trade.log"), true);
			}
			catch (IOException ex)
			{
				Logger.getLogger("Minecraft").log(Level.SEVERE, null, ex);
			}
		}
		StringBuilder sb = new StringBuilder();
		sb.append(type).append(",").append(subtype).append(",").append(event).append(",\"");
		sb.append(DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL).format(new Date()));
		sb.append("\",\"");
		if (sender != null)
		{
			sb.append(sender);
		}
		sb.append("\",");
		if (charge == null)
		{
			sb.append("\"\",\"\",\"\"");
		}
		else
		{
			if (charge.getItemStack() != null)
			{
				sb.append(charge.getItemStack().getAmount()).append(",");
				sb.append(charge.getItemStack().getType().toString()).append(",");
				sb.append(charge.getItemStack().getDurability());
			}
			if (charge.getMoney() != null)
			{
				sb.append(charge.getMoney()).append(",");
				sb.append("money").append(",");
				sb.append(ess.getSettings().getCurrencySymbol());
			}
			if (charge.getExperience() != null)
			{
				sb.append(charge.getExperience()).append(",");
				sb.append("exp").append(",");
				sb.append("\"\"");
			}
		}
		sb.append(",\"");
		if (receiver != null)
		{
			sb.append(receiver);
		}
		sb.append("\",");
		if (pay == null)
		{
			sb.append("\"\",\"\",\"\"");
		}
		else
		{
			if (pay.getItemStack() != null)
			{
				sb.append(pay.getItemStack().getAmount()).append(",");
				sb.append(pay.getItemStack().getType().toString()).append(",");
				sb.append(pay.getItemStack().getDurability());
			}
			if (pay.getMoney() != null)
			{
				sb.append(pay.getMoney()).append(",");
				sb.append("money").append(",");
				sb.append(ess.getSettings().getCurrencySymbol());
			}
			if (pay.getExperience() != null)
			{
				sb.append(pay.getExperience()).append(",");
				sb.append("exp").append(",");
				sb.append("\"\"");
			}
		}
		if (loc == null)
		{
			sb.append(",\"\",\"\",\"\",\"\"");
		}
		else
		{
			sb.append(",\"");
			sb.append(loc.getWorld().getName()).append("\",");
			sb.append(loc.getBlockX()).append(",");
			sb.append(loc.getBlockY()).append(",");
			sb.append(loc.getBlockZ()).append(",");
		}
		sb.append("\n");
		try
		{
			fw.write(sb.toString());
			fw.flush();
		}
		catch (IOException ex)
		{
			Logger.getLogger("Minecraft").log(Level.SEVERE, null, ex);
		}
	}

	public static void closeLog()
	{
		if (fw != null)
		{
			try
			{
				fw.close();
			}
			catch (IOException ex)
			{
				Logger.getLogger("Minecraft").log(Level.SEVERE, null, ex);
			}
			fw = null;
		}
	}
}
