package com.earth2me.essentials;

import com.earth2me.essentials.api.IEssentials;
import com.earth2me.essentials.api.IWorth;
import com.earth2me.essentials.storage.AsyncStorageObjectHolder;
import com.earth2me.essentials.storage.EnchantmentLevel;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;


public class Worth extends AsyncStorageObjectHolder<com.earth2me.essentials.settings.Worth> implements IWorth
{
	public Worth(final IEssentials ess)
	{
		super(ess, com.earth2me.essentials.settings.Worth.class);
		onReload(false);
	}

	@Override
	public double getPrice(final ItemStack itemStack)
	{
		this.acquireReadLock();
		try
		{
			final Map<MaterialData, Double> prices = this.getData().getSell();
			if (prices == null || itemStack == null)
			{
				return Double.NaN;
			}
			final Double basePrice = prices.get(itemStack.getData());
			if (basePrice == null || Double.isNaN(basePrice))
			{
				return Double.NaN;
			}
			double multiplier = 1.0;
			if (itemStack.getType().getMaxDurability() > 0) {
				multiplier *= (double)itemStack.getDurability() / (double)itemStack.getType().getMaxDurability();
			}
			if (itemStack.getEnchantments() != null && !itemStack.getEnchantments().isEmpty())
			{
				final Map<EnchantmentLevel, Double> enchantmentMultipliers = this.getData().getEnchantmentMultiplier();
				if (enchantmentMultipliers != null)
				{
					for (Map.Entry<Enchantment, Integer> entry : itemStack.getEnchantments().entrySet())
					{
						final Double enchMult = enchantmentMultipliers.get(new EnchantmentLevel(entry.getKey(), entry.getValue()));
						if (enchMult != null)
						{
							multiplier *= enchMult;
						}
					}
				}
			}
			return basePrice * multiplier;
		}
		finally
		{
			this.unlock();
		}
	}

	@Override
	public void setPrice(final ItemStack itemStack, final double price)
	{
		acquireWriteLock();
		try {
			if (getData().getSell() == null) {
				getData().setSell(new HashMap<MaterialData, Double>());
			}
			getData().getSell().put(itemStack.getData(), price);
		} finally {
			unlock();
		}
	}

	@Override
	public File getStorageFile() throws IOException
	{
		return new File(ess.getDataFolder(), "worth.yml");
	}
}
