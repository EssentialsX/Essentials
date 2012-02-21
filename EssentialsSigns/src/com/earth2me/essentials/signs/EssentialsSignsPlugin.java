package com.earth2me.essentials.signs;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.api.IEssentials;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;


public class EssentialsSignsPlugin extends JavaPlugin implements ISignsPlugin
{
	private static final transient Logger LOGGER = Bukkit.getLogger();
	private transient IEssentials ess;
	private transient SignsConfigHolder config;

	@Override
	public void onEnable()
	{
		final PluginManager pluginManager = getServer().getPluginManager();
		ess = (IEssentials)pluginManager.getPlugin("Essentials3");
		if (!this.getDescription().getVersion().equals(ess.getDescription().getVersion()))
		{
			LOGGER.log(Level.WARNING, _("versionMismatchAll"));
		}
		if (!ess.isEnabled())
		{
			this.setEnabled(false);
			return;
		}

		final SignBlockListener signBlockListener = new SignBlockListener(ess, this);
		pluginManager.registerEvents(signBlockListener, this);

		final SignPlayerListener signPlayerListener = new SignPlayerListener(ess, this);
		pluginManager.registerEvents(signPlayerListener, this);

		final SignEntityListener signEntityListener = new SignEntityListener(ess, this);
		pluginManager.registerEvents(signEntityListener, this);
		
		config = new SignsConfigHolder(ess, this);
		
		LOGGER.info(_("loadinfo", this.getDescription().getName(), this.getDescription().getVersion(), "essentials team"));
	}

	@Override
	public void onDisable()
	{
	}

	@Override
	public SignsConfigHolder getSettings()
	{
		return config;
	}
}
