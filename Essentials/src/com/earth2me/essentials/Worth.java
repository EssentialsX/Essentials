package com.earth2me.essentials;

import java.io.File;
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

	public double getPrice(ItemStack itemStack)
	{
		String itemname = itemStack.getType().toString().toLowerCase(Locale.ENGLISH).replace("_", "");
		double result;
		result = config.getDouble("worth." + itemname + "." + itemStack.getDurability(), Double.NaN);
		if (Double.isNaN(result))
		{
			result = config.getDouble("worth." + itemname + ".0", Double.NaN);
		}
		if (Double.isNaN(result))
		{
			result = config.getDouble("worth." + itemname, Double.NaN);
		}
		if (Double.isNaN(result))
		{
			result = config.getDouble("worth-" + itemStack.getTypeId(), Double.NaN);
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
