package com.earth2me.essentials;

import com.earth2me.essentials.utils.NumberUtil;
import com.earth2me.essentials.utils.StringUtil;
import com.google.common.collect.ImmutableMap;
import net.ess3.api.IEssentials;
import net.ess3.api.InvalidWorldException;
import net.ess3.api.MaxMoneyException;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.earth2me.essentials.I18n.tl;

public abstract class UserData extends PlayerExtension implements IConf {
    protected final transient IEssentials ess;
    private final EssentialsUserConf config;
    private BigDecimal money;
    private Map<String, Object> homes;
    private String nickname;
    private Set<Material> unlimited;
    private Map<String, Object> powertools;
    private Location lastLocation;
    private Location logoutLocation;
    private long lastTeleportTimestamp;
    private long lastHealTimestamp;
    private String jail;
    private List<String> mails;
    private boolean teleportEnabled;
    private boolean autoTeleportEnabled;
    private List<UUID> ignoredPlayers;
    private boolean godmode;
    private boolean muted;
    private String muteReason;
    private long muteTimeout;
    private boolean jailed;
    private long jailTimeout;
    private long lastLogin;
    private long lastLogout;
    private String lastLoginAddress;
    private boolean afk;
    private boolean newplayer;
    private String geolocation;
    private boolean isSocialSpyEnabled;
    private boolean isNPC;
    private String lastAccountName = null;
    private boolean arePowerToolsEnabled;
    private Map<String, Long> kitTimestamps;
    // Pattern, Date. Pattern for less pattern creations
    private Map<Pattern, Long> commandCooldowns;
    private boolean acceptingPay = true; // players accept pay by default
    private Boolean confirmPay;
    private Boolean confirmClear;
    private boolean lastMessageReplyRecipient;
    private int warnings = 0;

    protected UserData(final Player base, final IEssentials ess) {
        super(base);
        this.ess = ess;
        final File folder = new File(ess.getDataFolder(), "userdata");
        if (!folder.exists()) {
            folder.mkdirs();
        }

        String filename;
        try {
            filename = base.getUniqueId().toString();
        } catch (final Throwable ex) {
            ess.getLogger().warning("Falling back to old username system for " + base.getName());
            filename = base.getName();
        }

        config = new EssentialsUserConf(base.getName(), base.getUniqueId(), new File(folder, filename + ".yml"));
        reloadConfig();
    }

    public final void reset() {
        config.forceSave();
        config.getFile().delete();
        if (config.username != null) {
            ess.getUserMap().removeUser(config.username);
        }
    }

    public final void cleanup() {
        config.cleanup();
    }

    @Override
    public final void reloadConfig() {
        config.load();
        money = _getMoney();
        unlimited = _getUnlimited();
        powertools = _getPowertools();
        homes = _getHomes();
        lastLocation = _getLastLocation();
        lastTeleportTimestamp = _getLastTeleportTimestamp();
        lastHealTimestamp = _getLastHealTimestamp();
        jail = _getJail();
        mails = _getMails();
        warnings = _getWarnings();
        teleportEnabled = _getTeleportEnabled();
        godmode = _getGodModeEnabled();
        muted = _getMuted();
        muteTimeout = _getMuteTimeout();
        muteReason = _getMuteReason();
        jailed = _getJailed();
        jailTimeout = _getJailTimeout();
        lastLogin = _getLastLogin();
        lastLogout = _getLastLogout();
        lastLoginAddress = _getLastLoginAddress();
        afk = _getAfk();
        geolocation = _getGeoLocation();
        isSocialSpyEnabled = _isSocialSpyEnabled();
        isNPC = _isNPC();
        arePowerToolsEnabled = _arePowerToolsEnabled();
        kitTimestamps = _getKitTimestamps();
        nickname = _getNickname();
        ignoredPlayers = _getIgnoredPlayers();
        logoutLocation = _getLogoutLocation();
        lastAccountName = _getLastAccountName();
        commandCooldowns = _getCommandCooldowns();
        acceptingPay = _getAcceptingPay();
        confirmPay = _getConfirmPay();
        confirmClear = _getConfirmClear();
        lastMessageReplyRecipient = _getLastMessageReplyRecipient();
    }

    private BigDecimal _getMoney() {
        BigDecimal result = ess.getSettings().getStartingBalance();
        final BigDecimal maxMoney = ess.getSettings().getMaxMoney();
        final BigDecimal minMoney = ess.getSettings().getMinMoney();

        // NPC banks are not actual player banks, as such they do not have player starting balance.
        if (isNPC()) {
            result = BigDecimal.ZERO;
        }

        if (config.hasProperty("money")) {
            result = config.getBigDecimal("money", result);
        }
        if (result.compareTo(maxMoney) > 0) {
            result = maxMoney;
        }
        if (result.compareTo(minMoney) < 0) {
            result = minMoney;
        }
        return result;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(final BigDecimal value, final boolean throwError) throws MaxMoneyException {
        final BigDecimal maxMoney = ess.getSettings().getMaxMoney();
        final BigDecimal minMoney = ess.getSettings().getMinMoney();
        if (value.compareTo(maxMoney) > 0) {
            if (throwError) {
                throw new MaxMoneyException();
            }
            money = maxMoney;
        } else {
            money = value;
        }
        if (money.compareTo(minMoney) < 0) {
            money = minMoney;
        }
        config.setProperty("money", money);
        stopTransaction();
    }

    private Map<String, Object> _getHomes() {
        if (config.isConfigurationSection("homes")) {
            return config.getConfigurationSection("homes").getValues(false);
        }
        return new HashMap<>();
    }

    private String getHomeName(String search) {
        if (NumberUtil.isInt(search)) {
            try {
                search = getHomes().get(Integer.parseInt(search) - 1);
            } catch (final NumberFormatException | IndexOutOfBoundsException ignored) {
            }
        }
        return search;
    }

    public Location getHome(final String name) throws Exception {
        final String search = getHomeName(name);
        return config.getLocation("homes." + search, this.getBase().getServer());
    }

    public Location getHome(final Location world) {
        try {
            if (getHomes().isEmpty()) {
                return null;
            }
            Location loc;
            for (final String home : getHomes()) {
                loc = config.getLocation("homes." + home, this.getBase().getServer());
                if (world.getWorld() == loc.getWorld()) {
                    return loc;
                }

            }
            loc = config.getLocation("homes." + getHomes().get(0), this.getBase().getServer());
            return loc;
        } catch (final InvalidWorldException ex) {
            return null;
        }
    }

    public List<String> getHomes() {
        return new ArrayList<>(homes.keySet());
    }

    public void setHome(String name, final Location loc) {
        //Invalid names will corrupt the yaml
        name = StringUtil.safeString(name);
        homes.put(name, loc);
        config.setProperty("homes." + name, loc);
        config.save();
    }

    public void delHome(final String name) throws Exception {
        String search = getHomeName(name);
        if (!homes.containsKey(search)) {
            search = StringUtil.safeString(search);
        }
        if (homes.containsKey(search)) {
            homes.remove(search);
            config.removeProperty("homes." + search);
            config.save();
        } else {
            throw new Exception(tl("invalidHome", search));
        }
    }

    public boolean hasHome() {
        return config.hasProperty("home");
    }

    public boolean hasHome(final String name) {
        return config.hasProperty("homes." + name);
    }

    public String _getNickname() {
        return config.getString("nickname");
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(final String nick) {
        nickname = nick;
        config.setProperty("nickname", nick);
        config.save();
    }

    private Set<Material> _getUnlimited() {
        final Set<Material> retlist = new HashSet<>();
        final List<String> configList = config.getStringList("unlimited");
        for (final String s : configList) {
            final Material mat = Material.matchMaterial(s);
            if (mat != null) {
                retlist.add(mat);
            }
        }

        return retlist;
    }

    public Set<Material> getUnlimited() {
        return unlimited;
    }

    public boolean hasUnlimited(final ItemStack stack) {
        return unlimited.contains(stack.getType());
    }

    public void setUnlimited(final ItemStack stack, final boolean state) {
        final boolean wasUpdated;
        if (state) {
            wasUpdated = unlimited.add(stack.getType());
        } else {
            wasUpdated = unlimited.remove(stack.getType());
        }

        if (wasUpdated) {
            applyUnlimited();
        }
    }

    private void applyUnlimited() {
        config.setProperty("unlimited", unlimited.stream().map(Enum::name).collect(Collectors.toList()));
        config.save();
    }

    private Map<String, Object> _getPowertools() {
        if (config.isConfigurationSection("powertools")) {
            return config.getConfigurationSection("powertools").getValues(false);
        }
        return new HashMap<>();
    }

    public void clearAllPowertools() {
        powertools.clear();
        config.setProperty("powertools", powertools);
        config.save();
    }

    @SuppressWarnings("unchecked")
    public List<String> getPowertool(final ItemStack stack) {
        return (List<String>) powertools.get(stack.getType().name().toLowerCase(Locale.ENGLISH));
    }

    @SuppressWarnings("unchecked")
    public List<String> getPowertool(final Material material) {
        return (List<String>) powertools.get(material.name().toLowerCase(Locale.ENGLISH));
    }

    public void setPowertool(final ItemStack stack, final List<String> commandList) {
        if (commandList == null || commandList.isEmpty()) {
            powertools.remove(stack.getType().name().toLowerCase(Locale.ENGLISH));
        } else {
            powertools.put(stack.getType().name().toLowerCase(Locale.ENGLISH), commandList);
        }
        config.setProperty("powertools", powertools);
        config.save();
    }

    public boolean hasPowerTools() {
        return !powertools.isEmpty();
    }

    private Location _getLastLocation() {
        try {
            return config.getLocation("lastlocation", this.getBase().getServer());
        } catch (final InvalidWorldException e) {
            return null;
        }
    }

    public Location getLastLocation() {
        return lastLocation;
    }

    public void setLastLocation(final Location loc) {
        if (loc == null || loc.getWorld() == null) {
            return;
        }
        lastLocation = loc;
        config.setProperty("lastlocation", loc);
        config.save();
    }

    private Location _getLogoutLocation() {
        try {
            return config.getLocation("logoutlocation", this.getBase().getServer());
        } catch (final InvalidWorldException e) {
            return null;
        }
    }

    public Location getLogoutLocation() {
        return logoutLocation;
    }

    public void setLogoutLocation(final Location loc) {
        if (loc == null || loc.getWorld() == null) {
            return;
        }
        logoutLocation = loc;
        config.setProperty("logoutlocation", loc);
        config.save();
    }

    private long _getLastTeleportTimestamp() {
        return config.getLong("timestamps.lastteleport", 0);
    }

    public long getLastTeleportTimestamp() {
        return lastTeleportTimestamp;
    }

    public void setLastTeleportTimestamp(final long time) {
        lastTeleportTimestamp = time;
        config.setProperty("timestamps.lastteleport", time);
        config.save();
    }

    private long _getLastHealTimestamp() {
        return config.getLong("timestamps.lastheal", 0);
    }

    public long getLastHealTimestamp() {
        return lastHealTimestamp;
    }

    public void setLastHealTimestamp(final long time) {
        lastHealTimestamp = time;
        config.setProperty("timestamps.lastheal", time);
        config.save();
    }

    private String _getJail() {
        return config.getString("jail");
    }

    public String getJail() {
        return jail;
    }

    public void setJail(final String jail) {
        if (jail == null || jail.isEmpty()) {
            this.jail = null;
            config.removeProperty("jail");
        } else {
            this.jail = jail;
            config.setProperty("jail", jail);
        }
        config.save();
    }

    private List<String> _getMails() {
        return config.getStringList("mail");
    }

    public List<String> getMails() {
        return mails;
    }

    public void setMails(List<String> mails) {
        if (mails == null) {
            config.removeProperty("mail");
            mails = _getMails();
        } else {
            config.setProperty("mail", mails);
        }
        this.mails = mails;
        config.save();
    }

    public void addMail(final String mail) {
        mails.add(mail);
        setMails(mails);
    }

    private boolean _getTeleportEnabled() {
        return config.getBoolean("teleportenabled", true);
    }

    public boolean isTeleportEnabled() {
        return teleportEnabled;
    }

    public void setTeleportEnabled(final boolean set) {
        teleportEnabled = set;
        config.setProperty("teleportenabled", set);
        config.save();
    }

    private boolean _getAutoTeleportEnabled() {
        return config.getBoolean("teleportauto", false);
    }

    public boolean isAutoTeleportEnabled() {
        return autoTeleportEnabled;
    }

    public void setAutoTeleportEnabled(final boolean set) {
        autoTeleportEnabled = set;
        config.setProperty("teleportauto", set);
        config.save();
    }

    public List<UUID> _getIgnoredPlayers() {
        final List<UUID> players = new ArrayList<>();
        for (final String uuid : config.getStringList("ignore")) {
            try {
                players.add(UUID.fromString(uuid));
            } catch (final IllegalArgumentException ignored) {
            }
        }
        return Collections.synchronizedList(players);
    }

    @Deprecated
    public void setIgnoredPlayers(final List<String> players) {
        final List<UUID> uuids = new ArrayList<>();
        for (final String player : players) {
            final User user = ess.getOfflineUser(player);
            if (user == null) {
                return;
            }
            uuids.add(user.getBase().getUniqueId());
        }
        setIgnoredPlayerUUIDs(uuids);
    }

    public void setIgnoredPlayerUUIDs(final List<UUID> players) {
        if (players == null || players.isEmpty()) {
            ignoredPlayers = Collections.synchronizedList(new ArrayList<>());
            config.removeProperty("ignore");
        } else {
            ignoredPlayers = players;
            final List<String> uuids = new ArrayList<>();
            for (final UUID uuid : players) {
                uuids.add(uuid.toString());
            }
            config.setProperty("ignore", uuids);
        }
        config.save();
    }

    @Deprecated
    public boolean isIgnoredPlayer(final String userName) {
        final IUser user = ess.getUser(userName);
        if (user == null || !user.getBase().isOnline()) {
            return false;
        }
        return isIgnoredPlayer(user);
    }

    public boolean isIgnoredPlayer(final IUser user) {
        return ignoredPlayers.contains(user.getBase().getUniqueId()) && !user.isIgnoreExempt();
    }

    public void setIgnoredPlayer(final IUser user, final boolean set) {
        final UUID uuid = user.getBase().getUniqueId();
        if (set) {
            if (!ignoredPlayers.contains(uuid)) {
                ignoredPlayers.add(uuid);
            }
        } else {
            ignoredPlayers.remove(uuid);
        }
        setIgnoredPlayerUUIDs(ignoredPlayers);
    }

    private boolean _getGodModeEnabled() {
        return config.getBoolean("godmode", false);
    }

    public boolean isGodModeEnabled() {
        return godmode;
    }

    public void setGodModeEnabled(final boolean set) {
        godmode = set;
        config.setProperty("godmode", set);
        config.save();
    }

    public boolean _getMuted() {
        return config.getBoolean("muted", false);
    }

    public boolean getMuted() {
        return muted;
    }

    public boolean isMuted() {
        return muted;
    }

    public void setMuted(final boolean set) {
        muted = set;
        config.setProperty("muted", set);
        config.save();
    }

    public String _getMuteReason() {
        return config.getString("muteReason");
    }

    public String getMuteReason() {
        return muteReason;
    }

    public void setMuteReason(final String reason) {
        if (reason == null) {
            config.removeProperty("muteReason");
            muteReason = null;
        } else {
            muteReason = reason;
            config.setProperty("muteReason", reason);
        }
        config.save();
    }

    public boolean hasMuteReason() {
        return muteReason != null;
    }

    private long _getMuteTimeout() {
        return config.getLong("timestamps.mute", 0);
    }

    public long getMuteTimeout() {
        return muteTimeout;
    }

    public void setMuteTimeout(final long time) {
        muteTimeout = time;
        config.setProperty("timestamps.mute", time);
        config.save();
    }

    private boolean _getJailed() {
        return config.getBoolean("jailed", false);
    }

    public boolean isJailed() {
        return jailed;
    }

    public void setJailed(final boolean set) {
        jailed = set;
        config.setProperty("jailed", set);
        config.save();
    }

    public boolean toggleJailed() {
        final boolean ret = !isJailed();
        setJailed(ret);
        return ret;
    }

    private long _getJailTimeout() {
        return config.getLong("timestamps.jail", 0);
    }

    public long getJailTimeout() {
        return jailTimeout;
    }

    public void setJailTimeout(final long time) {
        jailTimeout = time;
        config.setProperty("timestamps.jail", time);
        config.save();
    }

    private long _getLastLogin() {
        return config.getLong("timestamps.login", 0);
    }

    public long getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(final long time) {
        _setLastLogin(time);
        if (base.getAddress() != null && base.getAddress().getAddress() != null) {
            _setLastLoginAddress(base.getAddress().getAddress().getHostAddress());
        }
        config.save();
    }

    private void _setLastLogin(final long time) {
        lastLogin = time;
        config.setProperty("timestamps.login", time);
    }

    private long _getLastLogout() {
        return config.getLong("timestamps.logout", 0);
    }

    public long getLastLogout() {
        return lastLogout;
    }

    public void setLastLogout(final long time) {
        lastLogout = time;
        config.setProperty("timestamps.logout", time);
        config.save();
    }

    private String _getLastLoginAddress() {
        return config.getString("ipAddress", "");
    }

    public String getLastLoginAddress() {
        return lastLoginAddress;
    }

    private void _setLastLoginAddress(final String address) {
        lastLoginAddress = address;
        config.setProperty("ipAddress", address);
    }

    private boolean _getAfk() {
        return config.getBoolean("afk", false);
    }

    public boolean isAfk() {
        return afk;
    }

    public void _setAfk(final boolean set) {
        afk = set;
        config.setProperty("afk", set);
        config.save();
    }
        
    private int _getWarnings() {
        return warnings;
    }
    
    public int getWarnings() {
        return config.getInt("warnings");
    }
    
    public void setWarnings(int warns) {
        warnings = warns;
        config.setProperty("warnings", warns);
        config.save();
    }
    
    private String _getGeoLocation() {
        return config.getString("geolocation");
    }

    public String getGeoLocation() {
        return geolocation;
    }

    public void setGeoLocation(final String geolocation) {
        if (geolocation == null || geolocation.isEmpty()) {
            this.geolocation = null;
            config.removeProperty("geolocation");
        } else {
            this.geolocation = geolocation;
            config.setProperty("geolocation", geolocation);
        }
        config.save();
    }

    private boolean _isSocialSpyEnabled() {
        return config.getBoolean("socialspy", false);
    }

    public boolean isSocialSpyEnabled() {
        return isSocialSpyEnabled;
    }

    public void setSocialSpyEnabled(final boolean status) {
        isSocialSpyEnabled = status;
        config.setProperty("socialspy", status);
        config.save();
    }

    private boolean _isNPC() {
        return config.getBoolean("npc", false);
    }

    public boolean isNPC() {
        return isNPC;
    }

    public void setNPC(final boolean set) {
        isNPC = set;
        config.setProperty("npc", set);
        config.save();
    }

    public String getLastAccountName() {
        return lastAccountName;
    }

    public void setLastAccountName(final String lastAccountName) {
        this.lastAccountName = lastAccountName;
        config.setProperty("lastAccountName", lastAccountName);
        config.save();
        ess.getUserMap().trackUUID(getConfigUUID(), lastAccountName, true);
    }

    public String _getLastAccountName() {
        return config.getString("lastAccountName", null);
    }

    public boolean arePowerToolsEnabled() {
        return arePowerToolsEnabled;
    }

    public void setPowerToolsEnabled(final boolean set) {
        arePowerToolsEnabled = set;
        config.setProperty("powertoolsenabled", set);
        config.save();
    }

    public boolean togglePowerToolsEnabled() {
        final boolean ret = !arePowerToolsEnabled();
        setPowerToolsEnabled(ret);
        return ret;
    }

    private boolean _arePowerToolsEnabled() {
        return config.getBoolean("powertoolsenabled", true);
    }

    private Map<String, Long> _getKitTimestamps() {

        if (config.isConfigurationSection("timestamps.kits")) {
            final ConfigurationSection section = config.getConfigurationSection("timestamps.kits");
            final Map<String, Long> timestamps = new HashMap<>();
            for (final String command : section.getKeys(false)) {
                if (section.isLong(command)) {
                    timestamps.put(command.toLowerCase(Locale.ENGLISH), section.getLong(command));
                } else if (section.isInt(command)) {
                    timestamps.put(command.toLowerCase(Locale.ENGLISH), (long) section.getInt(command));
                }
            }
            return timestamps;
        }
        return new HashMap<>();
    }

    public long getKitTimestamp(String name) {
        name = name.replace('.', '_').replace('/', '_').toLowerCase(Locale.ENGLISH);
        if (kitTimestamps != null && kitTimestamps.containsKey(name)) {
            return kitTimestamps.get(name);
        }
        return 0L;
    }

    public void setKitTimestamp(String name, final long time) {
        name = name.replace('.', '_').replace('/', '_').toLowerCase(Locale.ENGLISH);
        kitTimestamps.put(name, time);
        config.setProperty("timestamps.kits", kitTimestamps);
        config.save();
    }

    public void setConfigProperty(String node, final Object object) {
        final String prefix = "info.";
        node = prefix + node;
        if (object instanceof Map) {
            config.setProperty(node, (Map) object);
        } else if (object instanceof List) {
            config.setProperty(node, (List<String>) object);
        } else if (object instanceof Location) {
            config.setProperty(node, (Location) object);
        } else if (object instanceof ItemStack) {
            config.setProperty(node, (ItemStack) object);
        } else {
            config.setProperty(node, object);
        }
        config.save();
    }

    public Set<String> getConfigKeys() {
        if (config.isConfigurationSection("info")) {
            return config.getConfigurationSection("info").getKeys(true);
        }
        return new HashSet<>();
    }

    public Map<String, Object> getConfigMap() {
        if (config.isConfigurationSection("info")) {
            return config.getConfigurationSection("info").getValues(true);
        }
        return new HashMap<>();
    }

    public Map<String, Object> getConfigMap(final String node) {
        if (config.isConfigurationSection("info." + node)) {
            return config.getConfigurationSection("info." + node).getValues(true);
        }
        return new HashMap<>();
    }

    private Map<Pattern, Long> _getCommandCooldowns() {
        if (!config.contains("timestamps.command-cooldowns")) {
            return null;
        }

        // See saveCommandCooldowns() for deserialization explanation
        final List<Map<?, ?>> section = config.getMapList("timestamps.command-cooldowns");
        final HashMap<Pattern, Long> result = new HashMap<>();
        for (final Map<?, ?> map : section) {
            final Pattern pattern = Pattern.compile(map.get("pattern").toString());
            final long expiry = ((Number) map.get("expiry")).longValue();
            result.put(pattern, expiry);
        }
        return result;
    }

    public Map<Pattern, Long> getCommandCooldowns() {
        if (this.commandCooldowns == null) {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(this.commandCooldowns);
    }

    public Date getCommandCooldownExpiry(final String label) {
        if (commandCooldowns != null) {
            for (final Entry<Pattern, Long> entry : this.commandCooldowns.entrySet()) {
                if (entry.getKey().matcher(label).matches()) {
                    return new Date(entry.getValue());
                }
            }
        }
        return null;
    }

    public void addCommandCooldown(final Pattern pattern, final Date expiresAt, final boolean save) {
        if (this.commandCooldowns == null) {
            this.commandCooldowns = new HashMap<>();
        }
        this.commandCooldowns.put(pattern, expiresAt.getTime());
        if (save) {
            saveCommandCooldowns();
        }
    }

    public boolean clearCommandCooldown(final Pattern pattern) {
        if (this.commandCooldowns == null) {
            return false; // false for no modification
        }

        if (this.commandCooldowns.remove(pattern) != null) {
            saveCommandCooldowns();
            return true;
        }
        return false;
    }

    private void saveCommandCooldowns() {
        // Serialization explanation:
        //
        // Serialization is done as a map list instead of a config section due to limitations.
        // When serializing patterns (which commonly include full stops .) Bukkit/Essentials config framework
        // interprets it as a path separator, thus it breaks up the regex into sub nodes causing invalid syntax.
        // Thus each command cooldown is instead stored as a Map of {pattern: .., expiry: ..} to work around this.
        final List<Object> serialized = new ArrayList<>();
        for (final Entry<Pattern, Long> entry : this.commandCooldowns.entrySet()) {
            // Don't save expired cooldowns
            if (entry.getValue() < System.currentTimeMillis()) {
                continue;
            }

            final Map<?, ?> map = ImmutableMap.builder()
                .put("pattern", entry.getKey().pattern())
                .put("expiry", entry.getValue())
                .build();
            serialized.add(map);
        }
        config.setProperty("timestamps.command-cooldowns", serialized);
        save();
    }

    public boolean _getAcceptingPay() {
        return config.getBoolean("acceptingPay", true);
    }

    public boolean isAcceptingPay() {
        return acceptingPay;
    }

    public void setAcceptingPay(final boolean acceptingPay) {
        this.acceptingPay = acceptingPay;
        config.setProperty("acceptingPay", acceptingPay);
        save();
    }

    private Boolean _getConfirmPay() {
        return (Boolean) config.get("confirm-pay");
    }

    public boolean isPromptingPayConfirm() {
        return confirmPay != null ? confirmPay : ess.getSettings().isConfirmCommandEnabledByDefault("pay");
    }

    public void setPromptingPayConfirm(final boolean prompt) {
        this.confirmPay = prompt;
        config.setProperty("confirm-pay", prompt);
        save();
    }

    private Boolean _getConfirmClear() {
        return (Boolean) config.get("confirm-clear");
    }

    public boolean isPromptingClearConfirm() {
        return confirmClear != null ? confirmClear : ess.getSettings().isConfirmCommandEnabledByDefault("clearinventory");
    }

    public void setPromptingClearConfirm(final boolean prompt) {
        this.confirmClear = prompt;
        config.setProperty("confirm-clear", prompt);
        save();
    }

    private boolean _getLastMessageReplyRecipient() {
        return config.getBoolean("last-message-reply-recipient", ess.getSettings().isLastMessageReplyRecipient());
    }

    public boolean isLastMessageReplyRecipient() {
        return this.lastMessageReplyRecipient;
    }

    public void setLastMessageReplyRecipient(final boolean enabled) {
        this.lastMessageReplyRecipient = enabled;
        config.setProperty("last-message-reply-recipient", enabled);
        save();
    }

    public UUID getConfigUUID() {
        return config.uuid;
    }

    public void save() {
        config.save();
    }

    public void startTransaction() {
        config.startTransaction();
    }

    public void stopTransaction() {
        config.stopTransaction();
    }
}
