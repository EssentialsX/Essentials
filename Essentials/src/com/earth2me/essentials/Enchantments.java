package com.earth2me.essentials;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;
import org.bukkit.enchantments.Enchantment;


public class Enchantments
{
	private static final transient Pattern NUMPATTERN = Pattern.compile("\\d+");
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
		ENCHANTMENTS.put("featherfall", Enchantment.PROTECTION_FALL);
		ENCHANTMENTS.put("featherfalling", Enchantment.PROTECTION_FALL);
		
		ENCHANTMENTS.put("fireprotection", Enchantment.PROTECTION_FIRE);
		ENCHANTMENTS.put("fireprot", Enchantment.PROTECTION_FIRE);
		
		ENCHANTMENTS.put("projectileprotection", Enchantment.PROTECTION_PROJECTILE);
		ENCHANTMENTS.put("projprot", Enchantment.PROTECTION_PROJECTILE);
		
		ENCHANTMENTS.put("silktouch", Enchantment.SILK_TOUCH);
		
		ENCHANTMENTS.put("waterworker", Enchantment.WATER_WORKER);
		ENCHANTMENTS.put("aquaaffinity", Enchantment.WATER_WORKER);
		
		ENCHANTMENTS.put("firearrow", Enchantment.ARROW_FIRE);
		ENCHANTMENTS.put("flame", Enchantment.ARROW_FIRE);
		
		ENCHANTMENTS.put("arrowdamage", Enchantment.ARROW_DAMAGE);
		ENCHANTMENTS.put("power", Enchantment.ARROW_DAMAGE);
		
		ENCHANTMENTS.put("arrowknockback", Enchantment.ARROW_KNOCKBACK);
		ENCHANTMENTS.put("arrowkb", Enchantment.ARROW_KNOCKBACK);
		ENCHANTMENTS.put("punch", Enchantment.ARROW_KNOCKBACK);
		
		ENCHANTMENTS.put("infinitearrows", Enchantment.ARROW_INFINITE);
		ENCHANTMENTS.put("infarrows", Enchantment.ARROW_INFINITE);
		ENCHANTMENTS.put("infinity", Enchantment.ARROW_INFINITE);
	}
	
	public static Enchantment getByName(String name) {
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
		return enchantment;
	}

	public static Set<Entry<String, Enchantment>> entrySet()
	{
		return ENCHANTMENTS.entrySet();
	}
}
