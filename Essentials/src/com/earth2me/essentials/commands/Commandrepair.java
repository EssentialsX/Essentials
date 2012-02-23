package com.earth2me.essentials.commands;

import com.earth2me.essentials.api.ChargeException;
import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.Util;
import com.earth2me.essentials.api.IUser;
import com.earth2me.essentials.perm.Permissions;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;


public class Commandrepair extends EssentialsCommand
{
	@Override
	public void run(final IUser user, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}

		if (args[0].equalsIgnoreCase("hand"))
		{
			final ItemStack item = user.getItemInHand();
			if (item == null)
			{
				throw new Exception(_("repairInvalidType"));
			}

			if (!item.getEnchantments().isEmpty()
				&& !Permissions.REPAIR_ENCHANTED.isAuthorized(user))
			{
				throw new Exception(_("repairEnchanted"));
			}

			final String itemName = item.getType().toString().toLowerCase(Locale.ENGLISH);
			final Trade charge = new Trade("repair-" + itemName.replace('_', '-'), ess);

			charge.isAffordableFor(user);

			repairItem(item);

			charge.charge(user);

			user.sendMessage(_("repair", itemName.replace('_', ' ')));
		}
		else if (args[0].equalsIgnoreCase("all"))
		{
			final List<String> repaired = new ArrayList<String>();
			repairItems(user.getInventory().getContents(), user, repaired);

			if (Permissions.REPAIR_ARMOR.isAuthorized(user))
			{
				repairItems(user.getInventory().getArmorContents(), user, repaired);
			}

			if (repaired.isEmpty())
			{
				throw new Exception(_("repairNone"));
			}
			else
			{
				user.sendMessage(_("repair", Util.joinList(repaired)));
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
		if (material.isBlock() || material.getMaxDurability() < 1)
		{
			throw new Exception(_("repairInvalidType"));
		}

		if (item.getDurability() == 0)
		{
			throw new Exception(_("repairAlreadyFixed"));
		}

		item.setDurability((short)0);
	}

	private void repairItems(final ItemStack[] items, final IUser user, final List<String> repaired)
	{
		for (ItemStack item : items)
		{
			if (item == null)
			{
				continue;
			}
			final String itemName = item.getType().toString().toLowerCase(Locale.ENGLISH);
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
			if (!item.getEnchantments().isEmpty()
				&& !Permissions.REPAIR_ENCHANTED.isAuthorized(user))
			{
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
