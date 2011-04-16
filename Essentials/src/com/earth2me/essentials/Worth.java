package com.earth2me.essentials;

import java.io.File;
import java.util.logging.Logger;
import org.bukkit.inventory.ItemStack;


public class Worth implements IConf
{
	private static final Logger logger = Logger.getLogger("Minecraft");
	private EssentialsConf config;

	public Worth(File dataFolder)
	{
		config = new EssentialsConf(new File(dataFolder, "worth.yml"));
		config.setTemplateName("/worth.yml");
		config.load();
	}

	public double getPrice(ItemStack itemStack)
	{
		double result = config.getDouble("worth."+itemStack.getType().toString().toLowerCase().replace("_", "")+"."+itemStack.getData().getData(), Double.NaN);
		if (Double.isNaN(result)) {
			result = config.getDouble("worth."+itemStack.getType().toString().toLowerCase().replace("_", ""), Double.NaN);
		}
		if (Double.isNaN(result)) {
			result = config.getDouble("worth-"+itemStack.getTypeId(), 0.0);
		}
		return result;
	}

	public void setPrice(ItemStack itemStack, double price)
	{
		if (itemStack.getType().getData() == null) {
			config.setProperty("worth." + itemStack.getType().toString().toLowerCase().replace("_", ""), price);
		} else {
			config.setProperty("worth." + itemStack.getType().toString().toLowerCase().replace("_", "")+"."+itemStack.getData().getData(), price);
		}
		config.removeProperty("worth-"+itemStack.getTypeId());
		config.save();
	}

	public void reloadConfig()
	{
		config.load();
	}

}
