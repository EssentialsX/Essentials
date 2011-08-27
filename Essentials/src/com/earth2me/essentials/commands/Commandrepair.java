package com.earth2me.essentials.commands;

import com.earth2me.essentials.ChargeException;
import com.earth2me.essentials.IUser;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;


public class Commandrepair extends EssentialsCommand
{
	public Commandrepair()
	{
		super("repair");
	}

	@Override
	public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}

		if (args[0].equalsIgnoreCase("hand"))
		{
			final ItemStack item = user.getItemInHand();
			final String itemName = item.getType().toString().toLowerCase();
			final Trade charge = new Trade("repair-" + itemName.replace('_', '-'), ess);

			charge.isAffordableFor(user);

			repairItem(item);

			charge.charge(user);

			user.sendMessage(Util.format("repair", itemName.replace('_', ' ')));
		}
		else if (args[0].equalsIgnoreCase("all"))
		{
			final List<String> repaired = new ArrayList<String>();
			repairItems(user.getInventory().getContents(), user, repaired);

			repairItems(user.getInventory().getArmorContents(), user, repaired);

			if (repaired.isEmpty())
			{
				throw new Exception(Util.format("repairNone"));
			}
			else
			{
				user.sendMessage(Util.format("repair", Util.joinList(repaired)));
			}

		}
		else
		{
			throw new NotEnoughArgumentsException();
		}
	}

	private void repairItem(final ItemStack item) throws Exception
	{
		final Material material = Material.getMaterial(item.getTypeId());
		if (material.isBlock() || material.getMaxDurability() < 0)
		{
			throw new Exception(Util.i18n("repairInvalidType"));
		}

		if (item.getDurability() == 0)
		{
			throw new Exception(Util.i18n("repairAlreadyFixed"));
		}

		item.setDurability((short)0);
	}

	private void repairItems(final ItemStack[] items, final IUser user, final List<String> repaired)
	{
		for (ItemStack item : items)
		{
			final String itemName = item.getType().toString().toLowerCase();
			final Trade charge = new Trade("repair-" + itemName.replace('_', '-'), ess);
			try
			{
				charge.isAffordableFor(user);
			}
			catch (ChargeException ex)
			{
				user.sendMessage(ex.getMessage());
				continue;
			}

			try
			{
				repairItem(item);
			}
			catch (Exception e)
			{
				continue;
			}
			try
			{
				charge.charge(user);
			}
			catch (ChargeException ex)
			{
				user.sendMessage(ex.getMessage());
			}
			repaired.add(itemName.replace('_', ' '));
		}
	}
}
