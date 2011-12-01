package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import java.util.*;
import java.util.regex.Pattern;
import org.bukkit.Server;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;


public class Commandenchant extends EssentialsCommand
{
	private static final Map<String, Enchantment> ENCHANTMENTS = new HashMap<String, Enchantment>();
	private static final transient Pattern NUMPATTERN = Pattern.compile("\\d+");

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

	public Commandenchant()
	{
		super("enchant");
	}

	
	//TODO: Implement charge costs: final Trade charge = new Trade("enchant-" + enchantmentName, ess);
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
				final String enchantmentName = entry.getValue().getName().toLowerCase(Locale.ENGLISH);
				if (enchantmentslist.contains(enchantmentName) || user.isAuthorized("essentials.enchant." + enchantmentName))
				{
					enchantmentslist.add(entry.getKey());
					//enchantmentslist.add(enchantmentName);
				}
			}
			throw new NotEnoughArgumentsException(_("enchantments", Util.joinList(enchantmentslist.toArray())));
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
		Enchantment enchantment = getEnchantment(args[0], user);
		if (level < 0 || level > enchantment.getMaxLevel())
		{
			level = enchantment.getMaxLevel();
		}
		if (level == 0) {
			stack.removeEnchantment(enchantment);
		} else {
			stack.addEnchantment(enchantment, level);
		}
		user.getInventory().setItemInHand(stack);
		user.updateInventory();
		final String enchantmentName = enchantment.getName().toLowerCase(Locale.ENGLISH);
		if (level == 0) {
			user.sendMessage(_("enchantmentRemoved", enchantmentName.replace('_', ' ')));
		} else {
			user.sendMessage(_("enchantmentApplied", enchantmentName.replace('_', ' ')));
		}
	}

	public static Enchantment getEnchantment(final String name, final User user) throws Exception
	{
		
		Enchantment enchantment;
		if (NUMPATTERN.matcher(name).matches()) {
			enchantment = Enchantment.getById(Integer.parseInt(name));
		} else {
			enchantment = Enchantment.getByName(name.toUpperCase(Locale.ENGLISH));
		}
		if (enchantment == null)
		{
			enchantment = ENCHANTMENTS.get(name.toLowerCase(Locale.ENGLISH));
		}
		if (enchantment == null)
		{
			throw new Exception(_("enchantmentNotFound"));
		}
		final String enchantmentName = enchantment.getName().toLowerCase(Locale.ENGLISH);
		if (user != null && !user.isAuthorized("essentials.enchant." + enchantmentName))
		{
			throw new Exception(_("enchantmentPerm", enchantmentName));
		}
		return enchantment;
	}
}
