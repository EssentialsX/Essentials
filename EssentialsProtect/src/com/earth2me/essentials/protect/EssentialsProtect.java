package com.earth2me.essentials.protect;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.IConf;
import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import com.earth2me.essentials.protect.data.IProtectedBlock;
import com.earth2me.essentials.protect.data.ProtectedBlockMemory;
import com.earth2me.essentials.protect.data.ProtectedBlockMySQL;
import com.earth2me.essentials.protect.data.ProtectedBlockSQLite;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;


public class EssentialsProtect extends JavaPlugin implements IConf
{
	private EssentialsProtectBlockListener blockListener = null;
	private EssentialsProtectPlayerListener playerListener = null;
	private EssentialsProtectEntityListener entityListener = null;
	private EssentialsProtectWeatherListener weatherListener = null;
	public static final String AUTHORS = Essentials.AUTHORS;
	private static final Logger logger = Logger.getLogger("Minecraft");
	public static HashMap<String, Boolean> genSettings = null;
	public static HashMap<String, String> dataSettings = null;
	public static HashMap<String, Boolean> guardSettings = null;
	public static HashMap<String, Boolean> playerSettings = null;
	public static ArrayList usageList = null;
	public static ArrayList blackListPlace = null;
	public static ArrayList breakBlackList = null;
	public static ArrayList onPlaceAlert = null;
	public static ArrayList onUseAlert = null;
	public static ArrayList onBreakAlert = null;
	private IProtectedBlock storage = null;
	IEssentials ess = null;
	private static EssentialsProtect instance = null;

	public EssentialsProtect()
	{
	}

	public void onEnable()
	{
		ess = Essentials.getStatic();
		PluginManager pm = this.getServer().getPluginManager();
		Essentials ess = (Essentials)pm.getPlugin("Essentials");
		if (!ess.isEnabled())
		{
			pm.enablePlugin(ess);
		}

		instance = this;
		reloadConfig();

		playerListener = new EssentialsProtectPlayerListener(this);
		blockListener = new EssentialsProtectBlockListener(this);
		entityListener = new EssentialsProtectEntityListener(this);
		weatherListener = new EssentialsProtectWeatherListener(this);
		pm.registerEvent(Type.PLAYER_INTERACT, playerListener, Priority.Low, this);
		pm.registerEvent(Type.BLOCK_PLACE, blockListener, Priority.Highest, this);
		pm.registerEvent(Type.BLOCK_FROMTO, blockListener, Priority.Highest, this);
		pm.registerEvent(Type.BLOCK_IGNITE, blockListener, Priority.Highest, this);
		pm.registerEvent(Type.BLOCK_BURN, blockListener, Priority.Highest, this);
		pm.registerEvent(Type.ENTITY_EXPLODE, entityListener, Priority.Highest, this);
		pm.registerEvent(Type.ENTITY_DAMAGE, entityListener, Priority.Highest, this);
		pm.registerEvent(Type.BLOCK_BREAK, blockListener, Priority.Highest, this);
		pm.registerEvent(Type.CREATURE_SPAWN, entityListener, Priority.Highest, this);
		pm.registerEvent(Type.LIGHTNING_STRIKE, weatherListener, Priority.Highest, this);
		pm.registerEvent(Type.THUNDER_CHANGE, weatherListener, Priority.Highest, this);
		pm.registerEvent(Type.WEATHER_CHANGE, weatherListener, Priority.Highest, this);
		
		if (!this.getDescription().getVersion().equals(Essentials.getStatic().getDescription().getVersion()))
		{
			logger.log(Level.WARNING, "Version mismatch! Please update all Essentials jars to the same version.");
		}
		logger.info("Loaded " + this.getDescription().getName() + " build " + this.getDescription().getVersion() + " maintained by " + AUTHORS);
	}

	public static boolean checkProtectionItems(ArrayList itemList, int id)
	{
		return !itemList.isEmpty() && itemList.contains(String.valueOf(id));
	}

	@Override
	public void onDisable()
	{
		genSettings.clear();
		dataSettings.clear();

		blockListener = null;
		playerListener = null;
		entityListener = null;
		genSettings = null;
		dataSettings = null;
		guardSettings = null;
		playerSettings = null;
		usageList = null;
		blackListPlace = null;
		onPlaceAlert = null;
		onUseAlert = null;
		onBreakAlert = null;
	}

	public void alert(User user, String item, String type)
	{
		Location loc = user.getLocation();
		for (Player p : this.getServer().getOnlinePlayers())
		{
			User alertUser = ess.getUser(p);
			if (alertUser.isAuthorized("essentials.protect.alerts"))
				alertUser.sendMessage(Util.format("alertFormat", user.getName(), type, item, formatCoords(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())));
		}
	}

	public static String formatCoords(int x, int y, int z)
	{
		return x + "," + y + "," + z;
	}

	public void reloadConfig()
	{
		dataSettings = ess.getSettings().getEpDBSettings();
		genSettings = ess.getSettings().getEpSettings();
		guardSettings = ess.getSettings().getEpGuardSettings();
		usageList = ess.getSettings().epBlackListUsage();
		blackListPlace = ess.getSettings().epBlackListPlacement();
		breakBlackList = ess.getSettings().epBlockBreakingBlacklist();
		onPlaceAlert = ess.getSettings().getEpAlertOnPlacement();
		onUseAlert = ess.getSettings().getEpAlertOnUse();
		onBreakAlert = ess.getSettings().getEpAlertOnBreak();
		playerSettings = ess.getSettings().getEpPlayerSettings();

		if (dataSettings.get("protect.datatype").equals("mysql"))
		{
			try
			{
				storage = new ProtectedBlockMySQL(dataSettings.get("protect.mysqlDb"), dataSettings.get("protect.username"), dataSettings.get("protect.password"));
			}
			catch (PropertyVetoException ex)
			{
				logger.log(Level.SEVERE, null, ex);
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
				logger.log(Level.SEVERE, null, ex);
			}
		}
		if (genSettings.get("protect.memstore"))
		{
			storage = new ProtectedBlockMemory(storage);
		}
	}

	public static IProtectedBlock getStorage()
	{
		return EssentialsProtect.instance.storage;
	}
}
