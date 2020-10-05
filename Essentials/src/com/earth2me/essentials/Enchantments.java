package com.earth2me.essentials;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


public class Enchantments {
    private static final Map<String, Enchantment> ENCHANTMENTS = new HashMap<>();
    private static final Map<String, Enchantment> ALIASENCHANTMENTS = new HashMap<>();
    private static boolean isFlat;

    static {
        ENCHANTMENTS.put("alldamage", Enchantment.DAMAGE_ALL);
        ALIASENCHANTMENTS.put("alldmg", Enchantment.DAMAGE_ALL);
        ENCHANTMENTS.put("sharpness", Enchantment.DAMAGE_ALL);
        ALIASENCHANTMENTS.put("sharp", Enchantment.DAMAGE_ALL);
        ALIASENCHANTMENTS.put("dal", Enchantment.DAMAGE_ALL);

        ENCHANTMENTS.put("ardmg", Enchantment.DAMAGE_ARTHROPODS);
        ENCHANTMENTS.put("baneofarthropods", Enchantment.DAMAGE_ARTHROPODS);
        ALIASENCHANTMENTS.put("baneofarthropod", Enchantment.DAMAGE_ARTHROPODS);
        ALIASENCHANTMENTS.put("arthropod", Enchantment.DAMAGE_ARTHROPODS);
        ALIASENCHANTMENTS.put("dar", Enchantment.DAMAGE_ARTHROPODS);

        ENCHANTMENTS.put("undeaddamage", Enchantment.DAMAGE_UNDEAD);
        ENCHANTMENTS.put("smite", Enchantment.DAMAGE_UNDEAD);
        ALIASENCHANTMENTS.put("du", Enchantment.DAMAGE_UNDEAD);

        ENCHANTMENTS.put("digspeed", Enchantment.DIG_SPEED);
        ENCHANTMENTS.put("efficiency", Enchantment.DIG_SPEED);
        ALIASENCHANTMENTS.put("minespeed", Enchantment.DIG_SPEED);
        ALIASENCHANTMENTS.put("cutspeed", Enchantment.DIG_SPEED);
        ALIASENCHANTMENTS.put("ds", Enchantment.DIG_SPEED);
        ALIASENCHANTMENTS.put("eff", Enchantment.DIG_SPEED);

        ENCHANTMENTS.put("durability", Enchantment.DURABILITY);
        ALIASENCHANTMENTS.put("dura", Enchantment.DURABILITY);
        ENCHANTMENTS.put("unbreaking", Enchantment.DURABILITY);
        ALIASENCHANTMENTS.put("d", Enchantment.DURABILITY);

        ENCHANTMENTS.put("thorns", Enchantment.THORNS);
        ENCHANTMENTS.put("highcrit", Enchantment.THORNS);
        ALIASENCHANTMENTS.put("thorn", Enchantment.THORNS);
        ALIASENCHANTMENTS.put("highercrit", Enchantment.THORNS);
        ALIASENCHANTMENTS.put("t", Enchantment.THORNS);

        ENCHANTMENTS.put("fireaspect", Enchantment.FIRE_ASPECT);
        ENCHANTMENTS.put("fire", Enchantment.FIRE_ASPECT);
        ALIASENCHANTMENTS.put("meleefire", Enchantment.FIRE_ASPECT);
        ALIASENCHANTMENTS.put("meleeflame", Enchantment.FIRE_ASPECT);
        ALIASENCHANTMENTS.put("fa", Enchantment.FIRE_ASPECT);

        ENCHANTMENTS.put("knockback", Enchantment.KNOCKBACK);
        ALIASENCHANTMENTS.put("kback", Enchantment.KNOCKBACK);
        ALIASENCHANTMENTS.put("kb", Enchantment.KNOCKBACK);
        ALIASENCHANTMENTS.put("k", Enchantment.KNOCKBACK);

        ALIASENCHANTMENTS.put("blockslootbonus", Enchantment.LOOT_BONUS_BLOCKS);
        ENCHANTMENTS.put("fortune", Enchantment.LOOT_BONUS_BLOCKS);
        ALIASENCHANTMENTS.put("fort", Enchantment.LOOT_BONUS_BLOCKS);
        ALIASENCHANTMENTS.put("lbb", Enchantment.LOOT_BONUS_BLOCKS);

        ALIASENCHANTMENTS.put("mobslootbonus", Enchantment.LOOT_BONUS_MOBS);
        ENCHANTMENTS.put("mobloot", Enchantment.LOOT_BONUS_MOBS);
        ENCHANTMENTS.put("looting", Enchantment.LOOT_BONUS_MOBS);
        ALIASENCHANTMENTS.put("lbm", Enchantment.LOOT_BONUS_MOBS);

        ALIASENCHANTMENTS.put("oxygen", Enchantment.OXYGEN);
        ENCHANTMENTS.put("respiration", Enchantment.OXYGEN);
        ALIASENCHANTMENTS.put("breathing", Enchantment.OXYGEN);
        ENCHANTMENTS.put("breath", Enchantment.OXYGEN);
        ALIASENCHANTMENTS.put("o", Enchantment.OXYGEN);

        ENCHANTMENTS.put("protection", Enchantment.PROTECTION_ENVIRONMENTAL);
        ALIASENCHANTMENTS.put("prot", Enchantment.PROTECTION_ENVIRONMENTAL);
        ENCHANTMENTS.put("protect", Enchantment.PROTECTION_ENVIRONMENTAL);
        ALIASENCHANTMENTS.put("p", Enchantment.PROTECTION_ENVIRONMENTAL);

        ALIASENCHANTMENTS.put("explosionsprotection", Enchantment.PROTECTION_EXPLOSIONS);
        ALIASENCHANTMENTS.put("explosionprotection", Enchantment.PROTECTION_EXPLOSIONS);
        ALIASENCHANTMENTS.put("expprot", Enchantment.PROTECTION_EXPLOSIONS);
        ALIASENCHANTMENTS.put("blastprotection", Enchantment.PROTECTION_EXPLOSIONS);
        ALIASENCHANTMENTS.put("bprotection", Enchantment.PROTECTION_EXPLOSIONS);
        ALIASENCHANTMENTS.put("bprotect", Enchantment.PROTECTION_EXPLOSIONS);
        ENCHANTMENTS.put("blastprotect", Enchantment.PROTECTION_EXPLOSIONS);
        ALIASENCHANTMENTS.put("pe", Enchantment.PROTECTION_EXPLOSIONS);

        ALIASENCHANTMENTS.put("fallprotection", Enchantment.PROTECTION_FALL);
        ENCHANTMENTS.put("fallprot", Enchantment.PROTECTION_FALL);
        ENCHANTMENTS.put("featherfall", Enchantment.PROTECTION_FALL);
        ALIASENCHANTMENTS.put("featherfalling", Enchantment.PROTECTION_FALL);
        ALIASENCHANTMENTS.put("pfa", Enchantment.PROTECTION_FALL);

        ALIASENCHANTMENTS.put("fireprotection", Enchantment.PROTECTION_FIRE);
        ALIASENCHANTMENTS.put("flameprotection", Enchantment.PROTECTION_FIRE);
        ENCHANTMENTS.put("fireprotect", Enchantment.PROTECTION_FIRE);
        ALIASENCHANTMENTS.put("flameprotect", Enchantment.PROTECTION_FIRE);
        ENCHANTMENTS.put("fireprot", Enchantment.PROTECTION_FIRE);
        ALIASENCHANTMENTS.put("flameprot", Enchantment.PROTECTION_FIRE);
        ALIASENCHANTMENTS.put("pf", Enchantment.PROTECTION_FIRE);

        ENCHANTMENTS.put("projectileprotection", Enchantment.PROTECTION_PROJECTILE);
        ENCHANTMENTS.put("projprot", Enchantment.PROTECTION_PROJECTILE);
        ALIASENCHANTMENTS.put("pp", Enchantment.PROTECTION_PROJECTILE);

        ENCHANTMENTS.put("silktouch", Enchantment.SILK_TOUCH);
        ALIASENCHANTMENTS.put("softtouch", Enchantment.SILK_TOUCH);
        ALIASENCHANTMENTS.put("st", Enchantment.SILK_TOUCH);

        ENCHANTMENTS.put("waterworker", Enchantment.WATER_WORKER);
        ENCHANTMENTS.put("aquaaffinity", Enchantment.WATER_WORKER);
        ALIASENCHANTMENTS.put("watermine", Enchantment.WATER_WORKER);
        ALIASENCHANTMENTS.put("ww", Enchantment.WATER_WORKER);

        ALIASENCHANTMENTS.put("firearrow", Enchantment.ARROW_FIRE);
        ENCHANTMENTS.put("flame", Enchantment.ARROW_FIRE);
        ENCHANTMENTS.put("flamearrow", Enchantment.ARROW_FIRE);
        ALIASENCHANTMENTS.put("af", Enchantment.ARROW_FIRE);

        ENCHANTMENTS.put("arrowdamage", Enchantment.ARROW_DAMAGE);
        ENCHANTMENTS.put("power", Enchantment.ARROW_DAMAGE);
        ALIASENCHANTMENTS.put("arrowpower", Enchantment.ARROW_DAMAGE);
        ALIASENCHANTMENTS.put("ad", Enchantment.ARROW_DAMAGE);

        ENCHANTMENTS.put("arrowknockback", Enchantment.ARROW_KNOCKBACK);
        ALIASENCHANTMENTS.put("arrowkb", Enchantment.ARROW_KNOCKBACK);
        ENCHANTMENTS.put("punch", Enchantment.ARROW_KNOCKBACK);
        ALIASENCHANTMENTS.put("arrowpunch", Enchantment.ARROW_KNOCKBACK);
        ALIASENCHANTMENTS.put("ak", Enchantment.ARROW_KNOCKBACK);

        ALIASENCHANTMENTS.put("infinitearrows", Enchantment.ARROW_INFINITE);
        ENCHANTMENTS.put("infarrows", Enchantment.ARROW_INFINITE);
        ENCHANTMENTS.put("infinity", Enchantment.ARROW_INFINITE);
        ALIASENCHANTMENTS.put("infinite", Enchantment.ARROW_INFINITE);
        ALIASENCHANTMENTS.put("unlimited", Enchantment.ARROW_INFINITE);
        ALIASENCHANTMENTS.put("unlimitedarrows", Enchantment.ARROW_INFINITE);
        ALIASENCHANTMENTS.put("ai", Enchantment.ARROW_INFINITE);

        ENCHANTMENTS.put("luck", Enchantment.LUCK);
        ALIASENCHANTMENTS.put("luckofsea", Enchantment.LUCK);
        ALIASENCHANTMENTS.put("luckofseas", Enchantment.LUCK);
        ALIASENCHANTMENTS.put("rodluck", Enchantment.LUCK);

        ENCHANTMENTS.put("lure", Enchantment.LURE);
        ALIASENCHANTMENTS.put("rodlure", Enchantment.LURE);

        // 1.8
        try {
            Enchantment depthStrider = Enchantment.getByName("DEPTH_STRIDER");
            if (depthStrider != null) {
                ENCHANTMENTS.put("depthstrider", depthStrider);
                ALIASENCHANTMENTS.put("depth", depthStrider);
                ALIASENCHANTMENTS.put("strider", depthStrider);
            }
        } catch (IllegalArgumentException ignored) {}

        // 1.9
        try {
            Enchantment frostWalker = Enchantment.getByName("FROST_WALKER");
            if (frostWalker != null) {
                ENCHANTMENTS.put("frostwalker", frostWalker);
                ALIASENCHANTMENTS.put("frost", frostWalker);
                ALIASENCHANTMENTS.put("walker", frostWalker);
            }

            Enchantment mending = Enchantment.getByName("MENDING");
            if (mending != null) {
                ENCHANTMENTS.put("mending", mending);
            }
        } catch (IllegalArgumentException ignored) {}

        // 1.11
        try {
            Enchantment bindingCurse = Enchantment.getByName("BINDING_CURSE");
            if (bindingCurse != null) {
                ENCHANTMENTS.put("bindingcurse", bindingCurse);
                ALIASENCHANTMENTS.put("bindcurse", bindingCurse);
                ALIASENCHANTMENTS.put("binding", bindingCurse);
                ALIASENCHANTMENTS.put("bind", bindingCurse);
            }
            Enchantment vanishingCurse = Enchantment.getByName("VANISHING_CURSE");
            if (vanishingCurse != null) {
                ENCHANTMENTS.put("vanishingcurse", vanishingCurse);
                ALIASENCHANTMENTS.put("vanishcurse", vanishingCurse);
                ALIASENCHANTMENTS.put("vanishing", vanishingCurse);
                ALIASENCHANTMENTS.put("vanish", vanishingCurse);
            }
            Enchantment sweeping = Enchantment.getByName("SWEEPING_EDGE");
            if (sweeping != null) {
                ENCHANTMENTS.put("sweepingedge", sweeping);
                ALIASENCHANTMENTS.put("sweepedge", sweeping);
                ALIASENCHANTMENTS.put("sweeping", sweeping);
            }
        } catch (IllegalArgumentException ignored) {}


        try { // 1.13
            Enchantment loyalty = Enchantment.getByName("LOYALTY");
            if (loyalty != null) {
                ENCHANTMENTS.put("loyalty", loyalty);
                ALIASENCHANTMENTS.put("loyal", loyalty);
                ALIASENCHANTMENTS.put("return", loyalty);
            }
            Enchantment impaling = Enchantment.getByName("IMPALING");
            if (impaling != null) {
                ENCHANTMENTS.put("impaling", impaling);
                ALIASENCHANTMENTS.put("impale", impaling);
                ALIASENCHANTMENTS.put("oceandamage", impaling);
                ALIASENCHANTMENTS.put("oceandmg", impaling);
            }
            Enchantment riptide = Enchantment.getByName("RIPTIDE");
            if (riptide != null) {
                ENCHANTMENTS.put("riptide", riptide);
                ALIASENCHANTMENTS.put("rip", riptide);
                ALIASENCHANTMENTS.put("tide", riptide);
                ALIASENCHANTMENTS.put("launch", riptide);
            }
            Enchantment channelling = Enchantment.getByName("CHANNELING");
            if (channelling != null) {
                ENCHANTMENTS.put("channelling", channelling);
                ALIASENCHANTMENTS.put("chanelling", channelling);
                ALIASENCHANTMENTS.put("channeling", channelling);
                ALIASENCHANTMENTS.put("chaneling", channelling);
                ALIASENCHANTMENTS.put("channel", channelling);
            }
        } catch (IllegalArgumentException ignored) {}


        try { // 1.14
            Enchantment multishot = Enchantment.getByName("MULTISHOT");
            if (multishot != null) {
                ENCHANTMENTS.put("multishot", multishot);
                ALIASENCHANTMENTS.put("tripleshot", multishot);
            }
            Enchantment quickCharge = Enchantment.getByName("QUICK_CHARGE");
            if (quickCharge != null) {
                ENCHANTMENTS.put("quickcharge", quickCharge);
                ALIASENCHANTMENTS.put("quickdraw", quickCharge);
                ALIASENCHANTMENTS.put("fastcharge", quickCharge);
                ALIASENCHANTMENTS.put("fastdraw", quickCharge);
            }
            Enchantment piercing = Enchantment.getByName("PIERCING");
            if (piercing != null) {
                ENCHANTMENTS.put("piercing", piercing);
            }
        } catch (IllegalArgumentException ignored) {}

        try {
            Class<?> namespacedKeyClass = Class.forName("org.bukkit.NamespacedKey");
            Class<?> enchantmentClass = Class.forName("org.bukkit.enchantments.Enchantment");
            enchantmentClass.getDeclaredMethod("getByKey", namespacedKeyClass);
            isFlat = true;
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            isFlat = false;
        }
    }

    public static Enchantment getByName(String name) {
        Enchantment enchantment = null;
        if (isFlat) { // 1.13+ only
            enchantment = Enchantment.getByKey(NamespacedKey.minecraft(name.toLowerCase()));
        }

        if (enchantment == null) {
            enchantment = Enchantment.getByName(name.toUpperCase());
        }
        if (enchantment == null) {
            enchantment = ENCHANTMENTS.get(name.toLowerCase(Locale.ENGLISH));
        }
        if (enchantment == null) {
            enchantment = ALIASENCHANTMENTS.get(name.toLowerCase(Locale.ENGLISH));
        }
        return enchantment;
    }

    public static Set<Entry<String, Enchantment>> entrySet() {
        return ENCHANTMENTS.entrySet();
    }

    public static Set<String> keySet() {
        return ENCHANTMENTS.keySet();
    }
}
