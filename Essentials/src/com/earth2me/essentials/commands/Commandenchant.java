package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.bukkit.Server;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import static com.earth2me.essentials.I18n._;


public class Commandenchant extends EssentialsCommand
{
	private static final Map<String, Enchantment> ENCHANTMENTS = new HashMap<String, Enchantment>();

	static
	{
		ENCHANTMENTS.put("alldamage", Enchantment.DAMAGE_ALL);
		ENCHANTMENTS.put("alldmg", Enchantment.DAMAGE_ALL);
		ENCHANTMENTS.put("arthropodsdamage", Enchantment.DAMAGE_ARTHROPODS);
		ENCHANTMENTS.put("ardmg", Enchantment.DAMAGE_ARTHROPODS);
		ENCHANTMENTS.put("undeaddamage", Enchantment.DAMAGE_UNDEAD);
		ENCHANTMENTS.put("undeaddmg", Enchantment.DAMAGE_UNDEAD);
		ENCHANTMENTS.put("digspeed", Enchantment.DIG_SPEED);
		ENCHANTMENTS.put("durability", Enchantment.DURABILITY);
		ENCHANTMENTS.put("dura", Enchantment.DURABILITY);
		ENCHANTMENTS.put("fireaspect", Enchantment.FIRE_ASPECT);
		ENCHANTMENTS.put("fire", Enchantment.FIRE_ASPECT);
		ENCHANTMENTS.put("knockback", Enchantment.KNOCKBACK);
		ENCHANTMENTS.put("blockslootbonus", Enchantment.LOOT_BONUS_BLOCKS);
		ENCHANTMENTS.put("blocksbonus", Enchantment.LOOT_BONUS_BLOCKS);
		ENCHANTMENTS.put("mobslootbonus", Enchantment.LOOT_BONUS_MOBS);
		ENCHANTMENTS.put("mobsbonus", Enchantment.LOOT_BONUS_MOBS);
		ENCHANTMENTS.put("oxygen", Enchantment.OXYGEN);
		ENCHANTMENTS.put("environmentalprotection", Enchantment.PROTECTION_ENVIRONMENTAL);
		ENCHANTMENTS.put("envprot", Enchantment.PROTECTION_ENVIRONMENTAL);
		ENCHANTMENTS.put("explosionsprotection", Enchantment.PROTECTION_EXPLOSIONS);
		ENCHANTMENTS.put("expprot", Enchantment.PROTECTION_EXPLOSIONS);
		ENCHANTMENTS.put("fallprotection", Enchantment.PROTECTION_FALL);
		ENCHANTMENTS.put("fallprot", Enchantment.PROTECTION_FALL);
		ENCHANTMENTS.put("fireprotection", Enchantment.PROTECTION_FIRE);
		ENCHANTMENTS.put("fireprot", Enchantment.PROTECTION_FIRE);
		ENCHANTMENTS.put("projectileprotection", Enchantment.PROTECTION_PROJECTILE);
		ENCHANTMENTS.put("projprot", Enchantment.PROTECTION_PROJECTILE);
		ENCHANTMENTS.put("silktouch", Enchantment.SILK_TOUCH);
		ENCHANTMENTS.put("waterworker", Enchantment.WATER_WORKER);
	}

	public Commandenchant()
	{
		super("enchant");
	}

	@Override
	protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{
		final ItemStack stack = user.getItemInHand();
		if (stack == null)
		{
			throw new Exception(_("nothingInHand"));
		}
		if (args.length == 0)
		{
			final Set<String> enchantmentslist = new TreeSet<String>();
			for (Map.Entry<String, Enchantment> entry : ENCHANTMENTS.entrySet())
			{
				final String enchantmentName = entry.getValue().getName().toLowerCase();
				if (enchantmentslist.contains(enchantmentName) || user.isAuthorized("essentials.enchant." + enchantmentName))
				{
					enchantmentslist.add(entry.getKey());
					enchantmentslist.add(enchantmentName);
				}
			}
			throw new NotEnoughArgumentsException(_("entchantments", Util.joinList(enchantmentslist.toArray())));
		}
		int level = -1;
		if (args.length > 1)
		{
			try
			{
				level = Integer.parseInt(args[1]);
			}
			catch (NumberFormatException ex)
			{
				level = -1;
			}
		}
		Enchantment enchantment = Enchantment.getByName(args[0].toUpperCase(Locale.ENGLISH));
		if (enchantment == null)
		{
			enchantment = ENCHANTMENTS.get(args[0].toLowerCase(Locale.ENGLISH));
		}
		if (enchantment == null)
		{
			throw new Exception(_("enchantmentNotFound"));
		}
		final String enchantmentName = enchantment.getName().toLowerCase();
		if (!user.isAuthorized("essentials.enchant." + enchantmentName))
		{
			throw new Exception(_("enchantmentPerm", enchantmentName));
		}
		if (level < enchantment.getStartLevel() || level > enchantment.getMaxLevel())
		{
			level = enchantment.getMaxLevel();
		}
		stack.addEnchantment(enchantment, level);
		user.setItemInHand(stack);
		user.updateInventory();
		user.sendMessage(_("enchantmentApplied", enchantmentName.replace('_', ' ')));
	}
}
