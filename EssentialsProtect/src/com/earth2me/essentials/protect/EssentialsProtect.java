package com.earth2me.essentials.protect;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;


public class EssentialsProtect extends JavaPlugin
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
	public static ArrayList<Integer> usageList = null;
	public static ArrayList<Integer> blackListPlace = null;
	public static ArrayList<Integer> breakBlackList = null;
	public static ArrayList<Integer> onPlaceAlert = null;
	public static ArrayList<Integer> onUseAlert = null;
	public static ArrayList<Integer> onBreakAlert = null;

	public EssentialsProtect()
	{
	}

	public void onEnable()
	{
		PluginManager pm = this.getServer().getPluginManager();
		Essentials ess = (Essentials)pm.getPlugin("Essentials");
		if (!ess.isEnabled())
			pm.enablePlugin(ess);

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
		pm.registerEvent(Type.ENTITY_TARGET, entityListener, Priority.Highest, this);
		pm.registerEvent(Type.BLOCK_BREAK, blockListener, Priority.Highest, this);
		pm.registerEvent(Type.CREATURE_SPAWN, entityListener, Priority.Highest, this);
		pm.registerEvent(Type.WEATHER_CHANGE, weatherListener, Priority.Highest, this);
		pm.registerEvent(Type.THUNDER_CHANGE, weatherListener, Priority.Highest, this);
		pm.registerEvent(Type.LIGHTNING_STRIKE, weatherListener, Priority.Highest, this);
		loadSettings();
		if (!this.getDescription().getVersion().equals(Essentials.getStatic().getDescription().getVersion()))
		{
			logger.log(Level.WARNING, "Version mismatch! Please update all Essentials jars to the same version.");
		}
		logger.info("Loaded " + this.getDescription().getName() + " build " + this.getDescription().getVersion() + " maintained by " + AUTHORS);
	}

	public static boolean checkProtectionItems(ArrayList<Integer> itemList, int id)
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

	public static void loadSettings()
	{
		dataSettings = Essentials.getStatic().getSettings().getEpDBSettings();
		genSettings = Essentials.getStatic().getSettings().getEpSettings();
		guardSettings = Essentials.getStatic().getSettings().getEpGuardSettings();
		usageList = Essentials.getStatic().getSettings().epBlackListUsage();
		blackListPlace = Essentials.getStatic().getSettings().epBlackListPlacement();
		breakBlackList = Essentials.getStatic().getSettings().epBlockBreakingBlacklist();
		onPlaceAlert = Essentials.getStatic().getSettings().getEpAlertOnPlacement();
		onUseAlert = Essentials.getStatic().getSettings().getEpAlertOnUse();
		onBreakAlert = Essentials.getStatic().getSettings().getEpAlertOnBreak();
		playerSettings = Essentials.getStatic().getSettings().getEpPlayerSettings();
		EssentialsProtectData.createSqlTable();
	}

	public void alert(User user, String item, String type)
	{
		Location loc = user.getLocation();
		for (Player p : this.getServer().getOnlinePlayers())
		{
			User alertUser = Essentials.getStatic().getUser(p);
			if (alertUser.isAuthorized("essentials.protect.alerts"))
				alertUser.sendMessage(ChatColor.DARK_AQUA + "[" + user.getName() + "] " + ChatColor.WHITE + type + ChatColor.GOLD + item + " at: " + EssentialsProtectData.formatCoords(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
		}
	}
}
