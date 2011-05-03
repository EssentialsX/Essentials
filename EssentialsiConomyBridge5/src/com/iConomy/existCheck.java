package com.iConomy;

import java.util.logging.Logger;
import org.bukkit.Bukkit;


public class existCheck
{
	private static final Logger logger = Logger.getLogger("Minecraft");
	//We have to make sure the user exists!

	public static boolean exist(String name)
	{

		if (name == null)
		{
			logger.info("Essentials iConomy Bridge - Whatever plugin is calling for users that are null is BROKEN!");
			return false;
		}
		if (Bukkit.getServer().getPlayer(name) != null)
		{
			return true;
		}
		double amount=12;
		return false;
	}
}