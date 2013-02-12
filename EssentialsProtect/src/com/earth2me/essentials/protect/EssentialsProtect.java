package com.earth2me.essentials.protect;

import com.earth2me.essentials.protect.data.IProtectedBlock;
import com.mchange.v2.log.MLevel;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;


public class EssentialsProtect extends JavaPlugin implements IProtect
{
	private static final Logger LOGGER = Logger.getLogger("Minecraft");
	private static com.mchange.v2.log.MLogger C3P0logger;
	private final Map<ProtectConfig, Boolean> settingsBoolean = new EnumMap<ProtectConfig, Boolean>(ProtectConfig.class);
	private final Map<ProtectConfig, String> settingsString = new EnumMap<ProtectConfig, String>(ProtectConfig.class);
	private final Map<ProtectConfig, List<Integer>> settingsList = new EnumMap<ProtectConfig, List<Integer>>(ProtectConfig.class);
	private IProtectedBlock storage = null;
	private EssentialsConnect ess = null;

	@Override
	public void onLoad()
	{
		try
		{
			// Simple fix for the case that log4j is on the class path by another plugin
			Class propertyConfiguratorClass = Class.forName("org.apache.log4j.PropertyConfigurator");
			Properties properties = new Properties();
			properties.load(this.getClass().getResourceAsStream("log4j.properties"));
			propertyConfiguratorClass.getMethod("configure", Properties.class).invoke(null, properties);
		}
		catch (Exception ex)
		{
			//Ignore me, log4j not found on classloader.
		}
		C3P0logger = com.mchange.v2.log.MLog.getLogger(com.mchange.v2.c3p0.impl.AbstractPoolBackedDataSource.class);
		C3P0logger.setLevel(MLevel.WARNING);
	}

	@Override
	public void onEnable()
	{
		final PluginManager pm = this.getServer().getPluginManager();
		final Plugin essPlugin = pm.getPlugin("Essentials");
		if (essPlugin == null || !essPlugin.isEnabled())
		{
			enableEmergencyMode(pm);
			return;
		}
		ess = new EssentialsConnect(essPlugin, this);

		final EssentialsProtectPlayerListener playerListener = new EssentialsProtectPlayerListener(this);
		pm.registerEvents(playerListener, this);

		final EssentialsProtectBlockListener blockListener = new EssentialsProtectBlockListener(this);
		pm.registerEvents(blockListener, this);

		final EssentialsProtectEntityListener entityListener = new EssentialsProtectEntityListener(this);
		pm.registerEvents(entityListener, this);

		final EssentialsProtectWeatherListener weatherListener = new EssentialsProtectWeatherListener(this);
		pm.registerEvents(weatherListener, this);
	}

	private void enableEmergencyMode(final PluginManager pm)
	{
		final EmergencyListener emListener = new EmergencyListener();
		pm.registerEvents(emListener, this);

		for (Player player : getServer().getOnlinePlayers())
		{
			player.sendMessage("Essentials Protect is in emergency mode. Check your log for errors.");
		}
		LOGGER.log(Level.SEVERE, "Essentials not installed or failed to load. Essenials Protect is in emergency mode now.");
	}

	@Override
	public IProtectedBlock getStorage()
	{
		return storage;
	}

	@Override
	public void setStorage(IProtectedBlock pb)
	{
		storage = pb;
	}

	@Override
	public EssentialsConnect getEssentialsConnect()
	{
		return ess;
	}

	@Override
	public Map<ProtectConfig, Boolean> getSettingsBoolean()
	{
		return settingsBoolean;
	}

	@Override
	public Map<ProtectConfig, String> getSettingsString()
	{
		return settingsString;
	}

	@Override
	public Map<ProtectConfig, List<Integer>> getSettingsList()
	{
		return settingsList;
	}

	@Override
	public boolean getSettingBool(final ProtectConfig protectConfig)
	{
		final Boolean bool = settingsBoolean.get(protectConfig);
		return bool == null ? protectConfig.getDefaultValueBoolean() : bool;
	}

	@Override
	public String getSettingString(final ProtectConfig protectConfig)
	{
		final String str = settingsString.get(protectConfig);
		return str == null ? protectConfig.getDefaultValueString() : str;
	}

	@Override
	public void onDisable()
	{
		if (storage != null)
		{
			storage.onPluginDeactivation();
		}
		// Sleep for a second to allow the database to close.
		try
		{
			Thread.sleep(1000);
		}
		catch (InterruptedException ex)
		{
			Logger.getLogger(EssentialsProtect.class.getName()).log(Level.SEVERE, null, ex);
		}	
	}
}
