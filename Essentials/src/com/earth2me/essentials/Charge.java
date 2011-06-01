package com.earth2me.essentials;

import org.bukkit.inventory.ItemStack;


public class Charge
{
	private final String command;
	private final Double costs;
	private final ItemStack items;
	private final IEssentials ess;

	public Charge(String command, IEssentials ess)
	{
		this(command, null, null, ess);
	}

	public Charge(double money, IEssentials ess)
	{
		this(null, money, null, ess);
	}

	public Charge(ItemStack items, IEssentials ess)
	{
		this(null, null, items, ess);
	}
	
	private Charge(String command, Double money, ItemStack item, IEssentials ess)
	{
		this.command = command;
		this.costs = money;
		this.items = item;
		this.ess = ess;
	}

	public void isAffordableFor(IUser user) throws Exception
	{
		double mon = user.getMoney();
		if (costs != null)
		{
			if (mon < costs && !user.isAuthorized("essentials.eco.loan"))
			{
				throw new Exception(Util.i18n("notEnoughMoney"));
			}
		}
		if (items != null)
		{
			if (!InventoryWorkaround.containsItem(user.getInventory(), true, items))
			{
				throw new Exception(Util.format("missingItems", items.getAmount(), items.getType().toString().toLowerCase().replace("_", " ")));
			}
		}
		if (command != null && !command.isEmpty())
		{
			if (user.isAuthorized("essentials.nocommandcost.all")
				|| user.isAuthorized("essentials.nocommandcost." + command))
			{
				return;
			}
			double cost = ess.getSettings().getCommandCost(command.startsWith("/") ? command.substring(1) : command);
			if (mon < cost && !user.isAuthorized("essentials.eco.loan"))
			{
				throw new Exception(Util.i18n("notEnoughMoney"));
			}
		}
	}

	public void charge(IUser user) throws Exception
	{
		double mon = user.getMoney();
		if (costs != null)
		{
			if (mon < costs && !user.isAuthorized("essentials.eco.loan"))
			{
				throw new Exception(Util.i18n("notEnoughMoney"));
			}
			user.takeMoney(costs);
		}
		if (items != null)
		{
			if (!InventoryWorkaround.containsItem(user.getInventory(), true, items))
			{
				throw new Exception(Util.format("missingItems", items.getAmount(), items.getType().toString().toLowerCase().replace("_", " ")));
			}
			InventoryWorkaround.removeItem(user.getInventory(), true, items);
			user.updateInventory();
		}
		if (command != null && !command.isEmpty())
		{
			if (user.isAuthorized("essentials.nocommandcost.all")
				|| user.isAuthorized("essentials.nocommandcost." + command))
			{
				return;
			}

			double cost = ess.getSettings().getCommandCost(command.startsWith("/") ? command.substring(1) : command);
			if (mon < cost && !user.isAuthorized("essentials.eco.loan"))
			{
				throw new Exception(Util.i18n("notEnoughMoney"));
			}
			user.takeMoney(cost);
		}
	}
}
