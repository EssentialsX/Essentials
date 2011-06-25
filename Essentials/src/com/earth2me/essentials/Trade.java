package com.earth2me.essentials;

import java.util.Map;
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
}
