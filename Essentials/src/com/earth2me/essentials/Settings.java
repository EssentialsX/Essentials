package com.earth2me.essentials;

import com.earth2me.essentials.api.IItemDb;
import com.earth2me.essentials.commands.IEssentialsCommand;
import com.earth2me.essentials.signs.EssentialsSign;
import com.earth2me.essentials.signs.Signs;
import com.earth2me.essentials.textreader.IText;
import com.earth2me.essentials.textreader.SimpleTextInput;
import com.earth2me.essentials.utils.EnumUtil;
import com.earth2me.essentials.utils.FormatUtil;
import com.earth2me.essentials.utils.LocationUtil;
import com.earth2me.essentials.utils.NumberUtil;
import net.ess3.api.IEssentials;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static com.earth2me.essentials.I18n.tl;

public class Settings implements net.ess3.api.ISettings {
    private static final Logger logger = Logger.getLogger("Essentials");
    private static final BigDecimal MAXMONEY = new BigDecimal("10000000000000");
    private static final BigDecimal MINMONEY = new BigDecimal("-10000000000000");
    private final transient EssentialsConf config;
    private final transient IEssentials ess;
    private final Map<String, String> chatFormats = Collections.synchronizedMap(new HashMap<>());
    private int chatRadius = 0;
    // #easteregg
    private char chatShout = '!';
    // #easteregg
    private char chatQuestion = '?';
    private boolean teleportSafety;
    private boolean forceDisableTeleportSafety;
    private Set<String> disabledCommands = new HashSet<>();
    private ConfigurationSection commandCosts;
    private Set<String> socialSpyCommands = new HashSet<>();
    private Set<String> muteCommands = new HashSet<>();
    private String nicknamePrefix = "~";
    private String operatorColor = null;
    private List<Material> itemSpawnBl = new ArrayList<>();
    private List<EssentialsSign> enabledSigns = new ArrayList<>();
    private boolean signsEnabled = false;
    private boolean warnOnBuildDisallow;
    private boolean debug = false;
    private boolean configDebug = false;
    // #easteregg
    private boolean economyDisabled = false;
    private BigDecimal maxMoney = MAXMONEY;
    private BigDecimal minMoney = MINMONEY;
    private boolean economyLog = false;
    // #easteregg
    private boolean economyLogUpdate = false;
    private boolean changeDisplayName = true;
    private boolean changePlayerListName = false;
    private boolean prefixsuffixconfigured = false;
    private boolean addprefixsuffix = false;
    private boolean essentialsChatActive = false;
    // #easteregg
    private boolean disablePrefix = false;
    // #easteregg
    private boolean disableSuffix = false;
    private boolean getFreezeAfkPlayers;
    private boolean cancelAfkOnMove;
    private boolean cancelAfkOnInteract;
    private boolean sleepIgnoresAfkPlayers;
    private String afkListName;
    private boolean isAfkListName;
    private boolean broadcastAfkMessage;
    private KeepInvPolicy vanishingItemPolicy;
    private KeepInvPolicy bindingItemPolicy;
    private Set<String> noGodWorlds = new HashSet<>();
    private boolean registerBackInListener;
    private boolean disableItemPickupWhileAfk;
    private long teleportInvulnerabilityTime;
    private boolean teleportInvulnerability;
    private long loginAttackDelay;
    private int signUsePerSecond;
    private int mailsPerMinute;
    // #easteregg
    private long economyLagWarning;
    // #easteregg
    private long permissionsLagWarning;
    private boolean allowSilentJoin;
    private String customJoinMessage;
    private boolean isCustomJoinMessage;
    private String customQuitMessage;
    private boolean isCustomQuitMessage;
    private List<String> spawnOnJoinGroups;
    private Map<Pattern, Long> commandCooldowns;
    private boolean npcsInBalanceRanking = false;
    private NumberFormat currencyFormat;
    private List<EssentialsSign> unprotectedSigns = Collections.emptyList();
    private List<String> defaultEnabledConfirmCommands;
    private boolean teleportBackWhenFreedFromJail;
    private boolean isCompassTowardsHomePerm;
    private boolean isAllowWorldInBroadcastworld;
    private String itemDbType; // #EasterEgg - admins can manually switch items provider if they want
    private boolean forceEnableRecipe; // https://github.com/EssentialsX/Essentials/issues/1397
    private boolean allowOldIdSigns;
    private boolean isWaterSafe;
    private boolean isSafeUsermap;
    private boolean logCommandBlockCommands;
    private Set<Predicate<String>> nickBlacklist;
    private double maxProjectileSpeed;
    private boolean removeEffectsOnHeal;

    public Settings(final IEssentials ess) {
        this.ess = ess;
        config = new EssentialsConf(new File(ess.getDataFolder(), "config.yml"));
        config.setTemplateName("/config.yml");
        reloadConfig();
    }

    @Override
    public boolean getRespawnAtHome() {
        return config.getBoolean("respawn-at-home", false);
    }

    @Override
    public boolean isRespawnAtAnchor() {
        return config.getBoolean("respawn-at-anchor", false);
    }

    @Override
    public boolean getUpdateBedAtDaytime() {
        return config.getBoolean("update-bed-at-daytime", true);
    }

    @Override
    public Set<String> getMultipleHomes() {
        final ConfigurationSection section = config.getConfigurationSection("sethome-multiple");
        return section == null ? null : section.getKeys(false);
    }

    @Override
    public int getHomeLimit(final User user) {
        int limit = 1;
        if (user.isAuthorized("essentials.sethome.multiple")) {
            limit = getHomeLimit("default");
        }

        final Set<String> homeList = getMultipleHomes();
        if (homeList != null) {
            for (final String set : homeList) {
                if (user.isAuthorized("essentials.sethome.multiple." + set) && (limit < getHomeLimit(set))) {
                    limit = getHomeLimit(set);
                }
            }
        }
        return limit;
    }

    @Override
    public int getHomeLimit(final String set) {
        return config.getInt("sethome-multiple." + set, config.getInt("sethome-multiple.default", 3));
    }

    private int _getChatRadius() {
        return config.getInt("chat.radius", config.getInt("chat-radius", 0));
    }

    @Override
    public int getChatRadius() {
        return chatRadius;
    }

    @Override
    public int getNearRadius() {
        return config.getInt("near-radius", 200);
    }

    private char _getChatShout() {
        return config.getString("chat.shout", "!").charAt(0);
    }

    @Override
    public char getChatShout() {
        return chatShout;
    }

    private char _getChatQuestion() {
        return config.getString("chat.question", "?").charAt(0);
    }

    @Override
    public char getChatQuestion() {
        return chatQuestion;
    }

    public boolean _isTeleportSafetyEnabled() {
        return config.getBoolean("teleport-safety", true);
    }

    @Override
    public boolean isTeleportSafetyEnabled() {
        return teleportSafety;
    }

    private boolean _isForceDisableTeleportSafety() {
        return config.getBoolean("force-disable-teleport-safety", false);
    }

    @Override
    public boolean isForceDisableTeleportSafety() {
        return forceDisableTeleportSafety;
    }

    @Override
    public boolean isAlwaysTeleportSafety() {
        return config.getBoolean("force-safe-teleport-location", false);
    }

    @Override
    public boolean isTeleportPassengerDismount() {
        return config.getBoolean("teleport-passenger-dismount", true);
    }

    @Override
    public boolean isForcePassengerTeleport() {
        return config.getBoolean("force-passenger-teleportation", false);
    }

    @Override
    public double getTeleportDelay() {
        return config.getDouble("teleport-delay", 0);
    }

    @Override
    public int getOversizedStackSize() {
        return config.getInt("oversized-stacksize", 64);
    }

    @Override
    public int getDefaultStackSize() {
        return config.getInt("default-stack-size", -1);
    }

    @Override
    public BigDecimal getStartingBalance() {
        return config.getBigDecimal("starting-balance", BigDecimal.ZERO);
    }

    @Override
    public boolean isCommandDisabled(final IEssentialsCommand cmd) {
        return isCommandDisabled(cmd.getName());
    }

    @Override
    public boolean isCommandDisabled(final String label) {
        return disabledCommands.contains(label);
    }

    private Set<String> getDisabledCommands() {
        final Set<String> disCommands = new HashSet<>();
        for (final String c : config.getStringList("disabled-commands")) {
            disCommands.add(c.toLowerCase(Locale.ENGLISH));
        }
        for (final String c : config.getKeys(false)) {
            if (c.startsWith("disable-")) {
                disCommands.add(c.substring(8).toLowerCase(Locale.ENGLISH));
            }
        }
        return disCommands;
    }

    @Override
    public boolean isPlayerCommand(final String label) {
        for (final String c : config.getStringList("player-commands")) {
            if (!c.equalsIgnoreCase(label)) {
                continue;
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean isCommandOverridden(final String name) {
        for (final String c : config.getStringList("overridden-commands")) {
            if (!c.equalsIgnoreCase(name)) {
                continue;
            }
            return true;
        }
        return config.getBoolean("override-" + name.toLowerCase(Locale.ENGLISH), false);
    }

    @Override
    public BigDecimal getCommandCost(final IEssentialsCommand cmd) {
        return getCommandCost(cmd.getName());
    }

    private ConfigurationSection _getCommandCosts() {
        if (config.isConfigurationSection("command-costs")) {
            final ConfigurationSection section = config.getConfigurationSection("command-costs");
            final ConfigurationSection newSection = new MemoryConfiguration();
            for (final String command : section.getKeys(false)) {
                if (command.charAt(0) == '/') {
                    ess.getLogger().warning("Invalid command cost. '" + command + "' should not start with '/'.");
                }
                if (section.isDouble(command)) {
                    newSection.set(command.toLowerCase(Locale.ENGLISH), section.getDouble(command));
                } else if (section.isInt(command)) {
                    newSection.set(command.toLowerCase(Locale.ENGLISH), (double) section.getInt(command));
                } else if (section.isString(command)) {
                    final String costString = section.getString(command);
                    try {
                        final double cost = Double.parseDouble(costString.trim().replace(getCurrencySymbol(), "").replaceAll("\\W", ""));
                        newSection.set(command.toLowerCase(Locale.ENGLISH), cost);
                    } catch (final NumberFormatException ex) {
                        ess.getLogger().warning("Invalid command cost for: " + command + " (" + costString + ")");
                    }

                } else {
                    ess.getLogger().warning("Invalid command cost for: " + command);
                }
            }
            return newSection;
        }
        return null;
    }

    @Override
    public BigDecimal getCommandCost(String name) {
        name = name.replace('.', '_').replace('/', '_');
        if (commandCosts != null) {
            return EssentialsConf.toBigDecimal(commandCosts.getString(name), BigDecimal.ZERO);
        }
        return BigDecimal.ZERO;
    }

    private Set<String> _getSocialSpyCommands() {
        final Set<String> socialspyCommands = new HashSet<>();

        if (config.isList("socialspy-commands")) {
            for (final String c : config.getStringList("socialspy-commands")) {
                socialspyCommands.add(c.toLowerCase(Locale.ENGLISH));
            }
        } else {
            socialspyCommands.addAll(Arrays.asList("msg", "r", "mail", "m", "whisper", "emsg", "t", "tell", "er", "reply", "ereply", "email", "action", "describe", "eme", "eaction", "edescribe", "etell", "ewhisper", "pm"));
        }

        return socialspyCommands;
    }

    @Override
    public Set<String> getSocialSpyCommands() {
        return socialSpyCommands;
    }

    @Override
    public boolean getSocialSpyListenMutedPlayers() {
        return config.getBoolean("socialspy-listen-muted-players", true);
    }

    private Set<String> _getMuteCommands() {
        final Set<String> muteCommands = new HashSet<>();
        if (config.isList("mute-commands")) {
            for (final String s : config.getStringList("mute-commands")) {
                muteCommands.add(s.toLowerCase(Locale.ENGLISH));
            }
        }

        return muteCommands;
    }

    @Override
    public Set<String> getMuteCommands() {
        return muteCommands;
    }

    private String _getNicknamePrefix() {
        return config.getString("nickname-prefix", "~");
    }

    @Override
    public String getNicknamePrefix() {
        return nicknamePrefix;
    }

    @Override
    public double getTeleportCooldown() {
        return config.getDouble("teleport-cooldown", 0);
    }

    @Override
    public double getHealCooldown() {
        return config.getDouble("heal-cooldown", 0);
    }

    @Override
    public ConfigurationSection getKits() {
        return ess.getKits().getKits();
    }

    @Override
    public Map<String, Object> getKit(final String name) {
        return ess.getKits().getKit(name);
    }

    @Override
    public void addKit(final String name, final List<String> lines, final long delay) {
        ess.getKits().addKit(name, lines, delay);
    }

    @Override
    public ConfigurationSection getKitSection() {
        return config.getConfigurationSection("kits");
    }

    @Override
    public boolean isSkippingUsedOneTimeKitsFromKitList() {
        return config.getBoolean("skip-used-one-time-kits-from-kit-list", false);
    }

    @Override
    public String getOperatorColor() {
        return operatorColor;
    }

    private String _getOperatorColor() {
        final String colorName = config.getString("ops-name-color", null);

        if (colorName == null) {
            return ChatColor.RED.toString();
        } else if (colorName.equalsIgnoreCase("none") || colorName.isEmpty()) {
            return null;
        }

        try {
            return FormatUtil.parseHexColor(colorName);
        } catch (final NumberFormatException ignored) {
        }

        try {
            return ChatColor.valueOf(colorName.toUpperCase(Locale.ENGLISH)).toString();
        } catch (final IllegalArgumentException ignored) {
        }

        final ChatColor lastResort = ChatColor.getByChar(colorName);
        if (lastResort != null) {
            return lastResort.toString();
        }
        return null;
    }

    @Override
    public int getSpawnMobLimit() {
        return config.getInt("spawnmob-limit", 10);
    }

    @Override
    public boolean showNonEssCommandsInHelp() {
        return config.getBoolean("non-ess-in-help", true);
    }

    @Override
    public boolean hidePermissionlessHelp() {
        return config.getBoolean("hide-permissionless-help", true);
    }

    @Override
    public int getProtectCreeperMaxHeight() {
        return config.getInt("protect.creeper.max-height", -1);
    }

    @Override
    public boolean areSignsDisabled() {
        return !signsEnabled;
    }

    @Override
    public long getBackupInterval() {
        return config.getInt("backup.interval", 1440); // 1440 = 24 * 60
    }

    @Override
    public String getBackupCommand() {
        return config.getString("backup.command", null);
    }

    @Override
    public boolean isAlwaysRunBackup() {
        return config.getBoolean("backup.always-run", false);
    }

    @Override
    public String getChatFormat(final String group) {
        String mFormat = chatFormats.get(group);
        if (mFormat == null) {
            mFormat = config.getString("chat.group-formats." + (group == null ? "Default" : group), config.getString("chat.format", "&7[{GROUP}]&r {DISPLAYNAME}&7:&r {MESSAGE}"));
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
            mFormat = mFormat.replace("{PREFIX}", "{6}");
            mFormat = mFormat.replace("{SUFFIX}", "{7}");
            mFormat = mFormat.replace("{USERNAME}", "{8}");
            mFormat = "§r".concat(mFormat);
            chatFormats.put(group, mFormat);
        }
        if (isDebug()) {
            ess.getLogger().info(String.format("Found format '%s' for group '%s'", mFormat, group));
        }
        return mFormat;
    }

    @Override
    public boolean getAnnounceNewPlayers() {
        return !config.getString("newbies.announce-format", "-").isEmpty();
    }

    @Override
    public IText getAnnounceNewPlayerFormat() {
        return new SimpleTextInput(FormatUtil.replaceFormat(config.getString("newbies.announce-format", "&dWelcome {DISPLAYNAME} to the server!")));
    }

    @Override
    public String getNewPlayerKit() {
        return config.getString("newbies.kit", "");
    }

    @Override
    public String getNewbieSpawn() {
        return config.getString("newbies.spawnpoint", "default");
    }

    @Override
    public boolean getPerWarpPermission() {
        return config.getBoolean("per-warp-permission", false);
    }

    @Override
    public Map<String, Object> getListGroupConfig() {
        if (config.isConfigurationSection("list")) {
            final Map<String, Object> values = config.getConfigurationSection("list").getValues(false);
            if (!values.isEmpty()) {
                return values;
            }
        }
        final Map<String, Object> defaultMap = new HashMap<>();
        if (config.getBoolean("sort-list-by-groups", false)) {
            defaultMap.put("ListByGroup", "ListByGroup");
        } else {
            defaultMap.put("Players", "*");
        }
        return defaultMap;
    }

    @Override
    public void reloadConfig() {
        config.load();
        noGodWorlds = new HashSet<>(config.getStringList("no-god-in-worlds"));
        enabledSigns = _getEnabledSigns();
        teleportSafety = _isTeleportSafetyEnabled();
        forceDisableTeleportSafety = _isForceDisableTeleportSafety();
        teleportInvulnerabilityTime = _getTeleportInvulnerability();
        teleportInvulnerability = _isTeleportInvulnerability();
        disableItemPickupWhileAfk = _getDisableItemPickupWhileAfk();
        registerBackInListener = _registerBackInListener();
        cancelAfkOnInteract = _cancelAfkOnInteract();
        cancelAfkOnMove = _cancelAfkOnMove();
        getFreezeAfkPlayers = _getFreezeAfkPlayers();
        sleepIgnoresAfkPlayers = _sleepIgnoresAfkPlayers();
        afkListName = _getAfkListName();
        isAfkListName = !afkListName.equalsIgnoreCase("none");
        broadcastAfkMessage = _broadcastAfkMessage();
        itemSpawnBl = _getItemSpawnBlacklist();
        loginAttackDelay = _getLoginAttackDelay();
        signUsePerSecond = _getSignUsePerSecond();
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
        muteCommands = _getMuteCommands();
        spawnOnJoinGroups = _getSpawnOnJoinGroups();
        commandCooldowns = _getCommandCooldowns();
        npcsInBalanceRanking = _isNpcsInBalanceRanking();
        currencyFormat = _getCurrencyFormat();
        unprotectedSigns = _getUnprotectedSign();
        defaultEnabledConfirmCommands = _getDefaultEnabledConfirmCommands();
        teleportBackWhenFreedFromJail = _isTeleportBackWhenFreedFromJail();
        isCompassTowardsHomePerm = _isCompassTowardsHomePerm();
        isAllowWorldInBroadcastworld = _isAllowWorldInBroadcastworld();
        itemDbType = _getItemDbType();
        allowOldIdSigns = _allowOldIdSigns();
        isWaterSafe = _isWaterSafe();
        isSafeUsermap = _isSafeUsermap();
        logCommandBlockCommands = _logCommandBlockCommands();
        nickBlacklist = _getNickBlacklist();
        maxProjectileSpeed = _getMaxProjectileSpeed();
        removeEffectsOnHeal = _isRemovingEffectsOnHeal();
        vanishingItemPolicy = _getVanishingItemsPolicy();
        bindingItemPolicy = _getBindingItemsPolicy();
    }

    void _lateLoadItemSpawnBlacklist() {
        itemSpawnBl = _getItemSpawnBlacklist();
    }

    @Override
    public List<Material> itemSpawnBlacklist() {
        return itemSpawnBl;
    }

    private List<Material> _getItemSpawnBlacklist() {
        final List<Material> epItemSpwn = new ArrayList<>();
        final IItemDb itemDb = ess.getItemDb();
        if (itemDb == null || !itemDb.isReady()) {
            logger.log(Level.FINE, "Skipping item spawn blacklist read; item DB not yet loaded.");
            return epItemSpwn;
        }
        for (String itemName : config.getString("item-spawn-blacklist", "").split(",")) {
            itemName = itemName.trim();
            if (itemName.isEmpty()) {
                continue;
            }
            try {
                final ItemStack iStack = itemDb.get(itemName);
                epItemSpwn.add(iStack.getType());
            } catch (final Exception ex) {
                logger.log(Level.SEVERE, tl("unknownItemInList", itemName, "item-spawn-blacklist"), ex);
            }
        }
        return epItemSpwn;
    }

    @Override
    public List<EssentialsSign> enabledSigns() {
        return enabledSigns;
    }

    private List<EssentialsSign> _getEnabledSigns() {
        this.signsEnabled = false; // Ensure boolean resets on reload.

        final List<EssentialsSign> newSigns = new ArrayList<>();

        for (String signName : config.getStringList("enabledSigns")) {
            signName = signName.trim().toUpperCase(Locale.ENGLISH);
            if (signName.isEmpty()) {
                continue;
            }
            if (signName.equals("COLOR") || signName.equals("COLOUR")) {
                signsEnabled = true;
                continue;
            }
            try {
                newSigns.add(Signs.valueOf(signName).getSign());
            } catch (final Exception ex) {
                logger.log(Level.SEVERE, tl("unknownItemInList", signName, "enabledSigns"));
                continue;
            }
            signsEnabled = true;
        }
        return newSigns;
    }

    private boolean _warnOnBuildDisallow() {
        return config.getBoolean("protect.disable.warn-on-build-disallow", false);
    }

    @Override
    public boolean warnOnBuildDisallow() {
        return warnOnBuildDisallow;
    }

    private boolean _isDebug() {
        return config.getBoolean("debug", false);
    }

    @Override
    public boolean isDebug() {
        return debug || configDebug;
    }

    @Override
    public void setDebug(final boolean debug) {
        this.debug = debug;
    }

    @Override
    public boolean warnOnSmite() {
        return config.getBoolean("warn-on-smite", true);
    }

    @Override
    public boolean permissionBasedItemSpawn() {
        return config.getBoolean("permission-based-item-spawn", false);
    }

    @Override
    public String getLocale() {
        return config.getString("locale", "");
    }

    //This method should always only return one character due to the implementation of the calling methods
    //If you need to use a string currency, for example "coins", use the translation key 'currency'.
    @Override
    public String getCurrencySymbol() {
        return config.getString("currency-symbol", "$").concat("$").substring(0, 1).replaceAll("[0-9]", "$");
    }

    @Override
    public boolean isCurrencySymbolSuffixed() {
        return config.getBoolean("currency-symbol-suffix", false);
    }

    // #easteregg
    @Override
    @Deprecated
    public boolean isTradeInStacks(final int id) {
        return config.getBoolean("trade-in-stacks-" + id, false);
    }

    // #easteregg
    @Override
    public boolean isTradeInStacks(final Material type) {
        return config.getBoolean("trade-in-stacks." + type.toString().toLowerCase().replace("_", ""), false);
    }

    public boolean _isEcoDisabled() {
        return config.getBoolean("disable-eco", false);
    }

    @Override
    public boolean isEcoDisabled() {
        return economyDisabled;
    }

    @Override
    public boolean getProtectPreventSpawn(final String creatureName) {
        return config.getBoolean("protect.prevent.spawn." + creatureName, false);
    }

    @Override
    public List<Material> getProtectList(final String configName) {
        final List<Material> list = new ArrayList<>();
        for (String itemName : config.getString(configName, "").split(",")) {
            itemName = itemName.trim();
            if (itemName.isEmpty()) {
                continue;
            }

            Material mat = EnumUtil.getMaterial(itemName.toUpperCase());

            if (mat == null) {
                try {
                    final ItemStack itemStack = ess.getItemDb().get(itemName);
                    mat = itemStack.getType();
                } catch (final Exception ignored) {
                }
            }

            if (mat == null) {
                logger.log(Level.SEVERE, tl("unknownItemInList", itemName, configName));
            } else {
                list.add(mat);
            }
        }
        return list;
    }

    @Override
    public String getProtectString(final String configName) {
        return config.getString(configName, null);
    }

    @Override
    public boolean getProtectBoolean(final String configName, final boolean def) {
        return config.getBoolean(configName, def);
    }

    private BigDecimal _getMaxMoney() {
        return config.getBigDecimal("max-money", MAXMONEY);
    }

    @Override
    public BigDecimal getMaxMoney() {
        return maxMoney;
    }

    private BigDecimal _getMinMoney() {
        BigDecimal min = config.getBigDecimal("min-money", MINMONEY);
        if (min.signum() > 0) {
            min = min.negate();
        }
        return min;
    }

    @Override
    public BigDecimal getMinMoney() {
        return minMoney;
    }

    @Override
    public boolean isEcoLogEnabled() {
        return economyLog;
    }

    public boolean _isEcoLogEnabled() {
        return config.getBoolean("economy-log-enabled", false);
    }

    @Override
    public boolean isEcoLogUpdateEnabled() {
        return economyLogUpdate;
    }

    @Override
    public boolean realNamesOnList() {
        return config.getBoolean("real-names-on-list", false);
    }

    public boolean _isEcoLogUpdateEnabled() {
        return config.getBoolean("economy-log-update-enabled", false);
    }

    @Override
    public boolean removeGodOnDisconnect() {
        return config.getBoolean("remove-god-on-disconnect", false);
    }

    private boolean _changeDisplayName() {
        return config.getBoolean("change-displayname", true);
    }

    @Override
    public boolean changeDisplayName() {
        return changeDisplayName;
    }

    private boolean _changePlayerListName() {
        return config.getBoolean("change-playerlist", false);
    }

    @Override
    public boolean changePlayerListName() {
        return changePlayerListName;
    }

    @Override
    public boolean useBukkitPermissions() {
        return config.getBoolean("use-bukkit-permissions", false);
    }

    private boolean _addPrefixSuffix() {
        return config.getBoolean("add-prefix-suffix", false);
    }

    private boolean _isPrefixSuffixConfigured() {
        return config.hasProperty("add-prefix-suffix");
    }

    @Override
    public void setEssentialsChatActive(final boolean essentialsChatActive) {
        this.essentialsChatActive = essentialsChatActive;
    }

    @Override
    public boolean addPrefixSuffix() {
        return prefixsuffixconfigured ? addprefixsuffix : essentialsChatActive;
    }

    private boolean _disablePrefix() {
        return config.getBoolean("disablePrefix", false);
    }

    @Override
    public boolean disablePrefix() {
        return disablePrefix;
    }

    private boolean _disableSuffix() {
        return config.getBoolean("disableSuffix", false);
    }

    @Override
    public boolean disableSuffix() {
        return disableSuffix;
    }

    @Override
    public long getAutoAfk() {
        return config.getLong("auto-afk", 300);
    }

    @Override
    public long getAutoAfkKick() {
        return config.getLong("auto-afk-kick", -1);
    }

    @Override
    public boolean getFreezeAfkPlayers() {
        return getFreezeAfkPlayers;
    }

    private boolean _getFreezeAfkPlayers() {
        return config.getBoolean("freeze-afk-players", false);
    }

    @Override
    public boolean cancelAfkOnMove() {
        return cancelAfkOnMove;
    }

    private boolean _cancelAfkOnMove() {
        return config.getBoolean("cancel-afk-on-move", true);
    }

    @Override
    public boolean cancelAfkOnInteract() {
        return cancelAfkOnInteract;
    }

    private boolean _cancelAfkOnInteract() {
        return config.getBoolean("cancel-afk-on-interact", true);
    }

    @Override
    public boolean sleepIgnoresAfkPlayers() {
        return sleepIgnoresAfkPlayers;
    }

    private boolean _sleepIgnoresAfkPlayers() {
        return config.getBoolean("sleep-ignores-afk-players", true);
    }

    public String _getAfkListName() {
        return FormatUtil.replaceFormat(config.getString("afk-list-name", "none"));
    }

    @Override
    public boolean isAfkListName() {
        return isAfkListName;
    }

    @Override
    public String getAfkListName() {
        return afkListName;
    }

    @Override
    public boolean broadcastAfkMessage() {
        return broadcastAfkMessage;
    }

    private boolean _broadcastAfkMessage() {
        return config.getBoolean("broadcast-afk-message", true);
    }

    @Override
    public boolean areDeathMessagesEnabled() {
        return config.getBoolean("death-messages", true);
    }

    public KeepInvPolicy _getVanishingItemsPolicy() {
        final String value = config.getString("vanishing-items-policy", "keep").toLowerCase(Locale.ENGLISH);
        try {
            return KeepInvPolicy.valueOf(value.toUpperCase(Locale.ENGLISH));
        } catch (final IllegalArgumentException e) {
            return KeepInvPolicy.KEEP;
        }
    }

    @Override
    public KeepInvPolicy getVanishingItemsPolicy() {
        return vanishingItemPolicy;
    }

    public KeepInvPolicy _getBindingItemsPolicy() {
        final String value = config.getString("binding-items-policy", "keep").toLowerCase(Locale.ENGLISH);
        try {
            return KeepInvPolicy.valueOf(value.toUpperCase(Locale.ENGLISH));
        } catch (final IllegalArgumentException e) {
            return KeepInvPolicy.KEEP;
        }
    }

    @Override
    public KeepInvPolicy getBindingItemsPolicy() {
        return bindingItemPolicy;
    }

    @Override
    public Set<String> getNoGodWorlds() {
        return noGodWorlds;
    }

    @Override
    public boolean getRepairEnchanted() {
        return config.getBoolean("repair-enchanted", true);
    }

    @Override
    public boolean allowUnsafeEnchantments() {
        return config.getBoolean("unsafe-enchantments", false);
    }

    @Override
    public boolean isWorldTeleportPermissions() {
        return config.getBoolean("world-teleport-permissions", false);
    }

    @Override
    public boolean isWorldHomePermissions() {
        return config.getBoolean("world-home-permissions", false);
    }

    @Override
    public boolean registerBackInListener() {
        return registerBackInListener;
    }

    private boolean _registerBackInListener() {
        return config.getBoolean("register-back-in-listener", false);
    }

    @Override
    public boolean getDisableItemPickupWhileAfk() {
        return disableItemPickupWhileAfk;
    }

    private boolean _getDisableItemPickupWhileAfk() {
        return config.getBoolean("disable-item-pickup-while-afk", false);
    }

    private EventPriority getPriority(final String priority) {
        if ("none".equals(priority)) {
            return null;
        }
        if ("lowest".equals(priority)) {
            return EventPriority.LOWEST;
        }
        if ("low".equals(priority)) {
            return EventPriority.LOW;
        }
        if ("normal".equals(priority)) {
            return EventPriority.NORMAL;
        }
        if ("high".equals(priority)) {
            return EventPriority.HIGH;
        }
        if ("highest".equals(priority)) {
            return EventPriority.HIGHEST;
        }
        return EventPriority.NORMAL;
    }

    @Override
    public EventPriority getRespawnPriority() {
        final String priority = config.getString("respawn-listener-priority", "normal").toLowerCase(Locale.ENGLISH);
        return getPriority(priority);
    }

    @Override
    public EventPriority getSpawnJoinPriority() {
        final String priority = config.getString("spawn-join-listener-priority", "normal").toLowerCase(Locale.ENGLISH);
        return getPriority(priority);
    }

    @Override
    public long getTpaAcceptCancellation() {
        return config.getLong("tpa-accept-cancellation", 120);
    }

    private long _getTeleportInvulnerability() {
        return config.getLong("teleport-invulnerability", 0) * 1000;
    }

    @Override
    public long getTeleportInvulnerability() {
        return teleportInvulnerabilityTime;
    }

    private boolean _isTeleportInvulnerability() {
        return config.getLong("teleport-invulnerability", 0) > 0;
    }

    @Override
    public boolean isTeleportInvulnerability() {
        return teleportInvulnerability;
    }

    private long _getLoginAttackDelay() {
        return config.getLong("login-attack-delay", 0) * 1000;
    }

    @Override
    public long getLoginAttackDelay() {
        return loginAttackDelay;
    }

    private int _getSignUsePerSecond() {
        final int perSec = config.getInt("sign-use-per-second", 4);
        return perSec > 0 ? perSec : 1;
    }

    @Override
    public int getSignUsePerSecond() {
        return signUsePerSecond;
    }

    @Override
    public double getMaxFlySpeed() {
        final double maxSpeed = config.getDouble("max-fly-speed", 0.8);
        return maxSpeed > 1.0 ? 1.0 : Math.abs(maxSpeed);
    }

    @Override
    public double getMaxWalkSpeed() {
        final double maxSpeed = config.getDouble("max-walk-speed", 0.8);
        return maxSpeed > 1.0 ? 1.0 : Math.abs(maxSpeed);
    }

    private int _getMailsPerMinute() {
        return config.getInt("mails-per-minute", 1000);
    }

    @Override
    public int getMailsPerMinute() {
        return mailsPerMinute;
    }

    private long _getEconomyLagWarning() {
        // Default to 25ms
        return (long) (config.getDouble("economy-lag-warning", 25.0) * 1000000);
    }

    @Override
    public long getEconomyLagWarning() {
        return economyLagWarning;
    }

    private long _getPermissionsLagWarning() {
        // Default to 25ms
        return (long) (config.getDouble("permissions-lag-warning", 25.0) * 1000000);
    }

    @Override
    public long getPermissionsLagWarning() {
        return permissionsLagWarning;
    }

    @Override
    public long getMaxMute() {
        return config.getLong("max-mute-time", -1);
    }

    @Override
    public long getMaxTempban() {
        return config.getLong("max-tempban-time", -1);
    }

    @Override
    public int getMaxNickLength() {
        return config.getInt("max-nick-length", 30);
    }

    @Override
    public boolean ignoreColorsInMaxLength() {
        return config.getBoolean("ignore-colors-in-max-nick-length", false);
    }

    @Override
    public boolean hideDisplayNameInVanish() {
        return config.getBoolean("hide-displayname-in-vanish", false);
    }

    public boolean _allowSilentJoinQuit() {
        return config.getBoolean("allow-silent-join-quit", false);
    }

    @Override
    public boolean allowSilentJoinQuit() {
        return allowSilentJoin;
    }

    public String _getCustomJoinMessage() {
        return FormatUtil.replaceFormat(config.getString("custom-join-message", "none"));
    }

    @Override
    public String getCustomJoinMessage() {
        return customJoinMessage;
    }

    @Override
    public boolean isCustomJoinMessage() {
        return isCustomJoinMessage;
    }

    public String _getCustomQuitMessage() {
        return FormatUtil.replaceFormat(config.getString("custom-quit-message", "none"));
    }

    @Override
    public String getCustomQuitMessage() {
        return customQuitMessage;
    }

    @Override
    public boolean isCustomQuitMessage() {
        return isCustomQuitMessage;
    }

    @Override
    public int getJoinQuitMessagePlayerCount() {
        return config.getInt("hide-join-quit-messages-above", -1);
    }

    @Override
    public boolean hasJoinQuitMessagePlayerCount() {
        return getJoinQuitMessagePlayerCount() >= 0;
    }

    @Override
    public boolean isNotifyNoNewMail() {
        return config.getBoolean("notify-no-new-mail", true);
    }

    @Override
    public boolean isDropItemsIfFull() {
        return config.getBoolean("drop-items-if-full", false);
    }

    // #easteregg
    @Override
    public int getMaxUserCacheCount() {
        final long count = Runtime.getRuntime().maxMemory() / 1024 / 96;
        return config.getInt("max-user-cache-count", (int) count);
    }

    @Override
    public boolean isLastMessageReplyRecipient() {
        return config.getBoolean("last-message-reply-recipient", false);
    }

    @Override
    public boolean isReplyToVanished() {
        return config.getBoolean("last-message-reply-vanished", true);
    }

    @Override
    public BigDecimal getMinimumPayAmount() {
        return new BigDecimal(config.getString("minimum-pay-amount", "0.001"));
    }

    @Override
    public boolean isPayExcludesIgnoreList() {
        return config.getBoolean("pay-excludes-ignore-list", false);
    }

    @Override
    public long getLastMessageReplyRecipientTimeout() {
        return config.getLong("last-message-reply-recipient-timeout", 180);
    }

    @Override
    public boolean isMilkBucketEasterEggEnabled() {
        return config.getBoolean("milk-bucket-easter-egg", true);
    }

    @Override
    public boolean isSendFlyEnableOnJoin() {
        return config.getBoolean("send-fly-enable-on-join", true);
    }

    @Override
    public boolean isWorldTimePermissions() {
        return config.getBoolean("world-time-permissions", false);
    }

    @Override
    public boolean isSpawnOnJoin() {
        return !this.spawnOnJoinGroups.isEmpty();
    }

    public List<String> _getSpawnOnJoinGroups() {
        final List<String> def = Collections.emptyList();
        if (config.isSet("spawn-on-join")) {
            if (config.isList("spawn-on-join")) {
                return new ArrayList<>(config.getStringList("spawn-on-join"));
            } else if (config.isBoolean("spawn-on-join")) { // List of [*] to make all groups go to spawn on join.
                // This also maintains backwards compatibility with initial impl of single boolean value.
                return config.getBoolean("spawn-on-join") ? Collections.singletonList("*") : def;
            }
            // Take whatever the value is, convert to string and add it to a list as a single value.
            final String val = config.get("spawn-on-join").toString();
            return !val.isEmpty() ? Collections.singletonList(val) : def;
        } else {
            return def;
        }
    }

    @Override
    public List<String> getSpawnOnJoinGroups() {
        return this.spawnOnJoinGroups;
    }

    @Override
    public boolean isUserInSpawnOnJoinGroup(final IUser user) {
        for (final String group : this.spawnOnJoinGroups) {
            if (group.equals("*") || user.inGroup(group)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isTeleportToCenterLocation() {
        return config.getBoolean("teleport-to-center", true);
    }

    private Map<Pattern, Long> _getCommandCooldowns() {
        if (!config.isConfigurationSection("command-cooldowns")) {
            return null;
        }
        final ConfigurationSection section = config.getConfigurationSection("command-cooldowns");
        final Map<Pattern, Long> result = new LinkedHashMap<>();
        for (String cmdEntry : section.getKeys(false)) {
            Pattern pattern = null;

            /* ================================
             * >> Regex
             * ================================ */
            if (cmdEntry.startsWith("^")) {
                try {
                    pattern = Pattern.compile(cmdEntry.substring(1));
                } catch (final PatternSyntaxException e) {
                    ess.getLogger().warning("Command cooldown error: " + e.getMessage());
                }
            } else {
                // Escape above Regex
                if (cmdEntry.startsWith("\\^")) {
                    cmdEntry = cmdEntry.substring(1);
                }
                final String cmd = cmdEntry
                    .replaceAll("\\*", ".*"); // Wildcards are accepted as asterisk * as known universally.
                pattern = Pattern.compile(cmd + "( .*)?"); // This matches arguments, if present, to "ignore" them from the feature.
            }

            /* ================================
             * >> Process cooldown value
             * ================================ */
            Object value = section.get(cmdEntry);
            if (value instanceof String) {
                try {
                    value = Double.parseDouble(value.toString());
                } catch (final NumberFormatException ignored) {
                }
            }
            if (!(value instanceof Number)) {
                ess.getLogger().warning("Command cooldown error: '" + value + "' is not a valid cooldown");
                continue;
            }
            final double cooldown = ((Number) value).doubleValue();
            if (cooldown < 1) {
                ess.getLogger().warning("Command cooldown with very short " + cooldown + " cooldown.");
            }

            result.put(pattern, (long) cooldown * 1000); // convert to milliseconds
        }
        return result;
    }

    @Override
    public boolean isCommandCooldownsEnabled() {
        return commandCooldowns != null;
    }

    @Override
    public long getCommandCooldownMs(final String label) {
        final Entry<Pattern, Long> result = getCommandCooldownEntry(label);
        return result != null ? result.getValue() : -1; // return cooldown in milliseconds
    }

    @Override
    public Entry<Pattern, Long> getCommandCooldownEntry(final String label) {
        if (isCommandCooldownsEnabled()) {
            for (final Entry<Pattern, Long> entry : this.commandCooldowns.entrySet()) {
                // Check if label matches current pattern (command-cooldown in config)
                final boolean matches = entry.getKey().matcher(label).matches();
                if (isDebug()) {
                    ess.getLogger().info(String.format("Checking command '%s' against cooldown '%s': %s", label, entry.getKey(), matches));
                }

                if (matches) {
                    return entry;
                }
            }
        }
        return null;
    }

    @Override
    public boolean isCommandCooldownPersistent(final String label) {
        // TODO: enable per command cooldown specification for persistence.
        return config.getBoolean("command-cooldown-persistence", true);
    }

    private boolean _isNpcsInBalanceRanking() {
        return config.getBoolean("npcs-in-balance-ranking", false);
    }

    @Override
    public boolean isNpcsInBalanceRanking() {
        return npcsInBalanceRanking;
    }

    private NumberFormat _getCurrencyFormat() {
        final String currencyFormatString = config.getString("currency-format", "#,##0.00");

        final String symbolLocaleString = config.getString("currency-symbol-format-locale");
        final DecimalFormatSymbols decimalFormatSymbols;
        if (symbolLocaleString != null) {
            decimalFormatSymbols = DecimalFormatSymbols.getInstance(Locale.forLanguageTag(symbolLocaleString));
        } else {
            // Fallback to the JVM's default locale
            decimalFormatSymbols = DecimalFormatSymbols.getInstance(Locale.US);
        }

        final DecimalFormat currencyFormat = new DecimalFormat(currencyFormatString, decimalFormatSymbols);
        currencyFormat.setRoundingMode(RoundingMode.FLOOR);

        // Updates NumberUtil#PRETTY_FORMAT field so that all of Essentials can follow a single format.
        NumberUtil.internalSetPrettyFormat(currencyFormat);
        return currencyFormat;
    }

    @Override
    public NumberFormat getCurrencyFormat() {
        return this.currencyFormat;
    }

    @Override
    public List<EssentialsSign> getUnprotectedSignNames() {
        return this.unprotectedSigns;
    }

    private List<EssentialsSign> _getUnprotectedSign() {
        final List<EssentialsSign> newSigns = new ArrayList<>();

        for (String signName : config.getStringList("unprotected-sign-names")) {
            signName = signName.trim().toUpperCase(Locale.ENGLISH);
            if (signName.isEmpty()) {
                continue;
            }
            try {
                newSigns.add(Signs.valueOf(signName).getSign());
            } catch (final Exception ex) {
                logger.log(Level.SEVERE, tl("unknownItemInList", signName, "unprotected-sign-names"));
            }
        }
        return newSigns;
    }

    @Override
    public boolean isPastebinCreateKit() {
        return config.getBoolean("pastebin-createkit", false);
    }

    @Override
    public boolean isAllowBulkBuySell() {
        return config.getBoolean("allow-bulk-buy-sell", false);
    }

    @Override
    public boolean isAllowSellNamedItems() {
        return config.getBoolean("allow-selling-named-items", false);
    }

    @Override
    public boolean isAddingPrefixInPlayerlist() {
        return config.getBoolean("add-prefix-in-playerlist", false);
    }

    @Override
    public boolean isAddingSuffixInPlayerlist() {
        return config.getBoolean("add-suffix-in-playerlist", false);
    }

    @Override
    public int getNotifyPlayerOfMailCooldown() {
        return config.getInt("notify-player-of-mail-cooldown", 0);
    }

    @Override
    public int getMotdDelay() {
        return config.getInt("delay-motd", 0);
    }

    @Override
    public boolean isDirectHatAllowed() {
        return config.getBoolean("allow-direct-hat", true);
    }

    @Override
    public boolean isWorldChangeFlyResetEnabled() {
        return config.getBoolean("world-change-fly-reset", true);
    }

    @Override
    public boolean isWorldChangeSpeedResetEnabled() {
        return config.getBoolean("world-change-speed-reset", true);
    }

    private List<String> _getDefaultEnabledConfirmCommands() {
        final List<String> commands = config.getStringList("default-enabled-confirm-commands");
        for (int i = 0; i < commands.size(); i++) {
            commands.set(i, commands.get(i).toLowerCase());
        }
        return commands;
    }

    @Override
    public List<String> getDefaultEnabledConfirmCommands() {
        return defaultEnabledConfirmCommands;
    }

    @Override
    public boolean isConfirmCommandEnabledByDefault(final String commandName) {
        return getDefaultEnabledConfirmCommands().contains(commandName.toLowerCase());
    }

    private boolean _isTeleportBackWhenFreedFromJail() {
        return config.getBoolean("teleport-back-when-freed-from-jail", true);
    }

    @Override
    public boolean isTeleportBackWhenFreedFromJail() {
        return teleportBackWhenFreedFromJail;
    }

    private boolean _isCompassTowardsHomePerm() {
        return config.getBoolean("compass-towards-home-perm", false);
    }

    @Override
    public boolean isCompassTowardsHomePerm() {
        return isCompassTowardsHomePerm;
    }

    private boolean _isAllowWorldInBroadcastworld() {
        return config.getBoolean("allow-world-in-broadcastworld", false);
    }

    @Override
    public boolean isAllowWorldInBroadcastworld() {
        return isAllowWorldInBroadcastworld;
    }

    private String _getItemDbType() {
        return config.getString("item-db-type", "auto");
    }

    @Override
    public String getItemDbType() {
        return itemDbType;
    }

    private boolean _allowOldIdSigns() {
        return config.getBoolean("allow-old-id-signs", false);
    }

    @Override
    public boolean allowOldIdSigns() {
        return allowOldIdSigns;
    }

    private boolean _isWaterSafe() {
        final boolean _isWaterSafe = config.getBoolean("is-water-safe", false);
        LocationUtil.setIsWaterSafe(_isWaterSafe);

        return _isWaterSafe;
    }

    @Override
    public boolean isWaterSafe() {
        return isWaterSafe;
    }

    private boolean _isSafeUsermap() {
        return config.getBoolean("safe-usermap-names", true);
    }

    @Override
    public boolean isSafeUsermap() {
        return isSafeUsermap;
    }

    private boolean _logCommandBlockCommands() {
        return config.getBoolean("log-command-block-commands", true);
    }

    @Override
    public boolean logCommandBlockCommands() {
        return logCommandBlockCommands;
    }

    private Set<Predicate<String>> _getNickBlacklist() {
        final Set<Predicate<String>> blacklist = new HashSet<>();

        config.getStringList("nick-blacklist").forEach(entry -> {
            try {
                blacklist.add(Pattern.compile(entry).asPredicate());
            } catch (final PatternSyntaxException e) {
                logger.warning("Invalid nickname blacklist regex: " + entry);
            }
        });

        return blacklist;
    }

    @Override
    public Set<Predicate<String>> getNickBlacklist() {
        return nickBlacklist;
    }

    private double _getMaxProjectileSpeed() {
        return config.getDouble("max-projectile-speed", 8);
    }

    @Override
    public double getMaxProjectileSpeed() {
        return maxProjectileSpeed;
    }

    private boolean _isRemovingEffectsOnHeal() {
        return config.getBoolean("remove-effects-on-heal", true);
    }

    @Override
    public boolean isRemovingEffectsOnHeal() {
        return removeEffectsOnHeal;
    }

    @Override
    public boolean isSpawnIfNoHome() {
        return config.getBoolean("spawn-if-no-home", true);
    }

    @Override
    public boolean isConfirmHomeOverwrite() {
        return config.getBoolean("confirm-home-overwrite", false);
    }

    @Override
    public boolean infoAfterDeath() {
        return config.getBoolean("send-info-after-death", false);
    }
}
