package com.earth2me.essentials.signs;

import com.earth2me.essentials.ChargeException;
import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.User;
import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.Trade;
import java.util.HashMap;
import java.util.Map;
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
		getItemStack(sign.getLine(1), 1, ess);
		String[] enchantLevel = sign.getLine(2).split(":");
		if (enchantLevel.length != 2)
		{
			throw new SignException(_("invalidSignLine", 2));
		}
		if (!ENCHANTMENTS.containsKey(enchantLevel[0]))
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
		if (level < 1 || level > ENCHANTMENTS.get(enchantLevel[0]).getMaxLevel())
		{
			sign.setLine(2, enchantLevel[0] + ":" + ENCHANTMENTS.get(enchantLevel[0]).getMaxLevel());
		}
		getTrade(sign, 3, ess);
		return true;
	}

	@Override
	protected boolean onSignInteract(ISign sign, User player, String username, IEssentials ess) throws SignException, ChargeException
	{
		Material search = getItemStack(sign.getLine(1), 1, ess).getType();
		int slot;
		Trade charge = getTrade(sign, 3, ess);
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
		ItemStack toEnchant = player.getInventory().getItem(slot);
		String[] enchantLevel = sign.getLine(2).split(":");
		if (enchantLevel.length != 2)
		{
			player.sendMessage(_("invalidSignLine", 2));
			return true;
		}
		if (ENCHANTMENTS.containsKey(enchantLevel[0]))
		{
			try
			{
				toEnchant.addEnchantment(ENCHANTMENTS.get(enchantLevel[0]), Integer.parseInt(enchantLevel[1]));
			}
			catch (NumberFormatException ex)
			{
				toEnchant.addEnchantment(ENCHANTMENTS.get(enchantLevel[0]), 1);
			}
		}
		else
		{
			player.sendMessage(_("enchantmentNotFound"));
			return true;
		}
		charge.charge(player);
		Trade.log("Sign", "Enchant", "Interact", username, charge, username, charge, sign.getBlock().getLocation(), ess);
		return true;
	}
	private static final Map<String, Enchantment> ENCHANTMENTS = new HashMap<String, Enchantment>();

	static
	{
		ENCHANTMENTS.put("alldamage", Enchantment.DAMAGE_ALL);
		ENCHANTMENTS.put("alldmg", Enchantment.DAMAGE_ALL);
		ENCHANTMENTS.put("sharpness", Enchantment.DAMAGE_ALL);
		ENCHANTMENTS.put("arthropodsdamage", Enchantment.DAMAGE_ARTHROPODS);
		ENCHANTMENTS.put("ardmg", Enchantment.DAMAGE_ARTHROPODS);
		ENCHANTMENTS.put("baneofarthropods", Enchantment.DAMAGE_ARTHROPODS);
		ENCHANTMENTS.put("undeaddamage", Enchantment.DAMAGE_UNDEAD);
		ENCHANTMENTS.put("smite", Enchantment.DAMAGE_UNDEAD);
		ENCHANTMENTS.put("digspeed", Enchantment.DIG_SPEED);
		ENCHANTMENTS.put("efficiency", Enchantment.DIG_SPEED);
		ENCHANTMENTS.put("durability", Enchantment.DURABILITY);
		ENCHANTMENTS.put("dura", Enchantment.DURABILITY);
		ENCHANTMENTS.put("unbreaking", Enchantment.DURABILITY);
		ENCHANTMENTS.put("fireaspect", Enchantment.FIRE_ASPECT);
		ENCHANTMENTS.put("fire", Enchantment.FIRE_ASPECT);
		ENCHANTMENTS.put("knockback", Enchantment.KNOCKBACK);
		ENCHANTMENTS.put("blockslootbonus", Enchantment.LOOT_BONUS_BLOCKS);
		ENCHANTMENTS.put("fortune", Enchantment.LOOT_BONUS_BLOCKS);
		ENCHANTMENTS.put("mobslootbonus", Enchantment.LOOT_BONUS_MOBS);
		ENCHANTMENTS.put("mobloot", Enchantment.LOOT_BONUS_MOBS);
		ENCHANTMENTS.put("looting", Enchantment.LOOT_BONUS_MOBS);
		ENCHANTMENTS.put("oxygen", Enchantment.OXYGEN);
		ENCHANTMENTS.put("respiration", Enchantment.OXYGEN);
		ENCHANTMENTS.put("protection", Enchantment.PROTECTION_ENVIRONMENTAL);
		ENCHANTMENTS.put("prot", Enchantment.PROTECTION_ENVIRONMENTAL);
		ENCHANTMENTS.put("explosionsprotection", Enchantment.PROTECTION_EXPLOSIONS);
		ENCHANTMENTS.put("expprot", Enchantment.PROTECTION_EXPLOSIONS);
		ENCHANTMENTS.put("blastprotection", Enchantment.PROTECTION_EXPLOSIONS);
		ENCHANTMENTS.put("fallprotection", Enchantment.PROTECTION_FALL);
		ENCHANTMENTS.put("fallprot", Enchantment.PROTECTION_FALL);
		ENCHANTMENTS.put("featherfalling", Enchantment.PROTECTION_FALL);
		ENCHANTMENTS.put("fireprotection", Enchantment.PROTECTION_FIRE);
		ENCHANTMENTS.put("fireprot", Enchantment.PROTECTION_FIRE);
		ENCHANTMENTS.put("projectileprotection", Enchantment.PROTECTION_PROJECTILE);
		ENCHANTMENTS.put("projprot", Enchantment.PROTECTION_PROJECTILE);
		ENCHANTMENTS.put("silktouch", Enchantment.SILK_TOUCH);
		ENCHANTMENTS.put("waterworker", Enchantment.WATER_WORKER);
		ENCHANTMENTS.put("aquaaffinity", Enchantment.WATER_WORKER);
	}
}
