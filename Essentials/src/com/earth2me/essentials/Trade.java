package com.earth2me.essentials;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.inventory.ItemStack;


public class Trade
{
	private final transient String command;
	private final transient Double money;
	private final transient ItemStack itemStack;
	private final transient IEssentials ess;

	public Trade(final String command, final IEssentials ess)
	{
		this(command, null, null, ess);
	}

	public Trade(final double money, final IEssentials ess)
	{
		this(null, money, null, ess);
	}

	public Trade(final ItemStack items, final IEssentials ess)
	{
		this(null, null, items, ess);
	}

	private Trade(final String command, final Double money, final ItemStack item, final IEssentials ess)
	{
		this.command = command;
		this.money = money;
		this.itemStack = item;
		this.ess = ess;
	}

	public void isAffordableFor(final IUser user) throws ChargeException
	{
		final double mon = user.getMoney();
		if (getMoney() != null
			&& mon < getMoney()
			&& getMoney() > 0
			&& !user.isAuthorized("essentials.eco.loan"))
		{
			throw new ChargeException(Util.i18n("notEnoughMoney"));
		}

		if (getItemStack() != null
			&& !InventoryWorkaround.containsItem(user.getInventory(), true, itemStack))
		{
			throw new ChargeException(Util.format("missingItems", getItemStack().getAmount(), getItemStack().getType().toString().toLowerCase().replace("_", " ")));
		}

		if (command != null && !command.isEmpty()
			&& !user.isAuthorized("essentials.nocommandcost.all")
			&& !user.isAuthorized("essentials.nocommandcost." + command)
			&& mon < ess.getSettings().getCommandCost(command.charAt(0) == '/' ? command.substring(1) : command)
			&& 0 < ess.getSettings().getCommandCost(command.charAt(0) == '/' ? command.substring(1) : command)
			&& !user.isAuthorized("essentials.eco.loan"))
		{
			throw new ChargeException(Util.i18n("notEnoughMoney"));
		}
	}

	public void pay(final IUser user)
	{
		if (getMoney() != null && getMoney() > 0)
		{
			user.giveMoney(getMoney());
		}
		if (getItemStack() != null)
		{
			final Map<Integer, ItemStack> leftOver = InventoryWorkaround.addItem(user.getInventory(), true, getItemStack());
			for (ItemStack itemStack : leftOver.values())
			{
				InventoryWorkaround.dropItem(user.getLocation(), itemStack);
			}
			user.updateInventory();
		}
	}

	public void charge(final IUser user) throws ChargeException
	{
		if (getMoney() != null)
		{
			final double mon = user.getMoney();
			if (mon < getMoney() && getMoney() > 0 && !user.isAuthorized("essentials.eco.loan"))
			{
				throw new ChargeException(Util.i18n("notEnoughMoney"));
			}
			user.takeMoney(getMoney());
		}
		if (getItemStack() != null)
		{
			if (!InventoryWorkaround.containsItem(user.getInventory(), true, itemStack))
			{
				throw new ChargeException(Util.format("missingItems", getItemStack().getAmount(), getItemStack().getType().toString().toLowerCase().replace("_", " ")));
			}
			InventoryWorkaround.removeItem(user.getInventory(), true, getItemStack());
			user.updateInventory();
		}
		if (command != null && !command.isEmpty()
			&& !user.isAuthorized("essentials.nocommandcost.all")
			&& !user.isAuthorized("essentials.nocommandcost." + command))
		{
			final double mon = user.getMoney();
			final double cost = ess.getSettings().getCommandCost(command.charAt(0) == '/' ? command.substring(1) : command);
			if (mon < cost && cost > 0 && !user.isAuthorized("essentials.eco.loan"))
			{
				throw new ChargeException(Util.i18n("notEnoughMoney"));
			}
			user.takeMoney(cost);
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
	private static FileWriter fw = null;

	public static void log(String type, String subtype, String event, String sender, Trade charge, String receiver, Trade pay, IEssentials ess)
	{
		if (!ess.getSettings().isEcoLogEnabled())
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
		sb.append(type).append(",").append(subtype).append(",").append("event").append(",\"");
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
		if (fw != null) {
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
