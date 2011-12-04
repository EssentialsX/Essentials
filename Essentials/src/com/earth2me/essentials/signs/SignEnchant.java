package com.earth2me.essentials.signs;

import com.earth2me.essentials.ChargeException;
import com.earth2me.essentials.Enchantments;
import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.User;
import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.Trade;
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
		final Material search = getItemStack(sign.getLine(1), 1, ess).getType();
		int slot;
		final Trade charge = getTrade(sign, 3, ess);
		charge.isAffordableFor(player);
		if (player.getInventory().contains(search))
		{
			slot = player.getInventory().first(search);
		}
		else
		{
			player.sendMessage(_("missingItems", 1, search.toString()));
			return true;
		}
		final String[] enchantLevel = sign.getLine(2).split(":");
		if (enchantLevel.length != 2)
		{
			player.sendMessage(_("invalidSignLine", 2));
			return true;
		}
		final Enchantment enchantment = Enchantments.getByName(enchantLevel[0]);
		if (enchantment == null)
		{
			player.sendMessage(_("enchantmentNotFound"));
			return true;
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
