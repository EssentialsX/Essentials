package com.earth2me.essentials.storage;

import java.util.Map.Entry;
import org.bukkit.enchantments.Enchantment;


public class EnchantmentLevel implements Entry<Enchantment, Integer>
{
	private Enchantment enchantment;
	private  int level;

	public EnchantmentLevel(Enchantment enchantment, int level)
	{
		this.enchantment = enchantment;
		this.level = level;
	}

	public Enchantment getEnchantment()
	{
		return enchantment;
	}

	public void setEnchantment(Enchantment enchantment)
	{
		this.enchantment = enchantment;
	}

	public int getLevel()
	{
		return level;
	}

	public void setLevel(int level)
	{
		this.level = level;
	}

	@Override
	public Enchantment getKey()
	{
		return enchantment;
	}

	@Override
	public Integer getValue()
	{
		return level;
	}

	@Override
	public Integer setValue(Integer v)
	{
		int t = level;
		level = v;
		return t;
	}
}
