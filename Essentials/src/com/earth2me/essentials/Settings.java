package com.earth2me.essentials;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.commands.IEssentialsCommand;
import java.io.File;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;


public class Settings implements ISettings
{
	private final transient EssentialsConf config;
	private final static Logger logger = Logger.getLogger("Minecraft");
	private final transient IEssentials ess;

	public Settings(IEssentials ess)
	{
		this.ess = ess;
		config = new EssentialsConf(new File(ess.getDataFolder(), "config.yml"));
		config.setTemplateName("/config.yml");
		reloadConfig();
	}

	@Override
	public boolean getRespawnAtHome()
	{
		return config.getBoolean("respawn-at-home", false);
	}

	@Override
	public boolean getUpdateBedAtDaytime()
	{
		return config.getBoolean("update-bed-at-daytime", true);
	}

	@Override
	public List<String> getMultipleHomes()
	{
		return config.getKeys("sethome-multiple");
	}

	@Override
	public int getHomeLimit(final User user)
	{
		final List<String> homeList = getMultipleHomes();
		if (homeList == null)
		{
			//TODO: Replace this code to remove backwards compat, after settings are automatically updated
			// return getHomeLimit("default");
			return config.getInt("multiple-homes", 5);
		}
		int limit = getHomeLimit("default");
		for (String set : homeList)
		{
			if (user.isAuthorized("essentials.sethome.multiple." + set) && (limit < getHomeLimit(set)))
			{
				limit = getHomeLimit(set);
			}
		}
		return limit;
	}

	@Override
	public int getHomeLimit(final String set)
	{
		return config.getInt("sethome-multiple." + set, config.getInt("sethome-multiple.default", 3));
	}

	@Override
	public int getChatRadius()
	{
		return config.getInt("chat.radius", config.getInt("chat-radius", 0));
	}

	@Override
	public double getTeleportDelay()
	{
		return config.getDouble("teleport-delay", 0);
	}

	@Override
	public int getOversizedStackSize()
	{
		return config.getInt("oversized-stacksize", 64);
	}
	
	@Override
	public int getDefaultStackSize()
	{
		return config.getInt("default-stack-size", -1);
	}

	@Override
	public int getStartingBalance()
	{
		return config.getInt("starting-balance", 0);
	}

	@Override
	public boolean isCommandDisabled(final IEssentialsCommand cmd)
	{
		return isCommandDisabled(cmd.getName());
	}

	@Override
	public boolean isCommandDisabled(String label)
	{
		for (String c : config.getStringList("disabled-commands", new ArrayList<String>(0)))
		{
			if (!c.equalsIgnoreCase(label))
			{
				continue;
			}
			return true;
		}
		return config.getBoolean("disable-" + label.toLowerCase(Locale.ENGLISH), false);
	}

	@Override
	public boolean isCommandRestricted(IEssentialsCommand cmd)
	{
		return isCommandRestricted(cmd.getName());
	}

	@Override
	public boolean isCommandRestricted(String label)
	{
		for (String c : config.getStringList("restricted-commands", new ArrayList<String>(0)))
		{
			if (!c.equalsIgnoreCase(label))
			{
				continue;
			}
			return true;
		}
		return config.getBoolean("restrict-" + label.toLowerCase(Locale.ENGLISH), false);
	}

	@Override
	public boolean isPlayerCommand(String label)
	{
		for (String c : config.getStringList("player-commands", new ArrayList<String>(0)))
		{
			if (!c.equalsIgnoreCase(label))
			{
				continue;
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean isCommandOverridden(String name)
	{
		List<String> defaultList = new ArrayList<String>(1);
		defaultList.add("god");
		for (String c : config.getStringList("overridden-commands", defaultList))
		{
			if (!c.equalsIgnoreCase(name))
			{
				continue;
			}
			return true;
		}
		return config.getBoolean("override-" + name.toLowerCase(Locale.ENGLISH), false);
	}

	@Override
	public double getCommandCost(IEssentialsCommand cmd)
	{
		return getCommandCost(cmd.getName());
	}

	@Override
	public double getCommandCost(String label)
	{
		double cost = config.getDouble("command-costs." + label, 0.0);
		if (cost == 0.0)
		{
			cost = config.getDouble("cost-" + label, 0.0);
		}
		return cost;
	}

	@Override
	public String getNicknamePrefix()
	{
		return config.getString("nickname-prefix", "~");
	}

	@Override
	public double getTeleportCooldown()
	{
		return config.getDouble("teleport-cooldown", 0);
	}

	@Override
	public double getHealCooldown()
	{
		return config.getDouble("heal-cooldown", 0);
	}

	@Override
	public Object getKit(String name)
	{
		Map<String, Object> kits = (Map<String, Object>)config.getProperty("kits");
		for (Map.Entry<String, Object> entry : kits.entrySet())
		{
			if (entry.getKey().equalsIgnoreCase(name.replace('.', '_').replace('/', '_')))
			{
				return entry.getValue();
			}
		}
		return null;
	}

	@Override
	public Map<String, Object> getKits()
	{
		return (Map<String, Object>)config.getProperty("kits");
	}

	@Override
	public ChatColor getOperatorColor() throws Exception
	{
		String colorName = config.getString("ops-name-color", null);

		if (colorName == null)
		{
			return ChatColor.RED;
		}
		if ("none".equalsIgnoreCase(colorName) || colorName.isEmpty())
		{
			throw new Exception();
		}

		try
		{
			return ChatColor.valueOf(colorName.toUpperCase(Locale.ENGLISH));
		}
		catch (IllegalArgumentException ex)
		{
		}

		return ChatColor.getByCode(Integer.parseInt(colorName, 16));
	}

	@Override
	public int getSpawnMobLimit()
	{
		return config.getInt("spawnmob-limit", 10);
	}

	@Override
	public boolean showNonEssCommandsInHelp()
	{
		return config.getBoolean("non-ess-in-help", true);
	}

	@Override
	public boolean hidePermissionlessHelp()
	{
		return config.getBoolean("hide-permissionless-help", true);
	}

	@Override
	public int getProtectCreeperMaxHeight()
	{
		return config.getInt("protect.creeper.max-height", -1);
	}

	@Override
	public boolean areSignsDisabled()
	{
		return config.getBoolean("signs-disabled", false);
	}

	@Override
	public long getBackupInterval()
	{
		return config.getInt("backup.interval", 1440); // 1440 = 24 * 60
	}

	@Override
	public String getBackupCommand()
	{
		return config.getString("backup.command", null);
	}

	@Override
	public String getChatFormat(String group)
	{
		return config.getString("chat.group-formats." + (group == null ? "Default" : group),
								config.getString("chat.format", "&7[{GROUP}]&f {DISPLAYNAME}&7:&f {MESSAGE}"));
	}

	@Override
	public boolean getAnnounceNewPlayers()
	{
		return !config.getString("newbies.announce-format", "-").isEmpty();
	}

	@Override
	public String getAnnounceNewPlayerFormat(IUser user)
	{
		return format(config.getString("newbies.announce-format", "&dWelcome {DISPLAYNAME} to the server!"), user);
	}

	@Override
	public String format(String format, IUser user)
	{
		return format.replace('&', '§').replace("§§", "&").replace("{PLAYER}", user.getDisplayName()).replace("{DISPLAYNAME}", user.getDisplayName()).replace("{GROUP}", user.getGroup()).replace("{USERNAME}", user.getName()).replace("{ADDRESS}", user.getAddress().toString());
	}

	@Override
	public String getNewbieSpawn()
	{
		return config.getString("newbies.spawnpoint", "default");
	}

	@Override
	public boolean getPerWarpPermission()
	{
		return config.getBoolean("per-warp-permission", false);
	}

	@Override
	public boolean getSortListByGroups()
	{
		return config.getBoolean("sort-list-by-groups", true);
	}

	@Override
	public void reloadConfig()
	{
		config.load();
		noGodWorlds = new HashSet<String>(config.getStringList("no-god-in-worlds", Collections.<String>emptyList()));
	}

	@Override
	public List<Integer> itemSpawnBlacklist()
	{
		final List<Integer> epItemSpwn = new ArrayList<Integer>();
		for (String itemName : config.getString("item-spawn-blacklist", "").split(","))
		{
			itemName = itemName.trim();
			if (itemName.isEmpty())
			{
				continue;
			}
			ItemStack is;
			try
			{
				is = ess.getItemDb().get(itemName);
				epItemSpwn.add(is.getTypeId());
			}
			catch (Exception ex)
			{
				logger.log(Level.SEVERE, _("unknownItemInList", itemName, "item-spawn-blacklist"));
			}
		}
		return epItemSpwn;
	}

	@Override
	public boolean spawnIfNoHome()
	{
		return config.getBoolean("spawn-if-no-home", false);
	}

	@Override
	public boolean warnOnBuildDisallow()
	{
		return config.getBoolean("protect.disable.warn-on-build-disallow", false);
	}
	private boolean debug = false;

	@Override
	public boolean isDebug()
	{
		return debug || config.getBoolean("debug", false);
	}

	@Override
	public boolean warnOnSmite()
	{
		return config.getBoolean("warn-on-smite", true);
	}

	@Override
	public boolean permissionBasedItemSpawn()
	{
		return config.getBoolean("permission-based-item-spawn", false);
	}

	@Override
	public String getLocale()
	{
		return config.getString("locale", "");
	}

	@Override
	public String getCurrencySymbol()
	{
		return config.getString("currency-symbol", "$").substring(0, 1).replaceAll("[0-9]", "$");
	}

	@Override
	public boolean isTradeInStacks(int id)
	{
		return config.getBoolean("trade-in-stacks-" + id, false);
	}

	@Override
	public boolean isEcoDisabled()
	{
		return config.getBoolean("disable-eco", false);
	}

	@Override
	public boolean getProtectPreventSpawn(final String creatureName)
	{
		return config.getBoolean("protect.prevent.spawn." + creatureName, false);
	}

	@Override
	public List<Integer> getProtectList(final String configName)
	{
		final List<Integer> list = new ArrayList<Integer>();
		for (String itemName : config.getString(configName, "").split(","))
		{
			itemName = itemName.trim();
			if (itemName.isEmpty())
			{
				continue;
			}
			ItemStack itemStack;
			try
			{
				itemStack = ess.getItemDb().get(itemName);
				list.add(itemStack.getTypeId());
			}
			catch (Exception ex)
			{
				logger.log(Level.SEVERE, _("unknownItemInList", itemName, configName));
			}
		}
		return list;
	}

	@Override
	public String getProtectString(final String configName)
	{
		return config.getString(configName, null);
	}

	@Override
	public boolean getProtectBoolean(final String configName, boolean def)
	{
		return config.getBoolean(configName, def);
	}
	private final static double MAXMONEY = 10000000000000.0;

	@Override
	public double getMaxMoney()
	{
		double max = config.getDouble("max-money", MAXMONEY);
		if (Math.abs(max) > MAXMONEY)
		{
			max = max < 0 ? -MAXMONEY : MAXMONEY;
		}
		return max;
	}

	@Override
	public boolean isEcoLogEnabled()
	{
		return config.getBoolean("economy-log-enabled", false);
	}

	@Override
	public boolean removeGodOnDisconnect()
	{
		return config.getBoolean("remove-god-on-disconnect", false);
	}

	@Override
	public boolean changeDisplayName()
	{
		return config.getBoolean("change-displayname", true);
	}

	@Override
	public boolean useBukkitPermissions()
	{
		return config.getBoolean("use-bukkit-permissions", false);
	}

	@Override
	public boolean addPrefixSuffix()
	{
		return config.getBoolean("add-prefix-suffix", ess.getServer().getPluginManager().isPluginEnabled("EssentialsChat"));
	}

	@Override
	public boolean disablePrefix()
	{
		return config.getBoolean("disablePrefix", false);
	}

	@Override
	public boolean disableSuffix()
	{
		return config.getBoolean("disableSuffix", false);
	}

	@Override
	public long getAutoAfk()
	{
		return config.getLong("auto-afk", 300);
	}

	@Override
	public long getAutoAfkKick()
	{
		return config.getLong("auto-afk-kick", -1);
	}

	@Override
	public boolean getFreezeAfkPlayers()
	{
		return config.getBoolean("freeze-afk-players", false);
	}

	@Override
	public boolean areDeathMessagesEnabled()
	{
		return config.getBoolean("death-messages", true);
	}
	Set<String> noGodWorlds = new HashSet<String>();

	@Override
	public Set<String> getNoGodWorlds()
	{
		return noGodWorlds;
	}

	@Override
	public void setDebug(final boolean debug)
	{
		this.debug = debug;
	}

	@Override
	public boolean getRepairEnchanted()
	{
		return config.getBoolean("repair-enchanted", true);
	}

	@Override
	public boolean getIsWorldTeleportPermissions()
	{
		return config.getBoolean("world-teleport-permissions", false);
	}
	
	@Override
	public boolean registerBackInListener()
	{
		return config.getBoolean("register-back-in-listener", false);
	}

	@Override
	public boolean getDisableItemPickupWhileAfk()
	{
		return config.getBoolean("disable-item-pickup-while-afk", true);
	}
}
