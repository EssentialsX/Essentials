package com.earth2me.essentials.protect;

import com.earth2me.essentials.IConf;
import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import com.earth2me.essentials.protect.data.IProtectedBlock;
import com.earth2me.essentials.protect.data.ProtectedBlockMemory;
import com.earth2me.essentials.protect.data.ProtectedBlockMySQL;
import com.earth2me.essentials.protect.data.ProtectedBlockSQLite;
import java.beans.PropertyVetoException;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Filter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;


public class EssentialsProtect extends JavaPlugin implements IConf, IProtect
{
	private static final Logger LOGGER = Logger.getLogger("Minecraft");
	private static com.mchange.v2.log.MLogger C3P0logger;
	private final transient Map<ProtectConfig, Boolean> settingsBoolean = new EnumMap<ProtectConfig, Boolean>(ProtectConfig.class);
	private final transient Map<ProtectConfig, String> settingsString = new EnumMap<ProtectConfig, String>(ProtectConfig.class);
	private final transient Map<ProtectConfig, List<Integer>> settingsList = new EnumMap<ProtectConfig, List<Integer>>(ProtectConfig.class);
	private transient IProtectedBlock storage = null;
	public transient IEssentials ess = null;

	@Override
	public void onLoad()
	{
		C3P0logger = com.mchange.v2.log.MLog.getLogger(com.mchange.v2.c3p0.impl.AbstractPoolBackedDataSource.class);
		C3P0logger.setFilter(new Filter()
		{
			public boolean isLoggable(LogRecord lr)
			{
				return lr.getLevel() != Level.INFO;
			}
		});
	}

	public void onEnable()
	{
		final PluginManager pm = this.getServer().getPluginManager();
		ess = (IEssentials)pm.getPlugin("Essentials");

		final EssentialsProtectPlayerListener playerListener = new EssentialsProtectPlayerListener(this);
		pm.registerEvent(Type.PLAYER_INTERACT, playerListener, Priority.Low, this);

		final EssentialsProtectBlockListener blockListener = new EssentialsProtectBlockListener(this);
		pm.registerEvent(Type.BLOCK_PLACE, blockListener, Priority.Highest, this);
		pm.registerEvent(Type.BLOCK_FROMTO, blockListener, Priority.Highest, this);
		pm.registerEvent(Type.BLOCK_IGNITE, blockListener, Priority.Highest, this);
		pm.registerEvent(Type.BLOCK_BURN, blockListener, Priority.Highest, this);
		pm.registerEvent(Type.BLOCK_BREAK, blockListener, Priority.Highest, this);
		pm.registerEvent(Type.BLOCK_PISTON_EXTEND, blockListener, Priority.Highest, this);
		pm.registerEvent(Type.BLOCK_PISTON_RETRACT, blockListener, Priority.Highest, this);

		final EssentialsProtectEntityListener entityListener = new EssentialsProtectEntityListener(this);
		pm.registerEvent(Type.ENTITY_EXPLODE, entityListener, Priority.Highest, this);
		pm.registerEvent(Type.ENTITY_DAMAGE, entityListener, Priority.Highest, this);
		pm.registerEvent(Type.CREATURE_SPAWN, entityListener, Priority.Highest, this);
		pm.registerEvent(Type.ENTITY_TARGET, entityListener, Priority.Highest, this);
		pm.registerEvent(Type.EXPLOSION_PRIME, entityListener, Priority.Highest, this);

		final EssentialsProtectWeatherListener weatherListener = new EssentialsProtectWeatherListener(this);
		pm.registerEvent(Type.LIGHTNING_STRIKE, weatherListener, Priority.Highest, this);
		pm.registerEvent(Type.THUNDER_CHANGE, weatherListener, Priority.Highest, this);
		pm.registerEvent(Type.WEATHER_CHANGE, weatherListener, Priority.Highest, this);

		reloadConfig();
		ess.addReloadListener(this);
		if (!this.getDescription().getVersion().equals(ess.getDescription().getVersion()))
		{
			LOGGER.log(Level.WARNING, Util.i18n("versionMismatchAll"));
		}
		LOGGER.info(Util.format("loadinfo", this.getDescription().getName(), this.getDescription().getVersion(), "essentials team"));
	}

	@Override
	public boolean checkProtectionItems(final ProtectConfig list, final int id)
	{
		final List<Integer> itemList = settingsList.get(list);
		return itemList != null && !itemList.isEmpty() && itemList.contains(id);
	}

	@Override
	public void alert(final User user, final String item, final String type)
	{
		final Location loc = user.getLocation();
		final String warnMessage = Util.format("alertFormat", user.getName(), type, item,
											   loc.getWorld().getName() + "," + loc.getBlockX() + ","
											   + loc.getBlockY() + "," + loc.getBlockZ());
		LOGGER.log(Level.WARNING, warnMessage);
		for (Player p : this.getServer().getOnlinePlayers())
		{
			final User alertUser = ess.getUser(p);
			if (alertUser.isAuthorized("essentials.protect.alerts"))
			{
				alertUser.sendMessage(warnMessage);
			}
		}
	}

	public void reloadConfig()
	{
		if (storage != null)
		{
			storage.onPluginDeactivation();
		}
		for (ProtectConfig protectConfig : ProtectConfig.values())
		{
			if (protectConfig.isList())
			{
				settingsList.put(protectConfig, ess.getSettings().getProtectList(protectConfig.getConfigName()));
			}
			else if (protectConfig.isString())
			{
				settingsString.put(protectConfig, ess.getSettings().getProtectString(protectConfig.getConfigName()));
			}
			else
			{
				settingsBoolean.put(protectConfig, ess.getSettings().getProtectBoolean(protectConfig.getConfigName(), protectConfig.getDefaultValueBoolean()));
			}

		}

		if (getSettingString(ProtectConfig.datatype).equalsIgnoreCase("mysql"))
		{
			try
			{
				storage = new ProtectedBlockMySQL(
						getSettingString(ProtectConfig.mysqlDB),
						getSettingString(ProtectConfig.dbUsername),
						getSettingString(ProtectConfig.dbPassword));
			}
			catch (PropertyVetoException ex)
			{
				LOGGER.log(Level.SEVERE, null, ex);
			}
		}
		else
		{
			try
			{
				storage = new ProtectedBlockSQLite("jdbc:sqlite:plugins/Essentials/EssentialsProtect.db");
			}
			catch (PropertyVetoException ex)
			{
				LOGGER.log(Level.SEVERE, null, ex);
			}
		}
		if (getSettingBool(ProtectConfig.memstore))
		{
			storage = new ProtectedBlockMemory(storage, this);
		}
	}

	@Override
	public IProtectedBlock getStorage()
	{
		return storage;
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
		}
	}

	public IEssentials getEssentials()
	{
		return ess;
	}
}
