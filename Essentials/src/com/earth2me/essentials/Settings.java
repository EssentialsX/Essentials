package com.earth2me.essentials;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import com.earth2me.essentials.commands.IEssentialsCommand;
import java.io.File;
import java.util.ArrayList;
import org.bukkit.entity.CreatureType;
import org.bukkit.inventory.ItemStack;


public class Settings implements IConf
{
	private EssentialsConf config;
	private final static Logger logger = Logger.getLogger("Minecraft");

	public Settings(File dataFolder)
	{
		config = new EssentialsConf(new File(dataFolder, "config.yml"));
		config.setTemplateName("/config.yml");
		config.load();
	}

	public boolean getRespawnAtHome()
	{
		return config.getBoolean("respawn-at-home", false);
	}

	public boolean getBedSetsHome()
	{
		return config.getBoolean("bed-sethome", false);
	}

	public int getChatRadius()
	{
		return config.getInt("chat.radius", config.getInt("chat-radius", 0));
	}

	public double getTeleportDelay()
	{
		return config.getDouble("teleport-delay", 0);
	}

	public int getDefaultStackSize()
	{
		return config.getInt("default-stack-size", 64);
	}
	
	public String getCurrency()
	{
		return config.getString("currency-name", "Coin");
	}
	
	public String getCurrencyPlural()
	{
		return config.getString("currency-name-plural", "Coins");
	}
	
	public int getStartingBalance()
	{
		return config.getInt("starting-balance", 0);
	}

	public boolean getNetherPortalsEnabled()
	{
		return isNetherEnabled() && config.getBoolean("nether.portals-enabled", false);
	}

	public boolean isCommandDisabled(IEssentialsCommand cmd)
	{
		return isCommandDisabled(cmd.getName());
	}

	public boolean isCommandDisabled(String label)
	{
		for (String c : config.getStringList("disabled-commands", new ArrayList<String>(0)))
		{
			if (!c.equalsIgnoreCase(label)) continue;
			return true;
		}
		return config.getBoolean("disable-" + label.toLowerCase(), false);
	}

	public boolean isCommandRestricted(IEssentialsCommand cmd)
	{
		return isCommandRestricted(cmd.getName());
	}

	public boolean isCommandRestricted(String label)
	{
		for (String c : config.getStringList("restricted-commands", new ArrayList<String>(0)))
		{
			if (!c.equalsIgnoreCase(label)) continue;
			return true;
		}
		return config.getBoolean("restrict-" + label.toLowerCase(), false);
	}

	public boolean isCommandOverridden(String name)
	{
		List<String> defaultList = new ArrayList<String>(1);
		defaultList.add("god");
		for (String c : config.getStringList("overridden-commands", defaultList))
		{
			if (!c.equalsIgnoreCase(name))
				continue;
			return true;
		}
		return config.getBoolean("override-" + name.toLowerCase(), false);
	}

	public int getCommandCost(IEssentialsCommand cmd)
	{
		return getCommandCost(cmd.getName());
	}

	public int getCommandCost(String label)
	{
		int cost = config.getInt("command-costs." + label, 0);
		if (cost == 0)
			cost = config.getInt("cost-" + label, 0);
		return cost;
	}

	public String getCommandPrefix()
	{
		return config.getString("command-prefix", "");
	}

	public String getNicknamePrefix()
	{
		return config.getString("nickname-prefix", "");
	}

	public double getTeleportCooldown()
	{
		return config.getDouble("teleport-cooldown", 60);
	}

	public double getHealCooldown()
	{
		return config.getDouble("heal-cooldown", 60);
	}

	public Object getKit(String name)
	{
		Map<String, Object> kits = (Map<String, Object>)config.getProperty("kits");
		return kits.get(name.replace('.', '_').replace('/', '_'));
	}

	public ChatColor getOperatorColor()
	{
		String colorName = config.getString("ops-name-color", null);

		if (colorName == null)
			return ChatColor.RED;
		if("none".equalsIgnoreCase(colorName) || colorName.isEmpty())
			return ChatColor.WHITE;

		try
		{
			return ChatColor.valueOf(colorName.toUpperCase());
		}
		catch (IllegalArgumentException ex)
		{
		}

		return ChatColor.getByCode(Integer.parseInt(colorName, 16));
	}

	public boolean getReclaimSetting()
	{
		return config.getBoolean("reclaim-onlogout", true);
	}

	public String getNetherName()
	{
		return config.getString("nether.folder", "nether");
	}

	public boolean isNetherEnabled()
	{
		return config.getBoolean("nether.enabled", true);
	}

	public int getSpawnMobLimit()
	{
		return config.getInt("spawnmob-limit", 10);
	}

	public boolean showNonEssCommandsInHelp()
	{
		return config.getBoolean("non-ess-in-help", true);
	}

	public HashMap<String, Boolean> getEpSettings()
	{
		HashMap<String, Boolean> epSettings = new HashMap<String, Boolean>();

		epSettings.put("protect.protect.signs", config.getBoolean("protect.protect.signs", true));
		epSettings.put("protect.protect.rails", config.getBoolean("protect.protect.rails", true));
		epSettings.put("protect.protect.block-below", config.getBoolean("protect.protect.block-below", true));
		epSettings.put("protect.protect.prevent-block-on-rails", config.getBoolean("protect.protect.prevent-block-on-rails", false));
		return epSettings;
	}

	public HashMap<String, String> getEpDBSettings()
	{
		HashMap<String, String> epSettings = new HashMap<String, String>();
		epSettings.put("protect.datatype", config.getString("protect.datatype", "sqlite"));
		epSettings.put("protect.username", config.getString("protect.username", "root"));
		epSettings.put("protect.password", config.getString("protect.password", "root"));
		epSettings.put("protect.mysqlDb", config.getString("protect.mysqlDb", "jdbc:mysql://localhost:3306/minecraft"));
		return epSettings;
	}

	public ArrayList<Integer> getEpAlertOnPlacement()
	{
		ArrayList<Integer> epAlertPlace = new ArrayList<Integer>();
		for (String itemName : config.getString("protect.alert.on-placement", "").split(",")) {
			itemName = itemName.trim();
			if (itemName.isEmpty()) {
				continue;
			}
			ItemStack is;
			try {
				is = ItemDb.get(itemName);
				epAlertPlace.add(is.getTypeId());
			} catch (Exception ex) {
				logger.log(Level.SEVERE, Util.format("unknownItemInList", itemName, "alert.on-placement"));
			}
		}
		return epAlertPlace;
	}

	public ArrayList<Integer> getEpAlertOnUse()
	{
		ArrayList<Integer> epAlertUse = new ArrayList<Integer>();
		for (String itemName : config.getString("protect.alert.on-use", "").split(",")) {
			itemName = itemName.trim();
			if (itemName.isEmpty()) {
				continue;
			}
			ItemStack is;
			try {
				is = ItemDb.get(itemName);
				epAlertUse.add(is.getTypeId());
			} catch (Exception ex) {
				logger.log(Level.SEVERE, Util.format("unknownItemInList", itemName, "alert.on-use"));
			}
		}
		return epAlertUse;
	}

	public ArrayList<Integer> getEpAlertOnBreak()
	{
		ArrayList<Integer> epAlertPlace = new ArrayList<Integer>();
		for (String itemName : config.getString("protect.alert.on-break", "").split(",")) {
			itemName = itemName.trim();
			if (itemName.isEmpty()) {
				continue;
			}
			ItemStack is;
			try {
				is = ItemDb.get(itemName);
				epAlertPlace.add(is.getTypeId());
			} catch (Exception ex) {
				logger.log(Level.SEVERE, Util.format("unknownItemInList", itemName, "alert.on-break"));
			}
		}
		return epAlertPlace;
	}

	public ArrayList<Integer> epBlackListPlacement()
	{
		ArrayList<Integer> epBlacklistPlacement = new ArrayList<Integer>();
		for (String itemName : config.getString("protect.blacklist.placement", "").split(",")) {
			itemName = itemName.trim();
			if (itemName.isEmpty()) {
				continue;
			}
			ItemStack is;
			try {
				is = ItemDb.get(itemName);
				epBlacklistPlacement.add(is.getTypeId());
			} catch (Exception ex) {
				logger.log(Level.SEVERE, Util.format("unknownItemInList", itemName, "blacklist.placement"));
			}
		}
		return epBlacklistPlacement;
	}

	public ArrayList<Integer> epBlackListUsage()
	{
		ArrayList<Integer> epBlackListUsage = new ArrayList<Integer>();
		for (String itemName : config.getString("protect.blacklist.usage", "").split(",")) {
			itemName = itemName.trim();
			if (itemName.isEmpty()) {
				continue;
			}
			ItemStack is;
			try {
				is = ItemDb.get(itemName);
				epBlackListUsage.add(is.getTypeId());
			} catch (Exception ex) {
				logger.log(Level.SEVERE, Util.format("unknownItemInList", itemName, "blacklist.usage"));
			}
		}
		return epBlackListUsage;
	}

	public HashMap<String, Boolean> getEpGuardSettings()
	{
		HashMap<String, Boolean> epSettings = new HashMap<String, Boolean>();
		epSettings.put("protect.prevent.lava-flow", config.getBoolean("protect.prevent.lava-flow", false));
		epSettings.put("protect.prevent.water-flow", config.getBoolean("protect.prevent.water-flow", false));
		epSettings.put("protect.prevent.water-bucket-flow", config.getBoolean("protect.prevent.water-bucket-flow", false));
		epSettings.put("protect.prevent.fire-spread", config.getBoolean("protect.prevent.fire-spread", false));
		epSettings.put("protect.prevent.flint-fire", config.getBoolean("protect.prevent.flint-fire", false));
		epSettings.put("protect.prevent.portal-creation", config.getBoolean("protect.prevent.portal-creation", false));
		epSettings.put("protect.prevent.lava-fire-spread", config.getBoolean("protect.prevent.lava-fire-spread", false));
		epSettings.put("protect.prevent.tnt-explosion", config.getBoolean("protect.prevent.tnt-explosion", false));
		epSettings.put("protect.prevent.creeper-explosion", config.getBoolean("protect.prevent.creeper-explosion", false));
		epSettings.put("protect.prevent.creeper-playerdamage", config.getBoolean("protect.prevent.creeper-playerdamage", false));
		epSettings.put("protect.prevent.creeper-blockdamage", config.getBoolean("protect.prevent.creeper-blockdamage", false));
		epSettings.put("protect.prevent.entitytarget", config.getBoolean("protect.prevent.entitytarget", false));
		for (CreatureType ct : CreatureType.values()) {
			String name = ct.toString().toLowerCase();
			epSettings.put("protect.prevent.spawn."+name, config.getBoolean("protect.prevent.spawn."+name, false));
		}
		epSettings.put("protect.prevent.lightning-fire-spread", config.getBoolean("protect.prevent.lightning-fire-spread", false));
		return epSettings;
	}

	public HashMap<String, Boolean> getEpPlayerSettings()
	{
		HashMap<String, Boolean> epPlayerSettings = new HashMap<String, Boolean>();
		epPlayerSettings.put("protect.disable.fall", config.getBoolean("protect.disable.fall", false));
		epPlayerSettings.put("protect.disable.pvp", config.getBoolean("protect.disable.pvp", false));
		epPlayerSettings.put("protect.disable.drown", config.getBoolean("protect.disable.drown", false));
		epPlayerSettings.put("protect.disable.suffocate", config.getBoolean("protect.disable.suffocate", false));
		epPlayerSettings.put("protect.disable.lavadmg", config.getBoolean("protect.disable.lavadmg", false));
		epPlayerSettings.put("protect.disable.projectiles", config.getBoolean("protect.disable.projectiles", false));
		epPlayerSettings.put("protect.disable.contactdmg", config.getBoolean("protect.disable.contactdmg", false));
		epPlayerSettings.put("protect.disable.firedmg", config.getBoolean("protect.disable.firedmg", false));
		epPlayerSettings.put("protect.disable.build", config.getBoolean("protect.disable.build", false));
		epPlayerSettings.put("protect.disable.lightning", config.getBoolean("protect.disable.lightning", false));
		epPlayerSettings.put("protect.disable.weather.lightning", config.getBoolean("protect.disable.weather.lightning", false));
		epPlayerSettings.put("protect.disable.weather.storm", config.getBoolean("protect.disable.weather.storm", false));
		epPlayerSettings.put("protect.disable.weather.thunder", config.getBoolean("protect.disable.weather.thunder", false));
		return epPlayerSettings;

	}
	
	public int getEpCreeperMaxHeight()
	{
		return config.getInt("protect.creeper.max-height", -1);
	}

	public boolean areSignsDisabled()
	{
		return config.getBoolean("signs-disabled", false);
	}

	public long getBackupInterval()
	{
		return config.getInt("backup.interval", 1440); // 1440 = 24 * 60
	}

	public String getBackupCommand()
	{
		return config.getString("backup.command", null);
	}

	public String getChatFormat(String group)
	{
		return config.getString("chat.group-formats." + (group == null ? "Default" : group),
								config.getString("chat.format", "&7[{GROUP}]&f {DISPLAYNAME}&7:&f {MESSAGE}"));
	}

	public boolean getGenerateExitPortals()
	{
		return config.getBoolean("nether.generate-exit-portals", true);
	}

	public boolean getAnnounceNewPlayers()
	{
		return !config.getString("newbies.announce-format", "-").isEmpty();
	}

	public String getAnnounceNewPlayerFormat(User user)
	{
		return format(config.getString("newbies.announce-format", "&dWelcome {DISPLAYNAME} to the server!"), user);
	}

	public String format(String format, User user)
	{
		return format.replace('&', '§').replace("§§", "&").replace("{PLAYER}", user.getDisplayName()).replace("{DISPLAYNAME}", user.getDisplayName()).replace("{GROUP}", user.getGroup()).replace("{USERNAME}", user.getName()).replace("{ADDRESS}", user.getAddress().toString());
	}

	public String getNewbieSpawn()
	{
		return config.getString("newbies.spawnpoint", "default");
	}
        public boolean getPerWarpPermission()
	{
		return config.getBoolean("per-warp-permission", false);
	}
	
	public boolean getSortListByGroups()
	{
		return config.getBoolean("sort-list-by-groups", true);
	}

	public void reloadConfig() {
		config.load();
	}

	public ArrayList<Integer> itemSpawnBlacklist()
	{
		ArrayList<Integer> epItemSpwn = new ArrayList<Integer>();
		for (String itemName : config.getString("item-spawn-blacklist", "").split(",")) {
			itemName = itemName.trim();
			if (itemName.isEmpty()) {
				continue;
			}
			ItemStack is;
			try {
				is = ItemDb.get(itemName);
				epItemSpwn.add(is.getTypeId());
			} catch (Exception ex) {
				logger.log(Level.SEVERE, Util.format("unknownItemInList", itemName, "item-spawn-blacklist"));
			}
		}
		return epItemSpwn;
	}

	public ArrayList<Integer> epBlockBreakingBlacklist()
	{
		ArrayList<Integer> epBreakList = new ArrayList<Integer>();
		for (String itemName : config.getString("protect.blacklist.break", "").split(",")) {
			itemName = itemName.trim();
			if (itemName.isEmpty()) {
				continue;
			}
			ItemStack is;
			try {
				is = ItemDb.get(itemName);
				epBreakList.add(is.getTypeId());
			} catch (Exception ex) {
				logger.log(Level.SEVERE, Util.format("unknownItemInList", itemName, "blacklist.break"));
			}
		}
		return epBreakList;
	}

	public boolean spawnIfNoHome()
	{
		return config.getBoolean("spawn-if-no-home", false);
	}

	public boolean warnOnBuildDisallow()
	{
		return config.getBoolean("protect.disable.warn-on-build-disallow", false);
	}

	public boolean use1to1RatioInNether()
	{
		return config.getBoolean("nether.use-1to1-ratio", false);
	}
	
	public double getNetherRatio()
	{
		if (config.getBoolean("nether.use-1to1-ratio", false)) {
			return 1.0;
		}
		return config.getDouble("nether.ratio", 16.0);
	}
	
	public boolean isDebug()
	{
		return config.getBoolean("debug", false);
	}

	public boolean warnOnSmite()
	{
		return config.getBoolean("warn-on-smite" ,true);
	}
	
	public boolean permissionBasedItemSpawn()
	{
		return config.getBoolean("permission-based-item-spawn", false);
	}

	public String getLocale()
	{
		return config.getString("locale", "");
	}
}
