package com.earth2me.essentials;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.commands.IEssentialsCommand;
import com.earth2me.essentials.signs.EssentialsSign;
import com.earth2me.essentials.signs.Signs;
import com.earth2me.essentials.textreader.IText;
import com.earth2me.essentials.textreader.SimpleTextInput;
import java.io.File;
import java.text.MessageFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;


public class Settings implements ISettings
{
	private final transient EssentialsConf config;
	private final static Logger logger = Logger.getLogger("Minecraft");
	private final transient IEssentials ess;
	private boolean metricsEnabled = true;

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
	public Set<String> getMultipleHomes()
	{
		return config.getConfigurationSection("sethome-multiple").getKeys(false);
	}

	@Override
	public int getHomeLimit(final User user)
	{
		int limit = 1;
		if (user.isAuthorized("essentials.sethome.multiple"))
		{
			limit = getHomeLimit("default");
		}

		final Set<String> homeList = getMultipleHomes();
		if (homeList != null)
		{
			for (String set : homeList)
			{
				if (user.isAuthorized("essentials.sethome.multiple." + set) && (limit < getHomeLimit(set)))
				{
					limit = getHomeLimit(set);
				}
			}
		}
		return limit;
	}

	@Override
	public int getHomeLimit(final String set)
	{
		return config.getInt("sethome-multiple." + set, config.getInt("sethome-multiple.default", 3));
	}
	private int chatRadius = 0;

	private int _getChatRadius()
	{
		return config.getInt("chat.radius", config.getInt("chat-radius", 0));
	}

	@Override
	public int getChatRadius()
	{
		return chatRadius;
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
	private Set<String> disabledCommands = new HashSet<String>();

	@Override
	public boolean isCommandDisabled(String label)
	{
		return disabledCommands.contains(label);
	}

	private Set<String> getDisabledCommands()
	{
		Set<String> disCommands = new HashSet<String>();
		for (String c : config.getStringList("disabled-commands"))
		{
			disCommands.add(c.toLowerCase(Locale.ENGLISH));
		}
		for (String c : config.getKeys(false))
		{
			if (c.startsWith("disable-"))
			{
				disCommands.add(c.substring(8).toLowerCase(Locale.ENGLISH));
			}
		}
		return disCommands;
	}

	@Override
	public boolean isCommandRestricted(IEssentialsCommand cmd)
	{
		return isCommandRestricted(cmd.getName());
	}

	@Override
	public boolean isCommandRestricted(String label)
	{
		for (String c : config.getStringList("restricted-commands"))
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
		for (String c : config.getStringList("player-commands"))
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
		for (String c : config.getStringList("overridden-commands"))
		{
			if (!c.equalsIgnoreCase(name))
			{
				continue;
			}
			return true;
		}
		return config.getBoolean("override-" + name.toLowerCase(Locale.ENGLISH), false);
	}
	private ConfigurationSection commandCosts;

	@Override
	public double getCommandCost(IEssentialsCommand cmd)
	{
		return getCommandCost(cmd.getName());
	}

	public ConfigurationSection _getCommandCosts()
	{
		if (config.isConfigurationSection("command-costs"))
		{
			final ConfigurationSection section = config.getConfigurationSection("command-costs");
			final ConfigurationSection newSection = new MemoryConfiguration();
			for (String command : section.getKeys(false))
			{
				if (section.isDouble(command))
				{
					newSection.set(command.toLowerCase(Locale.ENGLISH), section.getDouble(command));
				}
				else if (section.isInt(command))
				{
					newSection.set(command.toLowerCase(Locale.ENGLISH), (double)section.getInt(command));
				}
			}
			return newSection;
		}
		return null;
	}

	@Override
	public double getCommandCost(String name)
	{
		name = name.replace('.', '_').replace('/', '_');
		if (commandCosts != null)
		{
			return commandCosts.getDouble(name, 0.0);
		}
		return 0.0;
	}
	private String nicknamePrefix = "~";

	private String _getNicknamePrefix()
	{
		return config.getString("nickname-prefix", "~");
	}

	@Override
	public String getNicknamePrefix()
	{
		return nicknamePrefix;
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
	private ConfigurationSection kits;

	public ConfigurationSection _getKits()
	{
		if (config.isConfigurationSection("kits"))
		{
			final ConfigurationSection section = config.getConfigurationSection("kits");
			final ConfigurationSection newSection = new MemoryConfiguration();
			for (String kitItem : section.getKeys(false))
			{
				if (section.isConfigurationSection(kitItem))
				{
					newSection.set(kitItem.toLowerCase(Locale.ENGLISH), section.getConfigurationSection(kitItem));
				}
			}
			return newSection;
		}
		return null;
	}

	@Override
	public ConfigurationSection getKits()
	{
		return kits;
	}

	@Override
	public Map<String, Object> getKit(String name)
	{
		name = name.replace('.', '_').replace('/', '_');
		if (getKits() != null)
		{
			final ConfigurationSection kits = getKits();
			if (kits.isConfigurationSection(name))
			{
				return kits.getConfigurationSection(name).getValues(true);
			}
		}
		return null;
	}
	private ChatColor operatorColor = null;

	@Override
	public ChatColor getOperatorColor()
	{
		return operatorColor;
	}

	private ChatColor _getOperatorColor()
	{
		String colorName = config.getString("ops-name-color", null);

		if (colorName == null)
		{
			return ChatColor.DARK_RED;
		}
		if ("none".equalsIgnoreCase(colorName) || colorName.isEmpty())
		{
			return null;
		}

		try
		{
			return ChatColor.valueOf(colorName.toUpperCase(Locale.ENGLISH));
		}
		catch (IllegalArgumentException ex)
		{
		}

		return ChatColor.getByChar(colorName);
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
		return !signsEnabled;
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
	private Map<String, MessageFormat> chatFormats = Collections.synchronizedMap(new HashMap<String, MessageFormat>());

	@Override
	public MessageFormat getChatFormat(String group)
	{
		MessageFormat mFormat = chatFormats.get(group);
		if (mFormat == null)
		{
			String format = config.getString("chat.group-formats." + (group == null ? "Default" : group),
											 config.getString("chat.format", "&7[{GROUP}]&f {DISPLAYNAME}&7:&f {MESSAGE}"));
			format = Util.replaceFormat(format);
			format = format.replace("{DISPLAYNAME}", "%1$s");
			format = format.replace("{GROUP}", "{0}");
			format = format.replace("{MESSAGE}", "%2$s");
			format = format.replace("{WORLDNAME}", "{1}");
			format = format.replace("{SHORTWORLDNAME}", "{2}");
			format = format.replaceAll("\\{(\\D*?)\\}", "\\[$1\\]");
			mFormat = new MessageFormat(format);
			chatFormats.put(group, mFormat);
		}
		return mFormat;
	}

	@Override
	public boolean getAnnounceNewPlayers()
	{
		return !config.getString("newbies.announce-format", "-").isEmpty();
	}

	@Override
	public IText getAnnounceNewPlayerFormat()
	{
		return new SimpleTextInput(Util.replaceFormat(config.getString("newbies.announce-format", "&dWelcome {DISPLAYNAME} to the server!")));
	}

	@Override
	public String getNewPlayerKit()
	{
		return config.getString("newbies.kit", "");
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
		noGodWorlds = new HashSet<String>(config.getStringList("no-god-in-worlds"));
		enabledSigns = _getEnabledSigns();
		teleportInvulnerability = _isTeleportInvulnerability();
		disableItemPickupWhileAfk = _getDisableItemPickupWhileAfk();
		registerBackInListener = _registerBackInListener();
		cancelAfkOnMove = _cancelAfkOnMove();
		getFreezeAfkPlayers = _getFreezeAfkPlayers();
		itemSpawnBl = _getItemSpawnBlacklist();
		loginAttackDelay = _getLoginAttackDelay();
		signUsePerSecond = _getSignUsePerSecond();
		kits = _getKits();
		chatFormats.clear();
		changeDisplayName = _changeDisplayName();
		disabledCommands = getDisabledCommands();
		nicknamePrefix = _getNicknamePrefix();
		operatorColor = _getOperatorColor();
		changePlayerListName = _changePlayerListName();
		configDebug = _isDebug();
		prefixsuffixconfigured = _isPrefixSuffixConfigured();
		addprefixsuffix = _addPrefixSuffix();
		disablePrefix = _disablePrefix();
		disableSuffix = _disableSuffix();
		chatRadius = _getChatRadius();
		commandCosts = _getCommandCosts();
		warnOnBuildDisallow = _warnOnBuildDisallow();
	}
	private List<Integer> itemSpawnBl = new ArrayList<Integer>();

	@Override
	public List<Integer> itemSpawnBlacklist()
	{
		return itemSpawnBl;
	}

	private List<Integer> _getItemSpawnBlacklist()
	{
		final List<Integer> epItemSpwn = new ArrayList<Integer>();
		if (ess.getItemDb() == null)
		{
			logger.log(Level.FINE, "Aborting ItemSpawnBL read, itemDB not yet loaded.");
			return epItemSpwn;
		}
		for (String itemName : config.getString("item-spawn-blacklist", "").split(","))
		{
			itemName = itemName.trim();
			if (itemName.isEmpty())
			{
				continue;
			}
			try
			{
				final ItemStack iStack = ess.getItemDb().get(itemName);
				epItemSpwn.add(iStack.getTypeId());
			}
			catch (Exception ex)
			{
				logger.log(Level.SEVERE, _("unknownItemInList", itemName, "item-spawn-blacklist"));
			}
		}
		return epItemSpwn;
	}
	private List<EssentialsSign> enabledSigns = new ArrayList<EssentialsSign>();
	private boolean signsEnabled = false;

	@Override
	public List<EssentialsSign> enabledSigns()
	{
		return enabledSigns;
	}

	private List<EssentialsSign> _getEnabledSigns()
	{
		List<EssentialsSign> newSigns = new ArrayList<EssentialsSign>();

		for (String signName : config.getStringList("enabledSigns"))
		{
			signName = signName.trim().toUpperCase(Locale.ENGLISH);
			if (signName.isEmpty())
			{
				continue;
			}
			if (signName.equals("COLOR") || signName.equals("COLOUR"))
			{
				signsEnabled = true;
				continue;
			}
			try
			{
				newSigns.add(Signs.valueOf(signName).getSign());
			}
			catch (Exception ex)
			{
				logger.log(Level.SEVERE, _("unknownItemInList", signName, "enabledSigns"));
				continue;
			}
			signsEnabled = true;
		}
		return newSigns;
	}
	private boolean warnOnBuildDisallow;

	private boolean _warnOnBuildDisallow()
	{
		return config.getBoolean("protect.disable.warn-on-build-disallow", false);
	}

	@Override
	public boolean warnOnBuildDisallow()
	{
		return warnOnBuildDisallow;
	}
	private boolean debug = false;
	private boolean configDebug = false;

	private boolean _isDebug()
	{
		return config.getBoolean("debug", false);
	}

	@Override
	public boolean isDebug()
	{
		return debug || configDebug;
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
		return config.getString("currency-symbol", "$").concat("$").substring(0, 1).replaceAll("[0-9]", "$");
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
	private final static double MINMONEY = -10000000000000.0;

	@Override
	public double getMinMoney()
	{
		double min = config.getDouble("min-money", MINMONEY);
		if (min > 0)
		{
			min = -min;
		}
		if (min < MINMONEY)
		{
			min = MINMONEY;
		}
		return min;
	}

	@Override
	public boolean isEcoLogEnabled()
	{
		return config.getBoolean("economy-log-enabled", false);
	}

	@Override
	public boolean isEcoLogUpdateEnabled()
	{
		return config.getBoolean("economy-log-update-enabled", false);
	}

	@Override
	public boolean removeGodOnDisconnect()
	{
		return config.getBoolean("remove-god-on-disconnect", false);
	}
	private boolean changeDisplayName = true;

	private boolean _changeDisplayName()
	{
		return config.getBoolean("change-displayname", true);
	}

	@Override
	public boolean changeDisplayName()
	{
		return changeDisplayName;
	}
	private boolean changePlayerListName = false;

	private boolean _changePlayerListName()
	{
		return config.getBoolean("change-playerlist", false);
	}

	@Override
	public boolean changePlayerListName()
	{
		return changePlayerListName;
	}

	@Override
	public boolean useBukkitPermissions()
	{
		return config.getBoolean("use-bukkit-permissions", false);
	}
	private boolean prefixsuffixconfigured = false;
	private boolean addprefixsuffix = false;

	private boolean _addPrefixSuffix()
	{
		return config.getBoolean("add-prefix-suffix", false);
	}

	private boolean _isPrefixSuffixConfigured()
	{
		return config.hasProperty("add-prefix-suffix");
	}

	@Override
	public boolean addPrefixSuffix()
	{
		return prefixsuffixconfigured ? addprefixsuffix : ess.getServer().getPluginManager().isPluginEnabled("EssentialsChat");
	}
	private boolean disablePrefix = false;

	private boolean _disablePrefix()
	{
		return config.getBoolean("disablePrefix", false);
	}

	@Override
	public boolean disablePrefix()
	{
		return disablePrefix;
	}
	private boolean disableSuffix = false;

	private boolean _disableSuffix()
	{
		return config.getBoolean("disableSuffix", false);
	}

	@Override
	public boolean disableSuffix()
	{
		return disableSuffix;
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
	private boolean getFreezeAfkPlayers;

	@Override
	public boolean getFreezeAfkPlayers()
	{
		return getFreezeAfkPlayers;
	}

	private boolean _getFreezeAfkPlayers()
	{
		return config.getBoolean("freeze-afk-players", false);
	}
	private boolean cancelAfkOnMove;

	@Override
	public boolean cancelAfkOnMove()
	{
		return cancelAfkOnMove;
	}

	private boolean _cancelAfkOnMove()
	{
		return config.getBoolean("cancel-afk-on-move", true);
	}

	@Override
	public boolean areDeathMessagesEnabled()
	{
		return config.getBoolean("death-messages", true);
	}
	private Set<String> noGodWorlds = new HashSet<String>();

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
	public boolean isWorldTeleportPermissions()
	{
		return config.getBoolean("world-teleport-permissions", false);
	}

	@Override
	public boolean isWorldHomePermissions()
	{
		return config.getBoolean("world-home-permissions", false);
	}
	private boolean registerBackInListener;

	@Override
	public boolean registerBackInListener()
	{
		return registerBackInListener;
	}

	private boolean _registerBackInListener()
	{
		return config.getBoolean("register-back-in-listener", false);
	}
	private boolean disableItemPickupWhileAfk;

	@Override
	public boolean getDisableItemPickupWhileAfk()
	{
		return disableItemPickupWhileAfk;
	}

	private boolean _getDisableItemPickupWhileAfk()
	{
		return config.getBoolean("disable-item-pickup-while-afk", false);
	}

	@Override
	public EventPriority getRespawnPriority()
	{
		String priority = config.getString("respawn-listener-priority", "normal").toLowerCase(Locale.ENGLISH);
		if ("lowest".equals(priority))
		{
			return EventPriority.LOWEST;
		}
		if ("low".equals(priority))
		{
			return EventPriority.LOW;
		}
		if ("normal".equals(priority))
		{
			return EventPriority.NORMAL;
		}
		if ("high".equals(priority))
		{
			return EventPriority.HIGH;
		}
		if ("highest".equals(priority))
		{
			return EventPriority.HIGHEST;
		}
		return EventPriority.NORMAL;
	}

	@Override
	public long getTpaAcceptCancellation()
	{
		return config.getLong("tpa-accept-cancellation", 0);
	}

	@Override
	public boolean isMetricsEnabled()
	{
		return metricsEnabled;
	}

	@Override
	public void setMetricsEnabled(boolean metricsEnabled)
	{
		this.metricsEnabled = metricsEnabled;
	}
	private boolean teleportInvulnerability;

	@Override
	public long getTeleportInvulnerability()
	{
		return config.getLong("teleport-invulnerability", 0) * 1000;
	}

	private boolean _isTeleportInvulnerability()
	{
		return (config.getLong("teleport-invulnerability", 0) > 0);
	}

	@Override
	public boolean isTeleportInvulnerability()
	{
		return teleportInvulnerability;
	}
	private long loginAttackDelay;

	private long _getLoginAttackDelay()
	{
		return config.getLong("login-attack-delay", 0) * 1000;
	}

	@Override
	public long getLoginAttackDelay()
	{
		return loginAttackDelay;
	}
	private int signUsePerSecond;

	private int _getSignUsePerSecond()
	{
		final int perSec = config.getInt("sign-use-per-second", 4);
		return perSec > 0 ? perSec : 1;
	}

	@Override
	public int getSignUsePerSecond()
	{
		return signUsePerSecond;
	}

	@Override
	public double getMaxFlySpeed()
	{
		double maxSpeed = config.getDouble("max-fly-speed", 1.0);
		return maxSpeed > 1.0 ? 1.0 : Math.abs(maxSpeed);
	}

	@Override
	public double getMaxWalkSpeed()
	{
		double maxSpeed = config.getDouble("max-walk-speed", 0.8);
		return maxSpeed > 1.0 ? 1.0 : Math.abs(maxSpeed);
	}
}
