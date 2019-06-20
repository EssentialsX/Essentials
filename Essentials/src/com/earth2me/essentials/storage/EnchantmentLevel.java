package com.earth2me.essentials.storage;

import org.bukkit.enchantments.Enchantment;

import java.util.Map.Entry;


/**
 * <p>EnchantmentLevel class.</p>
 *
 * @author LoopyD
 * @version $Id: $Id
 */
public class EnchantmentLevel implements Entry<Enchantment, Integer> {
    private Enchantment enchantment;
    private int level;

    /**
     * <p>Constructor for EnchantmentLevel.</p>
     *
     * @param enchantment a {@link org.bukkit.enchantments.Enchantment} object.
     * @param level a int.
     */
    public EnchantmentLevel(final Enchantment enchantment, final int level) {
        this.enchantment = enchantment;
        this.level = level;
    }

    /**
     * <p>Getter for the field <code>enchantment</code>.</p>
     *
     * @return a {@link org.bukkit.enchantments.Enchantment} object.
     */
    public Enchantment getEnchantment() {
        return enchantment;
    }

    /**
     * <p>Setter for the field <code>enchantment</code>.</p>
     *
     * @param enchantment a {@link org.bukkit.enchantments.Enchantment} object.
     */
    public void setEnchantment(final Enchantment enchantment) {
        this.enchantment = enchantment;
    }

    /**
     * <p>Getter for the field <code>level</code>.</p>
     *
     * @return a int.
     */
    public int getLevel() {
        return level;
    }

    /**
     * <p>Setter for the field <code>level</code>.</p>
     *
     * @param level a int.
     */
    public void setLevel(final int level) {
        this.level = level;
    }

    /** {@inheritDoc} */
    @Override
    public Enchantment getKey() {
        return enchantment;
    }

    /** {@inheritDoc} */
    @Override
    public Integer getValue() {
        return level;
    }

    /** {@inheritDoc} */
    @Override
    public Integer setValue(final Integer v) {
        int t = level;
        level = v;
        return t;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return enchantment.hashCode() ^ level;
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof Entry) {
            final Entry entry = (Entry) obj;
            if (entry.getKey() instanceof Enchantment && entry.getValue() instanceof Integer) {
                final Enchantment enchant = (Enchantment) entry.getKey();
                final Integer lvl = (Integer) entry.getValue();
                return this.enchantment.equals(enchant) && this.level == lvl.intValue();
            }
        }
        return false;
    }
}
