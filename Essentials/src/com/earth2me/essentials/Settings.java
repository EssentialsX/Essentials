package com.earth2me.essentials;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import com.earth2me.essentials.commands.IEssentialsCommand;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.entity.CreatureType;
import org.bukkit.inventory.ItemStack;


public class Settings implements IConf
{
	private final EssentialsConf config;
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
	
	public int getStartingBalance()
	{
		return config.getInt("starting-balance", 0);
	}

	public boolean getNetherPortalsEnabled()
	{
		return isNetherEnabled() && config.getBoolean("nether.portals-enabled", false);
	}

	public boolean isCommandDisabled(final IEssentialsCommand cmd)
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

	public double getCommandCost(IEssentialsCommand cmd)
	{
		return getCommandCost(cmd.getName());
	}

	public double getCommandCost(String label)
	{
		double cost = config.getDouble("command-costs." + label, 0.0);
		if (cost == 0.0)
			cost = config.getDouble("cost-" + label, 0.0);
		return cost;
	}

	public String getCommandPrefix()
	{
		return config.getString("command-prefix", "");
	}

	public String getNicknamePrefix()
	{
		return config.getString("nickname-prefix", "~");
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
		for (Map.Entry<String, Object> entry : kits.entrySet())
		{
			if (entry.getKey().equalsIgnoreCase(name.replace('.', '_').replace('/', '_'))) {
				return entry.getValue();
			}
		}
		return null;
	}
	
	public Map<String, Object> getKits()
	{
		return (Map<String, Object>)config.getProperty("kits");
	}

	public ChatColor getOperatorColor() throws Exception
	{
		String colorName = config.getString("ops-name-color", null);

		if (colorName == null)
			return ChatColor.RED;
		if("none".equalsIgnoreCase(colorName) || colorName.isEmpty())
			throw new Exception();

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

	public Map<String, Boolean> getEpSettings()
	{
		Map<String, Boolean> epSettings = new HashMap<String, Boolean>();

		epSettings.put("protect.protect.signs", config.getBoolean("protect.protect.signs", true));
		epSettings.put("protect.protect.rails", config.getBoolean("protect.protect.rails", true));
		epSettings.put("protect.protect.block-below", config.getBoolean("protect.protect.block-below", true));
		epSettings.put("protect.protect.prevent-block-on-rails", config.getBoolean("protect.protect.prevent-block-on-rails", false));
		epSettings.put("protect.memstore", config.getBoolean("protect.memstore", false));
		return epSettings;
	}

	public Map<String, String> getEpDBSettings()
	{
		Map<String, String> epSettings = new HashMap<String, String>();
		epSettings.put("protect.datatype", config.getString("protect.datatype", "sqlite"));
		epSettings.put("protect.username", config.getString("protect.username", "root"));
		epSettings.put("protect.password", config.getString("protect.password", "root"));
		epSettings.put("protect.mysqlDb", config.getString("protect.mysqlDb", "jdbc:mysql://localhost:3306/minecraft"));
		return epSettings;
	}

	public List<Integer> getEpAlertOnPlacement()
	{
		final List<Integer> epAlertPlace = new ArrayList<Integer>();
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

	public List<Integer> getEpAlertOnUse()
	{
		final List<Integer> epAlertUse = new ArrayList<Integer>();
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

	public List<Integer> getEpAlertOnBreak()
	{
		final List<Integer> epAlertPlace = new ArrayList<Integer>();
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

	public List<Integer> epBlackListPlacement()
	{
		final List<Integer> epBlacklistPlacement = new ArrayList<Integer>();
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

	public List<Integer> epBlackListUsage()
	{
		final List<Integer> epBlackListUsage = new ArrayList<Integer>();
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

	public Map<String, Boolean> getEpGuardSettings()
	{
		final Map<String, Boolean> epSettings = new HashMap<String, Boolean>();
		epSettings.put("protect.prevent.lava-flow", config.getBoolean("protect.prevent.lava-flow", false));
		epSettings.put("protect.prevent.water-flow", config.getBoolean("protect.prevent.water-flow", false));
		epSettings.put("protect.prevent.water-bucket-flow", config.getBoolean("protect.prevent.water-bucket-flow", false));
		epSettings.put("protect.prevent.fire-spread", config.getBoolean("protect.prevent.fire-spread", true));
		epSettings.put("protect.prevent.flint-fire", config.getBoolean("protect.prevent.flint-fire", false));
		epSettings.put("protect.prevent.portal-creation", config.getBoolean("protect.prevent.portal-creation", false));
		epSettings.put("protect.prevent.lava-fire-spread", config.getBoolean("protect.prevent.lava-fire-spread", true));
		epSettings.put("protect.prevent.tnt-explosion", config.getBoolean("protect.prevent.tnt-explosion", false));
		epSettings.put("protect.prevent.creeper-explosion", config.getBoolean("protect.prevent.creeper-explosion", false));
		epSettings.put("protect.prevent.creeper-playerdamage", config.getBoolean("protect.prevent.creeper-playerdamage", false));
		epSettings.put("protect.prevent.creeper-blockdamage", config.getBoolean("protect.prevent.creeper-blockdamage", false));
		epSettings.put("protect.prevent.entitytarget", config.getBoolean("protect.prevent.entitytarget", false));
		for (CreatureType ct : CreatureType.values()) {
			final String name = ct.toString().toLowerCase();
			epSettings.put("protect.prevent.spawn."+name, config.getBoolean("protect.prevent.spawn."+name, false));
		}
		epSettings.put("protect.prevent.lightning-fire-spread", config.getBoolean("protect.prevent.lightning-fire-spread", true));
		return epSettings;
	}

	public Map<String, Boolean> getEpPlayerSettings()
	{
		final Map<String, Boolean> epPlayerSettings = new HashMap<String, Boolean>();
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

	public String getAnnounceNewPlayerFormat(IUser user)
	{
		return format(config.getString("newbies.announce-format", "&dWelcome {DISPLAYNAME} to the server!"), user);
	}

	public String format(String format, IUser user)
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

	public List<Integer> itemSpawnBlacklist()
	{
		final List<Integer> epItemSpwn = new ArrayList<Integer>();
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

	public List<Integer> epBlockBreakingBlacklist()
	{
		final List<Integer> epBreakList = new ArrayList<Integer>();
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

	public String getCurrencySymbol()
	{
		return config.getString("currency-symbol", "$").substring(0, 1).replaceAll("[0-9]", "$");
	}

	public boolean isTradeInStacks(int id)
	{
		return config.getBoolean("trade-in-stacks-" + id, false);
	}

	public boolean isEcoDisabled()
	{
		return config.getBoolean("disable-eco", false);
	}
}
