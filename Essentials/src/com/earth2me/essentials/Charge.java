package com.earth2me.essentials;

import com.earth2me.essentials.commands.EssentialsCommand;
import org.bukkit.inventory.ItemStack;


public class Charge
{
	private String command = null;
	private Double costs = null;
	private ItemStack items = null;
	private Essentials ess = Essentials.getStatic();

	public Charge(String command)
	{
		this.command = command;
	}

	public Charge(double money)
	{
		this.costs = money;
	}

	public Charge(ItemStack items)
	{
		this.items = items;
	}

	public Charge(EssentialsCommand command)
	{
		this.command = command.getName();
	}

	public void isAffordableFor(User user) throws Exception
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

	public void charge(User user) throws Exception
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
