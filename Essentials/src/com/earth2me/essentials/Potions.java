package com.earth2me.essentials;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.bukkit.potion.PotionEffectType;


public class Potions
{
	private static final Map<String, PotionEffectType> POTIONS = new HashMap<String, PotionEffectType>();
	private static final Map<String, PotionEffectType> ALIASPOTIONS = new HashMap<String, PotionEffectType>();

	static
	{
		
		POTIONS.put("speed", PotionEffectType.SPEED);
		ALIASPOTIONS.put("fast", PotionEffectType.SPEED);
		ALIASPOTIONS.put("runfast", PotionEffectType.SPEED);
		ALIASPOTIONS.put("sprint", PotionEffectType.SPEED);
		ALIASPOTIONS.put("swift", PotionEffectType.SPEED);
		
		POTIONS.put("slowness", PotionEffectType.SLOW);
		ALIASPOTIONS.put("slow", PotionEffectType.SLOW);
		ALIASPOTIONS.put("sluggish", PotionEffectType.SLOW);
		
		POTIONS.put("haste", PotionEffectType.FAST_DIGGING);
		ALIASPOTIONS.put("superpick", PotionEffectType.FAST_DIGGING);
		ALIASPOTIONS.put("quickmine", PotionEffectType.FAST_DIGGING);
		ALIASPOTIONS.put("digspeed", PotionEffectType.FAST_DIGGING);
		ALIASPOTIONS.put("digfast", PotionEffectType.FAST_DIGGING);
		ALIASPOTIONS.put("sharp", PotionEffectType.FAST_DIGGING);
		
		POTIONS.put("fatigue", PotionEffectType.SLOW_DIGGING);
		ALIASPOTIONS.put("slow", PotionEffectType.SLOW_DIGGING);
		ALIASPOTIONS.put("dull", PotionEffectType.SLOW_DIGGING);
		
		POTIONS.put("strength", PotionEffectType.INCREASE_DAMAGE);
		ALIASPOTIONS.put("strong", PotionEffectType.INCREASE_DAMAGE);
		ALIASPOTIONS.put("bull", PotionEffectType.INCREASE_DAMAGE);
		ALIASPOTIONS.put("attack", PotionEffectType.INCREASE_DAMAGE);
		
		POTIONS.put("heal", PotionEffectType.HEAL);
		ALIASPOTIONS.put("healthy", PotionEffectType.HEAL);
		ALIASPOTIONS.put("instaheal", PotionEffectType.HEAL);
		
		POTIONS.put("harm", PotionEffectType.HARM);
		ALIASPOTIONS.put("injure", PotionEffectType.HARM);
		ALIASPOTIONS.put("damage", PotionEffectType.HARM);
		ALIASPOTIONS.put("inflict", PotionEffectType.HARM);
		
		POTIONS.put("jump", PotionEffectType.JUMP);
		ALIASPOTIONS.put("leap", PotionEffectType.JUMP);
		
		POTIONS.put("nausea", PotionEffectType.CONFUSION);
		ALIASPOTIONS.put("sick", PotionEffectType.CONFUSION);
		ALIASPOTIONS.put("sickness", PotionEffectType.CONFUSION);
		ALIASPOTIONS.put("confusion", PotionEffectType.CONFUSION);
		
		POTIONS.put("regeneration", PotionEffectType.REGENERATION);
		ALIASPOTIONS.put("regen", PotionEffectType.REGENERATION);
		
		POTIONS.put("resistance", PotionEffectType.DAMAGE_RESISTANCE);
		ALIASPOTIONS.put("dmgresist", PotionEffectType.DAMAGE_RESISTANCE);
		ALIASPOTIONS.put("armor", PotionEffectType.DAMAGE_RESISTANCE);
		ALIASPOTIONS.put("dmgresist", PotionEffectType.DAMAGE_RESISTANCE);
		
		POTIONS.put("fireresist", PotionEffectType.FIRE_RESISTANCE);
		ALIASPOTIONS.put("fireresistance", PotionEffectType.FIRE_RESISTANCE);
		ALIASPOTIONS.put("resistfire", PotionEffectType.FIRE_RESISTANCE);
		
		POTIONS.put("waterbreath", PotionEffectType.WATER_BREATHING);
		ALIASPOTIONS.put("waterbreathing", PotionEffectType.WATER_BREATHING);
		
		POTIONS.put("invisibility", PotionEffectType.INVISIBILITY);
		ALIASPOTIONS.put("invisible", PotionEffectType.INVISIBILITY);
		ALIASPOTIONS.put("invis", PotionEffectType.INVISIBILITY);
		ALIASPOTIONS.put("vanish", PotionEffectType.INVISIBILITY);
		ALIASPOTIONS.put("disappear", PotionEffectType.INVISIBILITY);
		
		POTIONS.put("blindness", PotionEffectType.BLINDNESS);
		ALIASPOTIONS.put("blind", PotionEffectType.BLINDNESS);
		
		POTIONS.put("nightvision", PotionEffectType.NIGHT_VISION);
		ALIASPOTIONS.put("vision", PotionEffectType.NIGHT_VISION);
		
		POTIONS.put("hunger", PotionEffectType.HUNGER);
		ALIASPOTIONS.put("hungry", PotionEffectType.HUNGER);
		ALIASPOTIONS.put("starve", PotionEffectType.HUNGER);
		
		POTIONS.put("weakness", PotionEffectType.WEAKNESS);
		ALIASPOTIONS.put("weak", PotionEffectType.WEAKNESS);
		
		POTIONS.put("poison", PotionEffectType.POISON);
		ALIASPOTIONS.put("venom", PotionEffectType.POISON);
		
		POTIONS.put("wither", PotionEffectType.WITHER);
		ALIASPOTIONS.put("decay", PotionEffectType.WITHER);
	}

	public static PotionEffectType getByName(String name)
	{
		PotionEffectType peffect;
		if (Util.isInt(name))
		{
			peffect = PotionEffectType.getById(Integer.parseInt(name));
		}
		else
		{
			peffect = PotionEffectType.getByName(name.toUpperCase(Locale.ENGLISH));
		}
		if (peffect == null)
		{
			peffect = POTIONS.get(name.toLowerCase(Locale.ENGLISH));
		}
		if (peffect == null)
		{
			peffect = ALIASPOTIONS.get(name.toLowerCase(Locale.ENGLISH));
		}
		return peffect;
	}

	public static Set<Entry<String, PotionEffectType>> entrySet()
	{
		return POTIONS.entrySet();
	}
}