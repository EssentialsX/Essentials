package com.nijiko.coelho.iConomy;

import com.earth2me.essentials.Essentials;
import org.bukkit.plugin.java.JavaPlugin;
import com.nijiko.coelho.iConomy.system.Bank;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

/**
 * This is not iConomy and I take NO credit for iConomy!
 * This is FayConomy, a iConomy Essentials Eco bridge!
 * @author Xeology
 */

public class iConomy extends JavaPlugin
{
	public static Bank Bank = null;
	private static final Logger logger = Logger.getLogger("Minecraft");

	@Override
	public void onDisable()
	{
	}

	@Override
	public void onEnable()
	{
		PluginManager pm = this.getServer().getPluginManager();
		Plugin p = pm.getPlugin("Essentials");
		if (p != null)
		{
			if (!pm.isPluginEnabled(p))
			{
				pm.enablePlugin(p);
			}
		}

		String version = this.getDescription().getDescription().replaceAll(".*: ", "");
		if (!version.equals(Essentials.getStatic().getDescription().getVersion()))
		{
			logger.log(Level.WARNING, "Version mismatch! Please update all Essentials jars to the same version.");
		}
		Essentials.getStatic().setIConomyFallback(false);

		Bank = new Bank();
		
		logger.info("Loaded " + this.getDescription().getDescription() + " by " + Essentials.AUTHORS);
		logger.info("Make sure you don't have iConomy installed, if you use this.");
	}

	//Fake bank
	public static Bank getBank()
	{
		return Bank;
	}
}
