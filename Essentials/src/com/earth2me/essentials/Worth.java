package com.earth2me.essentials;

import java.io.File;
import java.math.BigDecimal;
import java.util.Locale;
import java.util.logging.Logger;
import org.bukkit.inventory.ItemStack;


public class Worth implements IConf
{
	private static final Logger logger = Logger.getLogger("Minecraft");
	private final EssentialsConf config;

	public Worth(File dataFolder)
	{
		config = new EssentialsConf(new File(dataFolder, "worth.yml"));
		config.setTemplateName("/worth.yml");
		config.load();
	}

	public BigDecimal getPrice(ItemStack itemStack)
	{
		String itemname = itemStack.getType().toString().toLowerCase(Locale.ENGLISH).replace("_", "");
		BigDecimal result;
		result = config.getBigDecimal("worth." + itemname + "." + itemStack.getDurability(), BigDecimal.ONE.negate());
		if (result.signum() < 0)
		{
			result = config.getBigDecimal("worth." + itemname + ".0", BigDecimal.ONE.negate());
		}
		if (result.signum() < 0)
		{
			result = config.getBigDecimal("worth." + itemname, BigDecimal.ONE.negate());
		}
		if (result.signum() < 0)
		{
			result = config.getBigDecimal("worth-" + itemStack.getTypeId(), BigDecimal.ONE.negate());
		}
		if (result.signum() < 0)
		{
			return null;
		}
		return result;
	}

	public void setPrice(ItemStack itemStack, double price)
	{
		if (itemStack.getType().getData() == null)
		{
			config.setProperty("worth." + itemStack.getType().toString().toLowerCase(Locale.ENGLISH).replace("_", ""), price);
		}
		else
		{
			// Bukkit-bug: getDurability still contains the correct value, while getData().getData() is 0.
			config.setProperty("worth." + itemStack.getType().toString().toLowerCase(Locale.ENGLISH).replace("_", "") + "." + itemStack.getDurability(), price);
		}
		config.removeProperty("worth-" + itemStack.getTypeId());
		config.save();
	}

	@Override
	public void reloadConfig()
	{
		config.load();
	}
}
