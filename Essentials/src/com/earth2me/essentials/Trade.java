package com.earth2me.essentials;

import org.bukkit.inventory.ItemStack;


public class Charge
{
	private final transient String command;
	private final transient Double costs;
	private final transient ItemStack items;
	private final transient IEssentials ess;

	public Charge(final String command, final IEssentials ess)
	{
		this(command, null, null, ess);
	}

	public Charge(final double money, final IEssentials ess)
	{
		this(null, money, null, ess);
	}

	public Charge(final ItemStack items, final IEssentials ess)
	{
		this(null, null, items, ess);
	}

	private Charge(final String command, final Double money, final ItemStack item, final IEssentials ess)
	{
		this.command = command;
		this.costs = money;
		this.items = item;
		this.ess = ess;
	}

	public void isAffordableFor(final IUser user) throws ChargeException
	{
		final double mon = user.getMoney();
		if (costs != null
			&& mon < costs
			&& !user.isAuthorized("essentials.eco.loan"))
		{
			throw new ChargeException(Util.i18n("notEnoughMoney"));
		}

		if (items != null
			&& !InventoryWorkaround.containsItem(user.getInventory(), true, items))
		{
			throw new ChargeException(Util.format("missingItems", items.getAmount(), items.getType().toString().toLowerCase().replace("_", " ")));
		}

		if (command != null && !command.isEmpty()
			&& !user.isAuthorized("essentials.nocommandcost.all")
			&& !user.isAuthorized("essentials.nocommandcost." + command)
			&& mon < ess.getSettings().getCommandCost(command.charAt(0) == '/' ? command.substring(1) : command)
			&& !user.isAuthorized("essentials.eco.loan"))
		{
			throw new ChargeException(Util.i18n("notEnoughMoney"));
		}
	}

	public void charge(final IUser user) throws ChargeException
	{
		if (costs != null)
		{
			final double mon = user.getMoney();
			if (mon < costs && !user.isAuthorized("essentials.eco.loan"))
			{
				throw new ChargeException(Util.i18n("notEnoughMoney"));
			}
			user.takeMoney(costs);
		}
		if (items != null)
		{
			if (!InventoryWorkaround.containsItem(user.getInventory(), true, items))
			{
				throw new ChargeException(Util.format("missingItems", items.getAmount(), items.getType().toString().toLowerCase().replace("_", " ")));
			}
			InventoryWorkaround.removeItem(user.getInventory(), true, items);
			user.updateInventory();
		}
		if (command != null && !command.isEmpty()
			&& !user.isAuthorized("essentials.nocommandcost.all")
			&& !user.isAuthorized("essentials.nocommandcost." + command))
		{
			final double mon = user.getMoney();
			final double cost = ess.getSettings().getCommandCost(command.charAt(0) == '/' ? command.substring(1) : command);
			if (mon < cost && !user.isAuthorized("essentials.eco.loan"))
			{
				throw new ChargeException(Util.i18n("notEnoughMoney"));
			}
			user.takeMoney(cost);
		}
	}
}
