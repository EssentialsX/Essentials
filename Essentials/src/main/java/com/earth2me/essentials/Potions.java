package com.earth2me.essentials;

import com.earth2me.essentials.utils.NumberUtil;
import com.earth2me.essentials.utils.RegistryUtil;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public final class Potions {
    private static final Map<String, PotionEffectType> POTIONS = new HashMap<>();
    private static final Map<String, PotionEffectType> ALIASPOTIONS = new HashMap<>();

    static {

        POTIONS.put("speed", PotionEffectType.SPEED);
        ALIASPOTIONS.put("fast", PotionEffectType.SPEED);
        ALIASPOTIONS.put("runfast", PotionEffectType.SPEED);
        ALIASPOTIONS.put("sprint", PotionEffectType.SPEED);
        ALIASPOTIONS.put("swift", PotionEffectType.SPEED);

        final PotionEffectType SLOWNESS = RegistryUtil.valueOf(PotionEffectType.class, "SLOW", "SLOWNESS");

        POTIONS.put("slowness", SLOWNESS);
        ALIASPOTIONS.put("slow", SLOWNESS);
        ALIASPOTIONS.put("sluggish", SLOWNESS);

        final PotionEffectType HASTE = RegistryUtil.valueOf(PotionEffectType.class, "FAST_DIGGING", "HASTE");

        POTIONS.put("haste", HASTE);
        ALIASPOTIONS.put("superpick", HASTE);
        ALIASPOTIONS.put("quickmine", HASTE);
        ALIASPOTIONS.put("digspeed", HASTE);
        ALIASPOTIONS.put("digfast", HASTE);
        ALIASPOTIONS.put("sharp", HASTE);

        final PotionEffectType MINING_FATIGUE = RegistryUtil.valueOf(PotionEffectType.class, "SLOW_DIGGING", "MINING_FATIGUE");

        POTIONS.put("fatigue", MINING_FATIGUE);
        ALIASPOTIONS.put("slow", MINING_FATIGUE);
        ALIASPOTIONS.put("dull", MINING_FATIGUE);

        final PotionEffectType STRENGTH = RegistryUtil.valueOf(PotionEffectType.class, "INCREASE_DAMAGE", "STRENGTH");

        POTIONS.put("strength", STRENGTH);
        ALIASPOTIONS.put("strong", STRENGTH);
        ALIASPOTIONS.put("bull", STRENGTH);
        ALIASPOTIONS.put("attack", STRENGTH);

        final PotionEffectType INSTANT_HEALTH = RegistryUtil.valueOf(PotionEffectType.class, "HEAL", "INSTANT_HEALTH");

        POTIONS.put("heal", INSTANT_HEALTH);
        ALIASPOTIONS.put("healthy", INSTANT_HEALTH);
        ALIASPOTIONS.put("instaheal", INSTANT_HEALTH);

        final PotionEffectType INSTANT_DAMAGE = RegistryUtil.valueOf(PotionEffectType.class, "HARM", "INSTANT_DAMAGE");

        POTIONS.put("harm", INSTANT_DAMAGE);
        ALIASPOTIONS.put("harming", INSTANT_DAMAGE);
        ALIASPOTIONS.put("injure", INSTANT_DAMAGE);
        ALIASPOTIONS.put("damage", INSTANT_DAMAGE);
        ALIASPOTIONS.put("inflict", INSTANT_DAMAGE);

        final PotionEffectType JUMP_BOOST = RegistryUtil.valueOf(PotionEffectType.class, "JUMP", "JUMP_BOOST");

        POTIONS.put("jump", JUMP_BOOST);
        ALIASPOTIONS.put("leap", JUMP_BOOST);

        final PotionEffectType NAUSEA = RegistryUtil.valueOf(PotionEffectType.class, "CONFUSION", "NAUSEA");

        POTIONS.put("nausea", NAUSEA);
        ALIASPOTIONS.put("sick", NAUSEA);
        ALIASPOTIONS.put("sickness", NAUSEA);
        ALIASPOTIONS.put("confusion", NAUSEA);

        POTIONS.put("regeneration", PotionEffectType.REGENERATION);
        ALIASPOTIONS.put("regen", PotionEffectType.REGENERATION);

        final PotionEffectType RESISTANCE = RegistryUtil.valueOf(PotionEffectType.class, "DAMAGE_RESISTANCE", "RESISTANCE");

        POTIONS.put("resistance", RESISTANCE);
        ALIASPOTIONS.put("dmgresist", RESISTANCE);
        ALIASPOTIONS.put("armor", RESISTANCE);

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

        POTIONS.put("healthboost", PotionEffectType.HEALTH_BOOST);
        ALIASPOTIONS.put("boost", PotionEffectType.HEALTH_BOOST);

        POTIONS.put("absorption", PotionEffectType.ABSORPTION);
        ALIASPOTIONS.put("absorb", PotionEffectType.ABSORPTION);

        POTIONS.put("saturation", PotionEffectType.SATURATION);
        ALIASPOTIONS.put("food", PotionEffectType.SATURATION);

        POTIONS.put("waterbreathing", PotionEffectType.WATER_BREATHING);
        ALIASPOTIONS.put("underwaterbreathing", PotionEffectType.WATER_BREATHING);
        ALIASPOTIONS.put("waterbreath", PotionEffectType.WATER_BREATHING);
        ALIASPOTIONS.put("underwaterbreath", PotionEffectType.WATER_BREATHING);
        ALIASPOTIONS.put("air", PotionEffectType.WATER_BREATHING);

        // 1.9
        try {
            POTIONS.put("glowing", PotionEffectType.GLOWING);
            ALIASPOTIONS.put("glow", PotionEffectType.GLOWING);

            POTIONS.put("levitation", PotionEffectType.LEVITATION);
            ALIASPOTIONS.put("levitate", PotionEffectType.LEVITATION);

            POTIONS.put("luck", PotionEffectType.LUCK);
            POTIONS.put("unluck", PotionEffectType.UNLUCK);
        } catch (final Throwable ignored) {
        }

        // 1.21
        try {
            POTIONS.put("infested", PotionEffectType.INFESTED);
            ALIASPOTIONS.put("silverfish", PotionEffectType.INFESTED);

            POTIONS.put("oozing", PotionEffectType.OOZING);
            ALIASPOTIONS.put("ooze", PotionEffectType.OOZING);

            POTIONS.put("weaving", PotionEffectType.WEAVING);
            ALIASPOTIONS.put("weave", PotionEffectType.WEAVING);

            POTIONS.put("windcharged", PotionEffectType.WIND_CHARGED);
            ALIASPOTIONS.put("windcharge", PotionEffectType.WIND_CHARGED);
            ALIASPOTIONS.put("wind", PotionEffectType.WIND_CHARGED);
        } catch (final Throwable ignored) {
        }
    }

    private Potions() {
    }

    public static PotionEffectType getByName(final String name) {
        PotionEffectType peffect;
        if (NumberUtil.isInt(name)) {
            peffect = PotionEffectType.getById(Integer.parseInt(name));
        } else {
            peffect = PotionEffectType.getByName(name.toUpperCase(Locale.ENGLISH));
        }
        if (peffect == null) {
            peffect = POTIONS.get(name.toLowerCase(Locale.ENGLISH));
        }
        if (peffect == null) {
            peffect = ALIASPOTIONS.get(name.toLowerCase(Locale.ENGLISH));
        }
        return peffect;
    }

    public static Set<Entry<String, PotionEffectType>> entrySet() {
        return POTIONS.entrySet();
    }
}
