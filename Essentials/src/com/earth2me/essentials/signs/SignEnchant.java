package com.earth2me.essentials.signs;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.*;
import java.util.Locale;
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
		final ItemStack stack = sign.getLine(1).equals("*") || sign.getLine(1).equalsIgnoreCase("any") ? null : getItemStack(sign.getLine(1), 1, ess);
		final String[] enchantLevel = sign.getLine(2).split(":");
		if (enchantLevel.length != 2)
		{
			sign.setLine(2, "§c<enchant>");
			throw new SignException(_("invalidSignLine", 3));
		}
		final Enchantment enchantment = Enchantments.getByName(enchantLevel[0]);
		if (enchantment == null)
		{
			sign.setLine(2, "§c<enchant>");
			throw new SignException(_("enchantmentNotFound"));
		}
		int level;
		try
		{
			level = Integer.parseInt(enchantLevel[1]);
		}
		catch (NumberFormatException ex)
		{
			sign.setLine(2, "§c<enchant>");
			throw new SignException(ex.getMessage(), ex);
		}
		if (level < 1 || level > enchantment.getMaxLevel())
		{
			level = enchantment.getMaxLevel();
			sign.setLine(2, enchantLevel[0] + ":" + level);
		}
		try
		{
			if (stack != null)
			{
				stack.addEnchantment(enchantment, level);
			}
		}
		catch (Throwable ex)
		{
			throw new SignException(ex.getMessage(), ex);
		}
		getTrade(sign, 3, ess);
		return true;
	}

	@Override
	protected boolean onSignInteract(ISign sign, User player, String username, IEssentials ess) throws SignException, ChargeException
	{
		final ItemStack search = sign.getLine(1).equals("*") || sign.getLine(1).equalsIgnoreCase("any") ? null : getItemStack(sign.getLine(1), 1, ess);
		int slot = -1;
		final Trade charge = getTrade(sign, 3, ess);
		charge.isAffordableFor(player);
		final String[] enchantLevel = sign.getLine(2).split(":");
		if (enchantLevel.length != 2)
		{
			throw new SignException(_("invalidSignLine", 3));
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
			level = enchantment.getMaxLevel();
		}

		final ItemStack playerHand = player.getItemInHand();
		if (playerHand == null
			|| playerHand.getAmount() != 1
			|| (playerHand.containsEnchantment(enchantment)
				&& playerHand.getEnchantmentLevel(enchantment) == level))
		{
			throw new SignException(_("missingItems", 1, sign.getLine(1)));
		}
		if (search != null && playerHand.getType() != search.getType())
		{
			throw new SignException(_("missingItems", 1, search.getType().toString().toLowerCase(Locale.ENGLISH).replace('_', ' ')));
		}

		final ItemStack toEnchant = playerHand;
		try
		{
			toEnchant.addEnchantment(enchantment, level);
		}
		catch (Exception ex)
		{
			throw new SignException(ex.getMessage(), ex);
		}

		charge.charge(player);
		Trade.log("Sign", "Enchant", "Interact", username, charge, username, charge, sign.getBlock().getLocation(), ess);
		player.updateInventory();
		return true;
	}
}
