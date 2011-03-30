package com.earth2me.essentials;

import java.util.*;
import org.bukkit.ChatColor;
import com.earth2me.essentials.commands.IEssentialsCommand;
import java.io.File;
import java.util.ArrayList;
import org.bukkit.entity.CreatureType;


public class Settings implements IConf
{
	private EssentialsConf config;

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

	public long getTeleportDelay()
	{
		return config.getInt("teleport-delay", 0) * 1000L;
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

	public String getMcslKey()
	{
		return config.getString("mcsl-key", "").replaceAll("[^a-zA-Z0-9]", "");
	}

	public boolean getWhitelistEnabled()
	{
		return false;
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

	public long getTeleportCooldown()
	{
		return (long)config.getInt("teleport-cooldown", 60) * 1000L;
	}

	public long getHealCooldown()
	{
		return (long)config.getInt("heal-cooldown", 60) * 1000L;
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

	public ArrayList getEpAlertOnPlacement()
	{
		ArrayList epAlertPlace = new ArrayList();
		epAlertPlace.addAll(Arrays.asList(config.getString("protect.alert.on-placement", "").split(",")));
		return epAlertPlace;
	}

	public ArrayList getEpAlertOnUse()
	{
		ArrayList epAlertUse = new ArrayList();
		epAlertUse.addAll(Arrays.asList(config.getString("protect.alert.on-use", "").split(",")));
		return epAlertUse;
	}

	public ArrayList getEpAlertOnBreak()
	{
		ArrayList epAlertPlace = new ArrayList();
		epAlertPlace.addAll(Arrays.asList(config.getString("protect.alert.on-break", "").split(",")));
		return epAlertPlace;
	}

	public ArrayList epBlackListPlacement()
	{
		ArrayList epBlack = new ArrayList();
		epBlack.addAll(Arrays.asList(config.getString("protect.blacklist.placement", "").split(",")));
		return epBlack;
	}

	public ArrayList epBlackListUsage()
	{
		ArrayList epBlack = new ArrayList();
		epBlack.addAll(Arrays.asList(config.getString("protect.blacklist.usage", "").split(",")));
		return epBlack;
	}

	public HashMap<String, Boolean> getEpGuardSettings()
	{
		HashMap<String, Boolean> epSettings = new HashMap<String, Boolean>();
		epSettings.put("protect.prevent.lava-flow", config.getBoolean("protect.prevent.lava-flow", false));
		epSettings.put("protect.prevent.water-flow", config.getBoolean("protect.prevent.water-flow", false));
		epSettings.put("protect.prevent.water-bucket-flow", config.getBoolean("protect.prevent.water-bucket-flow", false));
		epSettings.put("protect.prevent.fire-spread", config.getBoolean("protect.prevent.fire-spread", false));
		epSettings.put("protect.prevent.flint-fire", config.getBoolean("protect.prevent.flint-fire", false));
		epSettings.put("protect.prevent.lava-fire-spread", config.getBoolean("protect.prevent.lava-fire-spread", false));
		epSettings.put("protect.prevent.tnt-explosion", config.getBoolean("protect.prevent.tnt-explosion", false));
		epSettings.put("protect.prevent.creeper-explosion", config.getBoolean("protect.prevent.creeper-explosion", false));
		epSettings.put("protect.prevent.creeper-playerdamage", config.getBoolean("protect.prevent.creeper-playerdamage", false));
		epSettings.put("protect.prevent.creeper-blockdamage", config.getBoolean("protect.prevent.creeper-blockdamage", false));
		for (CreatureType ct : CreatureType.values()) {
			String name = ct.toString().toLowerCase();
			epSettings.put("protect.prevent.spawn."+name, config.getBoolean("protect.prevent.spawn."+name, false));
		}		
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

	public ArrayList itemSpawnBlacklist()
	{
		ArrayList epItemSpwn = new ArrayList();
		epItemSpwn.addAll(Arrays.asList(config.getString("item-spawn-blacklist", "").split(",")));
		return epItemSpwn;
	}

	public ArrayList epBlockBreakingBlacklist()
	{
		ArrayList epBreakList = new ArrayList();
		epBreakList.addAll(Arrays.asList(config.getString("protect.blacklist.break", "").split(",")));
		return epBreakList;
	}
}
