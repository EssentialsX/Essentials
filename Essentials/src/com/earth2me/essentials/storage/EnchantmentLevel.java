package com.earth2me.essentials.storage;

import org.bukkit.enchantments.Enchantment;

import java.util.Map.Entry;


public class EnchantmentLevel implements Entry<Enchantment, Integer> {
    private Enchantment enchantment;
    private int level;

    public EnchantmentLevel(final Enchantment enchantment, final int level) {
        this.enchantment = enchantment;
        this.level = level;
    }

    public Enchantment getEnchantment() {
        return enchantment;
    }

    public void setEnchantment(final Enchantment enchantment) {
        this.enchantment = enchantment;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(final int level) {
        this.level = level;
    }

    @Override
    public Enchantment getKey() {
        return enchantment;
    }

    @Override
    public Integer getValue() {
        return level;
    }

    @Override
    public Integer setValue(final Integer v) {
        int t = level;
        level = v;
        return t;
    }

    @Override
    public int hashCode() {
        return enchantment.hashCode() ^ level;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof Entry) {
            final Entry entry = (Entry) obj;
            if (entry.getKey() instanceof Enchantment && entry.getValue() instanceof Integer) {
                final Enchantment enchant = (Enchantment) entry.getKey();
                final Integer lvl = (Integer) entry.getValue();
                return this.enchantment.equals(enchant) && this.level == lvl;
            }
        }
        return false;
    }
}
