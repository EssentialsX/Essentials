package com.earth2me.essentials;

import java.io.File;
import java.util.logging.Logger;


public class Worth implements IConf
{
	private static final Logger logger = Logger.getLogger("Minecraft");
	public EssentialsConf config;

	public Worth(File dataFolder)
	{
		config = new EssentialsConf(new File(dataFolder, "worth.yml"));
		config.setTemplateName("/worth.yml");
		config.load();
	}

	public int getPrice(String id)
	{
		return config.getInt("worth-" + id, 0);
	}

	public void setPrice(String id, int price)
	{
		config.setProperty("worth-" + id, price);
		config.save();
		reloadConfig();
	}

	public void reloadConfig()
	{
		config.load();
	}

}
