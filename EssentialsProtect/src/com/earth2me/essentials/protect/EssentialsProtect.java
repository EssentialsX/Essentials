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
import java.util.List;
import java.util.Map;
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
	public static Map<String, Boolean> genSettings = null;
	public static Map<String, String> dataSettings = null;
	public static Map<String, Boolean> guardSettings = null;
	public static Map<String, Boolean> playerSettings = null;
	public static List<Integer> usageList = null;
	public static List<Integer> blackListPlace = null;
	public static List<Integer> breakBlackList = null;
	public static List<Integer> onPlaceAlert = null;
	public static List<Integer> onUseAlert = null;
	public static List<Integer> onBreakAlert = null;
	private IProtectedBlock storage = null;
	IEssentials ess = null;
	private static EssentialsProtect instance = null;

	public EssentialsProtect()
	{
	}

	public void onEnable()
	{
		ess = Essentials.getStatic();
		ess.getDependancyChecker().checkProtectDependancies();
		PluginManager pm = this.getServer().getPluginManager();
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
		pm.registerEvent(Type.ENTITY_TARGET, entityListener, Priority.Highest, this);
		reloadConfig();
		ess.addReloadListener(this);
		if (!this.getDescription().getVersion().equals(Essentials.getStatic().getDescription().getVersion()))
		{
			logger.log(Level.WARNING, Util.i18n("versionMismatchAll"));
		}
		logger.info(Util.format("loadinfo", this.getDescription().getName(), this.getDescription().getVersion(), Essentials.AUTHORS));
	}

	public static boolean checkProtectionItems(List<Integer> itemList, int id)
	{
		return !itemList.isEmpty() && itemList.contains(id);
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
