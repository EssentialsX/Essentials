package com.earth2me.essentials;

import static com.earth2me.essentials.I18n.tl;
import com.earth2me.essentials.commands.IEssentialsCommand;
import com.earth2me.essentials.signs.EssentialsSign;
import com.earth2me.essentials.signs.Signs;
import com.earth2me.essentials.textreader.IText;
import com.earth2me.essentials.textreader.SimpleTextInput;
import com.earth2me.essentials.utils.FormatUtil;
import java.io.File;
import java.math.BigDecimal;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.ess3.api.IEssentials;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;


public class Settings implements net.ess3.api.ISettings
{
	private final transient EssentialsConf config;
	private static final Logger logger = Logger.getLogger("Essentials");
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
		final ConfigurationSection section = config.getConfigurationSection("sethome-multiple");
		return section == null ? null : section.getKeys(false);
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
	// #easteregg
	private char chatShout = '!';

	private char _getChatShout()
	{
		return config.getString("chat.shout", "!").charAt(0);
	}

	@Override
	public char getChatShout()
	{
		return chatShout;
	}
	// #easteregg
	private char chatQuestion = '?';

	private char _getChatQuestion()
	{
		return config.getString("chat.question", "?").charAt(0);
	}

	@Override
	public char getChatQuestion()
	{
		return chatQuestion;
	}
	private boolean teleportSafety;

	public boolean _isTeleportSafetyEnabled()
	{
		return config.getBoolean("teleport-safety", true);
	}

	@Override
	public boolean isTeleportSafetyEnabled()
	{
		return teleportSafety;
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
	public BigDecimal getStartingBalance()
	{
		return config.getBigDecimal("starting-balance", BigDecimal.ZERO);
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
	public BigDecimal getCommandCost(IEssentialsCommand cmd)
	{
		return getCommandCost(cmd.getName());
	}

	private ConfigurationSection _getCommandCosts()
	{
		if (config.isConfigurationSection("command-costs"))
		{
			final ConfigurationSection section = config.getConfigurationSection("command-costs");
			final ConfigurationSection newSection = new MemoryConfiguration();
			for (String command : section.getKeys(false))
			{
				if (command.charAt(0) == '/')
				{
					ess.getLogger().warning("Invalid command cost. '" + command + "' should not start with '/'.");
				}
				if (section.isDouble(command))
				{
					newSection.set(command.toLowerCase(Locale.ENGLISH), section.getDouble(command));
				}
				else if (section.isInt(command))
				{
					newSection.set(command.toLowerCase(Locale.ENGLISH), (double)section.getInt(command));
				}
				else if (section.isString(command))
				{
					String costString = section.getString(command);
					try
					{
						double cost = Double.parseDouble(costString.trim().replace(getCurrencySymbol(), "").replaceAll("\\W", ""));
						newSection.set(command.toLowerCase(Locale.ENGLISH), cost);
					}
					catch (NumberFormatException ex)
					{
						ess.getLogger().warning("Invalid command cost for: " + command + " (" + costString + ")");
					}

				}
				else
				{
					ess.getLogger().warning("Invalid command cost for: " + command);
				}
			}
			return newSection;
		}
		return null;
	}

	@Override
	public BigDecimal getCommandCost(String name)
	{
		name = name.replace('.', '_').replace('/', '_');
		if (commandCosts != null)
		{
			return EssentialsConf.toBigDecimal(commandCosts.getString(name), BigDecimal.ZERO);
		}
		return BigDecimal.ZERO;
	}
	private Set<String> socialSpyCommands = new HashSet<String>();

	private Set<String> _getSocialSpyCommands()
	{
		Set<String> socialspyCommands = new HashSet<String>();

		if (config.isList("socialspy-commands"))
		{
			for (String c : config.getStringList("socialspy-commands"))
			{
				socialspyCommands.add(c.toLowerCase(Locale.ENGLISH));
			}
		}
		else
		{
			socialspyCommands.addAll(Arrays.asList("msg", "r", "mail", "m", "whisper", "emsg", "t", "tell", "er", "reply", "ereply", "email", "action", "describe", "eme", "eaction", "edescribe", "etell", "ewhisper", "pm"));
		}

		return socialspyCommands;
	}

	@Override
	public Set<String> getSocialSpyCommands()
	{
		return socialSpyCommands;
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

	private ConfigurationSection _getKits()
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
	private final Map<String, String> chatFormats = Collections.synchronizedMap(new HashMap<String, String>());

	@Override
	public String getChatFormat(String group)
	{
		String mFormat = chatFormats.get(group);
		if (mFormat == null)
		{
			mFormat = config.getString("chat.group-formats." + (group == null ? "Default" : group),
									   config.getString("chat.format", "&7[{GROUP}]&r {DISPLAYNAME}&7:&r {MESSAGE}"));
			mFormat = FormatUtil.replaceFormat(mFormat);
			mFormat = mFormat.replace("{DISPLAYNAME}", "%1$s");
			mFormat = mFormat.replace("{MESSAGE}", "%2$s");
			mFormat = mFormat.replace("{GROUP}", "{0}");
			mFormat = mFormat.replace("{WORLD}", "{1}");
			mFormat = mFormat.replace("{WORLDNAME}", "{1}");
			mFormat = mFormat.replace("{SHORTWORLDNAME}", "{2}");
			mFormat = mFormat.replace("{TEAMPREFIX}", "{3}");
			mFormat = mFormat.replace("{TEAMSUFFIX}", "{4}");
			mFormat = mFormat.replace("{TEAMNAME}", "{5}");
			mFormat = "§r".concat(mFormat);
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
		return new SimpleTextInput(FormatUtil.replaceFormat(config.getString("newbies.announce-format", "&dWelcome {DISPLAYNAME} to the server!")));
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
	public Map<String, Object> getListGroupConfig()
	{
		if (config.isConfigurationSection("list"))
		{
			Map<String, Object> values = config.getConfigurationSection("list").getValues(false);
			if (!values.isEmpty())
			{
				return values;
			}
		}
		Map<String, Object> defaultMap = new HashMap<String, Object>();
		if (config.getBoolean("sort-list-by-groups", false))
		{
			defaultMap.put("ListByGroup", "ListByGroup");
		}
		else
		{
			defaultMap.put("Players", "*");
		}
		return defaultMap;
	}

	@Override
	public void reloadConfig()
	{
		config.load();
		noGodWorlds = new HashSet<String>(config.getStringList("no-god-in-worlds"));
		enabledSigns = _getEnabledSigns();
		teleportSafety = _isTeleportSafetyEnabled();
		teleportInvulnerabilityTime = _getTeleportInvulnerability();
		teleportInvulnerability = _isTeleportInvulnerability();
		disableItemPickupWhileAfk = _getDisableItemPickupWhileAfk();
		registerBackInListener = _registerBackInListener();
		cancelAfkOnInteract = _cancelAfkOnInteract();
		cancelAfkOnMove = _cancelAfkOnMove() && cancelAfkOnInteract;
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
		chatShout = _getChatShout();
		chatQuestion = _getChatQuestion();
		commandCosts = _getCommandCosts();
		socialSpyCommands = _getSocialSpyCommands();
		warnOnBuildDisallow = _warnOnBuildDisallow();
		mailsPerMinute = _getMailsPerMinute();
		maxMoney = _getMaxMoney();
		minMoney = _getMinMoney();
		permissionsLagWarning = _getPermissionsLagWarning();
		economyLagWarning = _getEconomyLagWarning();
		economyLog = _isEcoLogEnabled();
		economyLogUpdate = _isEcoLogUpdateEnabled();
		economyDisabled = _isEcoDisabled();
		allowSilentJoin = _allowSilentJoinQuit();
		customJoinMessage = _getCustomJoinMessage();
		isCustomJoinMessage = !customJoinMessage.equals("none");
		customQuitMessage = _getCustomQuitMessage();
		isCustomQuitMessage = !customQuitMessage.equals("none");
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
				logger.log(Level.SEVERE, tl("unknownItemInList", itemName, "item-spawn-blacklist"));
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
				logger.log(Level.SEVERE, tl("unknownItemInList", signName, "enabledSigns"));
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

	//This method should always only return one character due to the implementation of the calling methods
	//If you need to use a string currency, for example "coins", use the translation key 'currency'.
	@Override
	public String getCurrencySymbol()
	{
		return config.getString("currency-symbol", "$").concat("$").substring(0, 1).replaceAll("[0-9]", "$");
	}

	// #easteregg
	@Override
	public boolean isTradeInStacks(int id)
	{
		return config.getBoolean("trade-in-stacks-" + id, false);
	}
	// #easteregg
	private boolean economyDisabled = false;

	public boolean _isEcoDisabled()
	{
		return config.getBoolean("disable-eco", false);
	}

	@Override
	public boolean isEcoDisabled()
	{
		return economyDisabled;
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
				logger.log(Level.SEVERE, tl("unknownItemInList", itemName, configName));
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
	private static final BigDecimal MAXMONEY = new BigDecimal("10000000000000");
	private BigDecimal maxMoney = MAXMONEY;

	private BigDecimal _getMaxMoney()
	{
		return config.getBigDecimal("max-money", MAXMONEY);
	}

	@Override
	public BigDecimal getMaxMoney()
	{
		return maxMoney;
	}
	private static final BigDecimal MINMONEY = new BigDecimal("-10000000000000");
	private BigDecimal minMoney = MINMONEY;

	private BigDecimal _getMinMoney()
	{
		BigDecimal min = config.getBigDecimal("min-money", MINMONEY);
		if (min.signum() > 0)
		{
			min = min.negate();
		}
		return min;
	}

	@Override
	public BigDecimal getMinMoney()
	{
		return minMoney;
	}
	private boolean economyLog = false;

	@Override
	public boolean isEcoLogEnabled()
	{
		return economyLog;
	}

	public boolean _isEcoLogEnabled()
	{
		return config.getBoolean("economy-log-enabled", false);
	}
	// #easteregg
	private boolean economyLogUpdate = false;

	@Override
	public boolean isEcoLogUpdateEnabled()
	{
		return economyLogUpdate;
	}

	public boolean _isEcoLogUpdateEnabled()
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
	private boolean essentialsChatActive = false;

	private boolean _addPrefixSuffix()
	{
		return config.getBoolean("add-prefix-suffix", false);
	}

	private boolean _isPrefixSuffixConfigured()
	{
		return config.hasProperty("add-prefix-suffix");
	}

	@Override
	public void setEssentialsChatActive(boolean essentialsChatActive)
	{
		this.essentialsChatActive = essentialsChatActive;
	}

	@Override
	public boolean addPrefixSuffix()
	{
		return prefixsuffixconfigured ? addprefixsuffix : essentialsChatActive;
	}
	// #easteregg
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
	// #easteregg
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
	private boolean cancelAfkOnInteract;

	@Override
	public boolean cancelAfkOnInteract()
	{
		return cancelAfkOnInteract;
	}

	private boolean _cancelAfkOnInteract()
	{
		return config.getBoolean("cancel-afk-on-interact", true);
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
	public boolean allowUnsafeEnchantments()
	{
		return config.getBoolean("unsafe-enchantments", false);
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
		return config.getLong("tpa-accept-cancellation", 120);
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
	private long teleportInvulnerabilityTime;

	private long _getTeleportInvulnerability()
	{
		return config.getLong("teleport-invulnerability", 0) * 1000;
	}

	@Override
	public long getTeleportInvulnerability()
	{
		return teleportInvulnerabilityTime;
	}
	private boolean teleportInvulnerability;

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
		double maxSpeed = config.getDouble("max-fly-speed", 0.8);
		return maxSpeed > 1.0 ? 1.0 : Math.abs(maxSpeed);
	}

	@Override
	public double getMaxWalkSpeed()
	{
		double maxSpeed = config.getDouble("max-walk-speed", 0.8);
		return maxSpeed > 1.0 ? 1.0 : Math.abs(maxSpeed);
	}
	private int mailsPerMinute;

	private int _getMailsPerMinute()
	{
		return config.getInt("mails-per-minute", 1000);
	}

	@Override
	public int getMailsPerMinute()
	{
		return mailsPerMinute;
	}
	// #easteregg
	private long economyLagWarning;

	private long _getEconomyLagWarning()
	{
		// Default to 25ms
		final long value = (long)(config.getDouble("economy-lag-warning", 25.0) * 1000000);
		return value;
	}

	@Override
	public long getEconomyLagWarning()
	{
		return economyLagWarning;
	}
	
	// #easteregg
	private long permissionsLagWarning;

	private long _getPermissionsLagWarning()
	{
		// Default to 25ms
		final long value = (long)(config.getDouble("permissions-lag-warning", 25.0) * 1000000);
		return value;
	}

	@Override
	public long getPermissionsLagWarning()
	{
		return permissionsLagWarning;
	}

	@Override
	public long getMaxTempban()
	{
		return config.getLong("max-tempban-time", -1);
	}

	@Override
	public int getMaxNickLength()
	{
		return config.getInt("max-nick-length", 30);
	}
	private boolean allowSilentJoin;

	public boolean _allowSilentJoinQuit()
	{
		return config.getBoolean("allow-silent-join-quit", false);
	}

	@Override
	public boolean allowSilentJoinQuit()
	{
		return allowSilentJoin;
	}
	private String customJoinMessage;
	private boolean isCustomJoinMessage;

	public String _getCustomJoinMessage()
	{
		return FormatUtil.replaceFormat(config.getString("custom-join-message", "none"));
	}

	@Override
	public String getCustomJoinMessage()
	{
		return customJoinMessage;
	}

	@Override
	public boolean isCustomJoinMessage()
	{
		return isCustomJoinMessage;
	}
	private String customQuitMessage;
	private boolean isCustomQuitMessage;

	public String _getCustomQuitMessage()
	{
		return FormatUtil.replaceFormat(config.getString("custom-quit-message", "none"));
	}

	@Override
	public String getCustomQuitMessage()
	{
		return customQuitMessage;
	}

	@Override
	public boolean isCustomQuitMessage()
	{
		return isCustomQuitMessage;
	}

	// #easteregg
	@Override
	public int getMaxUserCacheCount()
	{
		long count = Runtime.getRuntime().maxMemory() / 1024 / 96;
		return config.getInt("max-user-cache-count", (int)count);
	}
}
