package com.earth2me.essentials.commands;

import com.earth2me.essentials.*;
import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.utils.StringUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.ess3.api.IUser;
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
		if (args.length < 1 || args[0].equalsIgnoreCase("hand") || !user.isAuthorized("essentials.repair.all"))
		{
			repairHand(user);
		}
		else if (args[0].equalsIgnoreCase("all"))
		{
			final Trade charge = new Trade("repair-all", ess);
			charge.isAffordableFor(user);
			repairAll(user);
			charge.charge(user);
		}
		else
		{
			throw new NotEnoughArgumentsException();
		}
	}

	public void repairHand(User user) throws Exception
	{
		final ItemStack item = user.getItemInHand();
		if (item == null || item.getType().isBlock() || item.getDurability() == 0)
		{
			throw new Exception(_("repairInvalidType"));
		}

		if (!item.getEnchantments().isEmpty()
			&& !ess.getSettings().getRepairEnchanted()
			&& !user.isAuthorized("essentials.repair.enchanted"))
		{
			throw new Exception(_("repairEnchanted"));
		}

		final String itemName = item.getType().toString().toLowerCase(Locale.ENGLISH);
		final Trade charge = new Trade("repair-" + itemName.replace('_', '-'), new Trade("repair-" + item.getTypeId(), new Trade("repair-item", ess), ess), ess);

		charge.isAffordableFor(user);

		repairItem(item);

		charge.charge(user);
		user.updateInventory();
		user.sendMessage(_("repair", itemName.replace('_', ' ')));
	}

	public void repairAll(User user) throws Exception
	{
		final List<String> repaired = new ArrayList<String>();
		repairItems(user.getInventory().getContents(), user, repaired);

		if (user.isAuthorized("essentials.repair.armor"))
		{
			repairItems(user.getInventory().getArmorContents(), user, repaired);
		}
		
		user.updateInventory();
		if (repaired.isEmpty())
		{
			throw new Exception(_("repairNone"));
		}
		else
		{
			user.sendMessage(_("repair", StringUtil.joinList(repaired)));
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
			if (item == null || item.getType().isBlock() || item.getDurability() == 0)
			{
				continue;
			}
			final String itemName = item.getType().toString().toLowerCase(Locale.ENGLISH);
			final Trade charge = new Trade("repair-" + itemName.replace('_', '-'), new Trade("repair-" + item.getTypeId(), new Trade("repair-item", ess), ess), ess);
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
				&& !ess.getSettings().getRepairEnchanted()
				&& !user.isAuthorized("essentials.repair.enchanted"))
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
