package com.earth2me.essentials.signs;

import com.earth2me.essentials.ChargeException;
import com.earth2me.essentials.Enchantments;
import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.User;
import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.craftbukkit.InventoryWorkaround;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;


public class SignEnchant extends EssentialsSign
{
	public SignEnchant()
	{
		super("Enchant");
	}

	@Override
	protected boolean onSignCreate(final ISign sign, final User player, final String username, final IEssentials ess) throws SignException, ChargeException
	{
		final ItemStack stack = getItemStack(sign.getLine(1), 1, ess);
		final String[] enchantLevel = sign.getLine(2).split(":");
		if (enchantLevel.length != 2)
		{
			throw new SignException(_("invalidSignLine", 2));
		}
		final Enchantment enchantment = Enchantments.getByName(enchantLevel[0]);
		if (enchantment == null)
		{
			throw new SignException(_("enchantmentNotFound"));
		}
		int level;
		try
		{
			level = Integer.parseInt(enchantLevel[1]);
		}
		catch (NumberFormatException ex)
		{
			throw new SignException(ex.getMessage());
		}
		if (level < 1 || level > enchantment.getMaxLevel())
		{
			level = enchantment.getMaxLevel();
			sign.setLine(2, enchantLevel[0] + ":" + level);
		}
		try
		{
			stack.addEnchantment(enchantment, level);
		}
		catch (Throwable ex)
		{
			throw new SignException(ex.getMessage());
		}
		getTrade(sign, 3, ess);
		return true;
	}

	@Override
	protected boolean onSignInteract(ISign sign, User player, String username, IEssentials ess) throws SignException, ChargeException
	{
		final ItemStack search = getItemStack(sign.getLine(1), 1, ess);
		int slot = -1;
		final Trade charge = getTrade(sign, 3, ess);
		charge.isAffordableFor(player);
		if (InventoryWorkaround.containsItem(player.getInventory(), false, search))
		{
			slot = InventoryWorkaround.first(player.getInventory(), search, false, true);
		}
		if (slot == -1)
		{
			throw new SignException(_("missingItems", 1, search.toString()));
		}
		final String[] enchantLevel = sign.getLine(2).split(":");
		if (enchantLevel.length != 2)
		{
			throw new SignException(_("invalidSignLine", 2));
		}
		final Enchantment enchantment = Enchantments.getByName(enchantLevel[0]);
		if (enchantment == null)
		{
			throw new SignException(_("enchantmentNotFound"));
		}

		final ItemStack toEnchant = player.getInventory().getItem(slot);
		try
		{
			toEnchant.addEnchantment(enchantment, Integer.parseInt(enchantLevel[1]));
		}
		catch (NumberFormatException ex)
		{
			toEnchant.addEnchantment(enchantment, enchantment.getMaxLevel());
		}

		charge.charge(player);
		Trade.log("Sign", "Enchant", "Interact", username, charge, username, charge, sign.getBlock().getLocation(), ess);
		player.getInventory().setItem(slot, toEnchant);
		player.updateInventory();
		return true;
	}
}
