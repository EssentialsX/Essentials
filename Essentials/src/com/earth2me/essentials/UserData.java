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
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import static com.earth2me.essentials.I18n.tl;


/**
 * <p>Abstract UserData class.</p>
 *
 * @author LoopyD
 * @version $Id: $Id
 */
public abstract class UserData extends PlayerExtension implements IConf {
    protected final transient IEssentials ess;
    private final EssentialsUserConf config;
    private final File folder;

    /**
     * <p>Constructor for UserData.</p>
     *
     * @param base a {@link org.bukkit.entity.Player} object.
     * @param ess a {@link net.ess3.api.IEssentials} object.
     */
    protected UserData(Player base, IEssentials ess) {
        super(base);
        this.ess = ess;
        folder = new File(ess.getDataFolder(), "userdata");
        if (!folder.exists()) {
            folder.mkdirs();
        }

        String filename;
        try {
            filename = base.getUniqueId().toString();
        } catch (Throwable ex) {
            ess.getLogger().warning("Falling back to old username system for " + base.getName());
            filename = base.getName();
        }

        config = new EssentialsUserConf(base.getName(), base.getUniqueId(), new File(folder, filename + ".yml"));
        reloadConfig();
    }

    /**
     * <p>reset.</p>
     */
    public final void reset() {
        config.forceSave();
        config.getFile().delete();
        if (config.username != null) {
            ess.getUserMap().removeUser(config.username);
        }
    }

    /**
     * <p>cleanup.</p>
     */
    public final void cleanup() {
        config.cleanup();
    }

    /** {@inheritDoc} */
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

    private BigDecimal money;

    private BigDecimal _getMoney() {
        BigDecimal result = ess.getSettings().getStartingBalance();
        BigDecimal maxMoney = ess.getSettings().getMaxMoney();
        BigDecimal minMoney = ess.getSettings().getMinMoney();

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

    /**
     * <p>Getter for the field <code>money</code>.</p>
     *
     * @return a {@link java.math.BigDecimal} object.
     */
    public BigDecimal getMoney() {
        return money;
    }

    /**
     * <p>Setter for the field <code>money</code>.</p>
     *
     * @param value a {@link java.math.BigDecimal} object.
     * @param throwError a boolean.
     * @throws net.ess3.api.MaxMoneyException if any.
     */
    public void setMoney(BigDecimal value, boolean throwError) throws MaxMoneyException {
        BigDecimal maxMoney = ess.getSettings().getMaxMoney();
        BigDecimal minMoney = ess.getSettings().getMinMoney();
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

    private Map<String, Object> homes;

    private Map<String, Object> _getHomes() {
        if (config.isConfigurationSection("homes")) {
            return config.getConfigurationSection("homes").getValues(false);
        }
        return new HashMap<String, Object>();
    }

    private String getHomeName(String search) {
        if (NumberUtil.isInt(search)) {
            try {
                search = getHomes().get(Integer.parseInt(search) - 1);
            } catch (NumberFormatException e) {
            } catch (IndexOutOfBoundsException e) {
            }
        }
        return search;
    }

    /**
     * <p>getHome.</p>
     *
     * @param name a {@link java.lang.String} object.
     * @return a {@link org.bukkit.Location} object.
     * @throws java.lang.Exception if any.
     */
    public Location getHome(String name) throws Exception {
        String search = getHomeName(name);
        return config.getLocation("homes." + search, this.getBase().getServer());
    }

    /**
     * <p>getHome.</p>
     *
     * @param world a {@link org.bukkit.Location} object.
     * @return a {@link org.bukkit.Location} object.
     */
    public Location getHome(final Location world) {
        try {
            if (getHomes().isEmpty()) {
                return null;
            }
            Location loc;
            for (String home : getHomes()) {
                loc = config.getLocation("homes." + home, this.getBase().getServer());
                if (world.getWorld() == loc.getWorld()) {
                    return loc;
                }

            }
            loc = config.getLocation("homes." + getHomes().get(0), this.getBase().getServer());
            return loc;
        } catch (InvalidWorldException ex) {
            return null;
        }
    }

    /**
     * <p>Getter for the field <code>homes</code>.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<String> getHomes() {
        return new ArrayList<String>(homes.keySet());
    }

    /**
     * <p>setHome.</p>
     *
     * @param name a {@link java.lang.String} object.
     * @param loc a {@link org.bukkit.Location} object.
     */
    public void setHome(String name, Location loc) {
        //Invalid names will corrupt the yaml
        name = StringUtil.safeString(name);
        homes.put(name, loc);
        config.setProperty("homes." + name, loc);
        config.save();
    }

    /**
     * <p>delHome.</p>
     *
     * @param name a {@link java.lang.String} object.
     * @throws java.lang.Exception if any.
     */
    public void delHome(String name) throws Exception {
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

    /**
     * <p>hasHome.</p>
     *
     * @return a boolean.
     */
    public boolean hasHome() {
        return config.hasProperty("home");
    }

    private String nickname;

    /**
     * <p>_getNickname.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String _getNickname() {
        return config.getString("nickname");
    }

    /**
     * <p>Getter for the field <code>nickname</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * <p>Setter for the field <code>nickname</code>.</p>
     *
     * @param nick a {@link java.lang.String} object.
     */
    public void setNickname(String nick) {
        nickname = nick;
        config.setProperty("nickname", nick);
        config.save();
    }

    private List<Material> unlimited;

    private List<Material> _getUnlimited() {
        List<Material> retlist = new ArrayList<>();
        List<String> configList = config.getStringList("unlimited");
        for(String s : configList) {
            Material mat = Material.matchMaterial(s);
            if(mat != null) {
                retlist.add(mat);
            }
        }

        return retlist;
    }

    /**
     * <p>Getter for the field <code>unlimited</code>.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<Material> getUnlimited() {
        return unlimited;
    }

    /**
     * <p>hasUnlimited.</p>
     *
     * @param stack a {@link org.bukkit.inventory.ItemStack} object.
     * @return a boolean.
     */
    public boolean hasUnlimited(ItemStack stack) {
        return unlimited.contains(stack.getType());
    }

    /**
     * <p>Setter for the field <code>unlimited</code>.</p>
     *
     * @param stack a {@link org.bukkit.inventory.ItemStack} object.
     * @param state a boolean.
     */
    public void setUnlimited(ItemStack stack, boolean state) {
        if (unlimited.contains(stack.getType())) {
            unlimited.remove(stack.getType());
        }
        if (state) {
            unlimited.add(stack.getType());
        }
        config.setProperty("unlimited", unlimited);
        config.save();
    }

    private Map<String, Object> powertools;

    private Map<String, Object> _getPowertools() {
        if (config.isConfigurationSection("powertools")) {
            return config.getConfigurationSection("powertools").getValues(false);
        }
        return new HashMap<>();
    }

    /**
     * <p>clearAllPowertools.</p>
     */
    public void clearAllPowertools() {
        powertools.clear();
        config.setProperty("powertools", powertools);
        config.save();
    }

    /**
     * <p>getPowertool.</p>
     *
     * @param stack a {@link org.bukkit.inventory.ItemStack} object.
     * @return a {@link java.util.List} object.
     */
    @SuppressWarnings("unchecked")
    public List<String> getPowertool(ItemStack stack) {
        return (List<String>) powertools.get(stack.getType().name().toLowerCase(Locale.ENGLISH));
    }

    /**
     * <p>getPowertool.</p>
     *
     * @param material a {@link org.bukkit.Material} object.
     * @return a {@link java.util.List} object.
     */
    @SuppressWarnings("unchecked")
    public List<String> getPowertool(Material material) {
        return (List<String>) powertools.get(material.name().toLowerCase(Locale.ENGLISH));
    }

    /**
     * <p>setPowertool.</p>
     *
     * @param stack a {@link org.bukkit.inventory.ItemStack} object.
     * @param commandList a {@link java.util.List} object.
     */
    public void setPowertool(ItemStack stack, List<String> commandList) {
        if (commandList == null || commandList.isEmpty()) {
            powertools.remove(stack.getType().name().toLowerCase(Locale.ENGLISH));
        } else {
            powertools.put(stack.getType().name().toLowerCase(Locale.ENGLISH), commandList);
        }
        config.setProperty("powertools", powertools);
        config.save();
    }

    /**
     * <p>hasPowerTools.</p>
     *
     * @return a boolean.
     */
    public boolean hasPowerTools() {
        return !powertools.isEmpty();
    }

    private Location lastLocation;

    private Location _getLastLocation() {
        try {
            return config.getLocation("lastlocation", this.getBase().getServer());
        } catch (InvalidWorldException e) {
            return null;
        }
    }

    /**
     * <p>Getter for the field <code>lastLocation</code>.</p>
     *
     * @return a {@link org.bukkit.Location} object.
     */
    public Location getLastLocation() {
        return lastLocation;
    }

    /**
     * <p>Setter for the field <code>lastLocation</code>.</p>
     *
     * @param loc a {@link org.bukkit.Location} object.
     */
    public void setLastLocation(Location loc) {
        if (loc == null || loc.getWorld() == null) {
            return;
        }
        lastLocation = loc;
        config.setProperty("lastlocation", loc);
        config.save();
    }

    private Location logoutLocation;

    private Location _getLogoutLocation() {
        try {
            return config.getLocation("logoutlocation", this.getBase().getServer());
        } catch (InvalidWorldException e) {
            return null;
        }
    }

    /**
     * <p>Getter for the field <code>logoutLocation</code>.</p>
     *
     * @return a {@link org.bukkit.Location} object.
     */
    public Location getLogoutLocation() {
        return logoutLocation;
    }

    /**
     * <p>Setter for the field <code>logoutLocation</code>.</p>
     *
     * @param loc a {@link org.bukkit.Location} object.
     */
    public void setLogoutLocation(Location loc) {
        if (loc == null || loc.getWorld() == null) {
            return;
        }
        logoutLocation = loc;
        config.setProperty("logoutlocation", loc);
        config.save();
    }

    private long lastTeleportTimestamp;

    private long _getLastTeleportTimestamp() {
        return config.getLong("timestamps.lastteleport", 0);
    }

    /**
     * <p>Getter for the field <code>lastTeleportTimestamp</code>.</p>
     *
     * @return a long.
     */
    public long getLastTeleportTimestamp() {
        return lastTeleportTimestamp;
    }

    /**
     * <p>Setter for the field <code>lastTeleportTimestamp</code>.</p>
     *
     * @param time a long.
     */
    public void setLastTeleportTimestamp(long time) {
        lastTeleportTimestamp = time;
        config.setProperty("timestamps.lastteleport", time);
        config.save();
    }

    private long lastHealTimestamp;

    private long _getLastHealTimestamp() {
        return config.getLong("timestamps.lastheal", 0);
    }

    /**
     * <p>Getter for the field <code>lastHealTimestamp</code>.</p>
     *
     * @return a long.
     */
    public long getLastHealTimestamp() {
        return lastHealTimestamp;
    }

    /**
     * <p>Setter for the field <code>lastHealTimestamp</code>.</p>
     *
     * @param time a long.
     */
    public void setLastHealTimestamp(long time) {
        lastHealTimestamp = time;
        config.setProperty("timestamps.lastheal", time);
        config.save();
    }

    private String jail;

    private String _getJail() {
        return config.getString("jail");
    }

    /**
     * <p>Getter for the field <code>jail</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getJail() {
        return jail;
    }

    /**
     * <p>Setter for the field <code>jail</code>.</p>
     *
     * @param jail a {@link java.lang.String} object.
     */
    public void setJail(String jail) {
        if (jail == null || jail.isEmpty()) {
            this.jail = null;
            config.removeProperty("jail");
        } else {
            this.jail = jail;
            config.setProperty("jail", jail);
        }
        config.save();
    }

    private List<String> mails;

    private List<String> _getMails() {
        return config.getStringList("mail");
    }

    /**
     * <p>Getter for the field <code>mails</code>.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<String> getMails() {
        return mails;
    }

    /**
     * <p>Setter for the field <code>mails</code>.</p>
     *
     * @param mails a {@link java.util.List} object.
     */
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

    /**
     * <p>addMail.</p>
     *
     * @param mail a {@link java.lang.String} object.
     */
    public void addMail(String mail) {
        mails.add(mail);
        setMails(mails);
    }

    private boolean teleportEnabled;

    private boolean _getTeleportEnabled() {
        return config.getBoolean("teleportenabled", true);
    }

    /**
     * <p>isTeleportEnabled.</p>
     *
     * @return a boolean.
     */
    public boolean isTeleportEnabled() {
        return teleportEnabled;
    }

    /**
     * <p>Setter for the field <code>teleportEnabled</code>.</p>
     *
     * @param set a boolean.
     */
    public void setTeleportEnabled(boolean set) {
        teleportEnabled = set;
        config.setProperty("teleportenabled", set);
        config.save();
    }

    private boolean autoTeleportEnabled;

    private boolean _getAutoTeleportEnabled() {
        return config.getBoolean("teleportauto", false);
    }

    /**
     * <p>isAutoTeleportEnabled.</p>
     *
     * @return a boolean.
     */
    public boolean isAutoTeleportEnabled() {
        return autoTeleportEnabled;
    }

    /**
     * <p>Setter for the field <code>autoTeleportEnabled</code>.</p>
     *
     * @param set a boolean.
     */
    public void setAutoTeleportEnabled(boolean set) {
        autoTeleportEnabled = set;
        config.setProperty("teleportauto", set);
        config.save();
    }

    private List<String> ignoredPlayers;

    /**
     * <p>_getIgnoredPlayers.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<String> _getIgnoredPlayers() {
        return Collections.synchronizedList(config.getStringList("ignore"));
    }

    /**
     * <p>Setter for the field <code>ignoredPlayers</code>.</p>
     *
     * @param players a {@link java.util.List} object.
     */
    public void setIgnoredPlayers(List<String> players) {
        if (players == null || players.isEmpty()) {
            ignoredPlayers = Collections.synchronizedList(new ArrayList<String>());
            config.removeProperty("ignore");
        } else {
            ignoredPlayers = players;
            config.setProperty("ignore", players);
        }
        config.save();
    }

    /**
     * <p>isIgnoredPlayer.</p>
     *
     * @param userName a {@link java.lang.String} object.
     * @return a boolean.
     */
    @Deprecated
    public boolean isIgnoredPlayer(final String userName) {
        final IUser user = ess.getUser(userName);
        if (user == null || !user.getBase().isOnline()) {
            return false;
        }
        return isIgnoredPlayer(user);
    }

    /**
     * <p>isIgnoredPlayer.</p>
     *
     * @param user a {@link com.earth2me.essentials.IUser} object.
     * @return a boolean.
     */
    public boolean isIgnoredPlayer(IUser user) {
        return (ignoredPlayers.contains(user.getName().toLowerCase(Locale.ENGLISH)) && !user.isIgnoreExempt());
    }

    /**
     * <p>setIgnoredPlayer.</p>
     *
     * @param user a {@link com.earth2me.essentials.IUser} object.
     * @param set a boolean.
     */
    public void setIgnoredPlayer(IUser user, boolean set) {
        final String entry = user.getName().toLowerCase(Locale.ENGLISH);
        if (set) {
            if (!ignoredPlayers.contains(entry)) ignoredPlayers.add(entry);
        } else {
            ignoredPlayers.remove(entry);
        }
        setIgnoredPlayers(ignoredPlayers);
    }

    private boolean godmode;

    private boolean _getGodModeEnabled() {
        return config.getBoolean("godmode", false);
    }

    /**
     * <p>isGodModeEnabled.</p>
     *
     * @return a boolean.
     */
    public boolean isGodModeEnabled() {
        return godmode;
    }

    /**
     * <p>setGodModeEnabled.</p>
     *
     * @param set a boolean.
     */
    public void setGodModeEnabled(boolean set) {
        godmode = set;
        config.setProperty("godmode", set);
        config.save();
    }

    private boolean muted;
    private String muteReason;

    /**
     * <p>_getMuted.</p>
     *
     * @return a boolean.
     */
    public boolean _getMuted() {
        return config.getBoolean("muted", false);
    }

    /**
     * <p>Getter for the field <code>muted</code>.</p>
     *
     * @return a boolean.
     */
    public boolean getMuted() {
        return muted;
    }

    /**
     * <p>isMuted.</p>
     *
     * @return a boolean.
     */
    public boolean isMuted() {
        return muted;
    }

    /**
     * <p>Setter for the field <code>muted</code>.</p>
     *
     * @param set a boolean.
     */
    public void setMuted(boolean set) {
        muted = set;
        config.setProperty("muted", set);
        config.save();
    }

    /**
     * <p>_getMuteReason.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String _getMuteReason() {
        return config.getString("muteReason");
    }

    /**
     * <p>Getter for the field <code>muteReason</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getMuteReason() {
        return muteReason;
    }

    /**
     * <p>Setter for the field <code>muteReason</code>.</p>
     *
     * @param reason a {@link java.lang.String} object.
     */
    public void setMuteReason(String reason) {
        if (reason == null) {
            config.removeProperty("muteReason");
            muteReason = null;
        } else {
            muteReason = reason;
            config.setProperty("muteReason", reason);
        }
        config.save();
    }

    /**
     * <p>hasMuteReason.</p>
     *
     * @return a boolean.
     */
    public boolean hasMuteReason(){
        return muteReason != null;
    }

    private long muteTimeout;

    private long _getMuteTimeout() {
        return config.getLong("timestamps.mute", 0);
    }

    /**
     * <p>Getter for the field <code>muteTimeout</code>.</p>
     *
     * @return a long.
     */
    public long getMuteTimeout() {
        return muteTimeout;
    }

    /**
     * <p>Setter for the field <code>muteTimeout</code>.</p>
     *
     * @param time a long.
     */
    public void setMuteTimeout(long time) {
        muteTimeout = time;
        config.setProperty("timestamps.mute", time);
        config.save();
    }

    private boolean jailed;

    private boolean _getJailed() {
        return config.getBoolean("jailed", false);
    }

    /**
     * <p>isJailed.</p>
     *
     * @return a boolean.
     */
    public boolean isJailed() {
        return jailed;
    }

    /**
     * <p>Setter for the field <code>jailed</code>.</p>
     *
     * @param set a boolean.
     */
    public void setJailed(boolean set) {
        jailed = set;
        config.setProperty("jailed", set);
        config.save();
    }

    /**
     * <p>toggleJailed.</p>
     *
     * @return a boolean.
     */
    public boolean toggleJailed() {
        boolean ret = !isJailed();
        setJailed(ret);
        return ret;
    }

    private long jailTimeout;

    private long _getJailTimeout() {
        return config.getLong("timestamps.jail", 0);
    }

    /**
     * <p>Getter for the field <code>jailTimeout</code>.</p>
     *
     * @return a long.
     */
    public long getJailTimeout() {
        return jailTimeout;
    }

    /**
     * <p>Setter for the field <code>jailTimeout</code>.</p>
     *
     * @param time a long.
     */
    public void setJailTimeout(long time) {
        jailTimeout = time;
        config.setProperty("timestamps.jail", time);
        config.save();
    }

    private long lastLogin;

    private long _getLastLogin() {
        return config.getLong("timestamps.login", 0);
    }

    /**
     * <p>Getter for the field <code>lastLogin</code>.</p>
     *
     * @return a long.
     */
    public long getLastLogin() {
        return lastLogin;
    }

    private void _setLastLogin(long time) {
        lastLogin = time;
        config.setProperty("timestamps.login", time);
    }

    /**
     * <p>Setter for the field <code>lastLogin</code>.</p>
     *
     * @param time a long.
     */
    public void setLastLogin(long time) {
        _setLastLogin(time);
        if (base.getAddress() != null && base.getAddress().getAddress() != null) {
            _setLastLoginAddress(base.getAddress().getAddress().getHostAddress());
        }
        config.save();
    }

    private long lastLogout;

    private long _getLastLogout() {
        return config.getLong("timestamps.logout", 0);
    }

    /**
     * <p>Getter for the field <code>lastLogout</code>.</p>
     *
     * @return a long.
     */
    public long getLastLogout() {
        return lastLogout;
    }

    /**
     * <p>Setter for the field <code>lastLogout</code>.</p>
     *
     * @param time a long.
     */
    public void setLastLogout(long time) {
        lastLogout = time;
        config.setProperty("timestamps.logout", time);
        config.save();
    }

    private String lastLoginAddress;

    private String _getLastLoginAddress() {
        return config.getString("ipAddress", "");
    }

    /**
     * <p>Getter for the field <code>lastLoginAddress</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getLastLoginAddress() {
        return lastLoginAddress;
    }

    private void _setLastLoginAddress(String address) {
        lastLoginAddress = address;
        config.setProperty("ipAddress", address);
    }

    private boolean afk;

    private boolean _getAfk() {
        return config.getBoolean("afk", false);
    }

    /**
     * <p>isAfk.</p>
     *
     * @return a boolean.
     */
    public boolean isAfk() {
        return afk;
    }

    /**
     * <p>_setAfk.</p>
     *
     * @param set a boolean.
     */
    public void _setAfk(boolean set) {
        afk = set;
        config.setProperty("afk", set);
        config.save();
    }

    private boolean newplayer;
    private String geolocation;

    private String _getGeoLocation() {
        return config.getString("geolocation");
    }

    /**
     * <p>getGeoLocation.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getGeoLocation() {
        return geolocation;
    }

    /**
     * <p>setGeoLocation.</p>
     *
     * @param geolocation a {@link java.lang.String} object.
     */
    public void setGeoLocation(String geolocation) {
        if (geolocation == null || geolocation.isEmpty()) {
            this.geolocation = null;
            config.removeProperty("geolocation");
        } else {
            this.geolocation = geolocation;
            config.setProperty("geolocation", geolocation);
        }
        config.save();
    }

    private boolean isSocialSpyEnabled;

    private boolean _isSocialSpyEnabled() {
        return config.getBoolean("socialspy", false);
    }

    /**
     * <p>isSocialSpyEnabled.</p>
     *
     * @return a boolean.
     */
    public boolean isSocialSpyEnabled() {
        return isSocialSpyEnabled;
    }

    /**
     * <p>setSocialSpyEnabled.</p>
     *
     * @param status a boolean.
     */
    public void setSocialSpyEnabled(boolean status) {
        isSocialSpyEnabled = status;
        config.setProperty("socialspy", status);
        config.save();
    }

    private boolean isNPC;

    private boolean _isNPC() {
        return config.getBoolean("npc", false);
    }

    /**
     * <p>isNPC.</p>
     *
     * @return a boolean.
     */
    public boolean isNPC() {
        return isNPC;
    }

    private String lastAccountName = null;

    /**
     * <p>Getter for the field <code>lastAccountName</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getLastAccountName() {
        return lastAccountName;
    }

    /**
     * <p>_getLastAccountName.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String _getLastAccountName() {
        return config.getString("lastAccountName", null);
    }

    /**
     * <p>Setter for the field <code>lastAccountName</code>.</p>
     *
     * @param lastAccountName a {@link java.lang.String} object.
     */
    public void setLastAccountName(String lastAccountName) {
        this.lastAccountName = lastAccountName;
        config.setProperty("lastAccountName", lastAccountName);
        config.save();
        ess.getUserMap().trackUUID(getConfigUUID(), lastAccountName, true);
    }

    /**
     * <p>setNPC.</p>
     *
     * @param set a boolean.
     */
    public void setNPC(boolean set) {
        isNPC = set;
        config.setProperty("npc", set);
        config.save();
    }

    private boolean arePowerToolsEnabled;

    /**
     * <p>arePowerToolsEnabled.</p>
     *
     * @return a boolean.
     */
    public boolean arePowerToolsEnabled() {
        return arePowerToolsEnabled;
    }

    /**
     * <p>setPowerToolsEnabled.</p>
     *
     * @param set a boolean.
     */
    public void setPowerToolsEnabled(boolean set) {
        arePowerToolsEnabled = set;
        config.setProperty("powertoolsenabled", set);
        config.save();
    }

    /**
     * <p>togglePowerToolsEnabled.</p>
     *
     * @return a boolean.
     */
    public boolean togglePowerToolsEnabled() {
        boolean ret = !arePowerToolsEnabled();
        setPowerToolsEnabled(ret);
        return ret;
    }

    private boolean _arePowerToolsEnabled() {
        return config.getBoolean("powertoolsenabled", true);
    }

    private Map<String, Long> kitTimestamps;

    private Map<String, Long> _getKitTimestamps() {

        if (config.isConfigurationSection("timestamps.kits")) {
            final ConfigurationSection section = config.getConfigurationSection("timestamps.kits");
            final Map<String, Long> timestamps = new HashMap<String, Long>();
            for (String command : section.getKeys(false)) {
                if (section.isLong(command)) {
                    timestamps.put(command.toLowerCase(Locale.ENGLISH), section.getLong(command));
                } else if (section.isInt(command)) {
                    timestamps.put(command.toLowerCase(Locale.ENGLISH), (long) section.getInt(command));
                }
            }
            return timestamps;
        }
        return new HashMap<String, Long>();
    }

    /**
     * <p>getKitTimestamp.</p>
     *
     * @param name a {@link java.lang.String} object.
     * @return a long.
     */
    public long getKitTimestamp(String name) {
        name = name.replace('.', '_').replace('/', '_');
        if (kitTimestamps != null && kitTimestamps.containsKey(name)) {
            return kitTimestamps.get(name);
        }
        return 0l;
    }

    /**
     * <p>setKitTimestamp.</p>
     *
     * @param name a {@link java.lang.String} object.
     * @param time a long.
     */
    public void setKitTimestamp(final String name, final long time) {
        kitTimestamps.put(name.toLowerCase(Locale.ENGLISH), time);
        config.setProperty("timestamps.kits", kitTimestamps);
        config.save();
    }

    /**
     * <p>setConfigProperty.</p>
     *
     * @param node a {@link java.lang.String} object.
     * @param object a {@link java.lang.Object} object.
     */
    public void setConfigProperty(String node, Object object) {
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

    /**
     * <p>getConfigKeys.</p>
     *
     * @return a {@link java.util.Set} object.
     */
    public Set<String> getConfigKeys() {
        if (config.isConfigurationSection("info")) {
            return config.getConfigurationSection("info").getKeys(true);
        }
        return new HashSet<String>();
    }

    /**
     * <p>getConfigMap.</p>
     *
     * @return a {@link java.util.Map} object.
     */
    public Map<String, Object> getConfigMap() {
        if (config.isConfigurationSection("info")) {
            return config.getConfigurationSection("info").getValues(true);
        }
        return new HashMap<String, Object>();
    }

    /**
     * <p>getConfigMap.</p>
     *
     * @param node a {@link java.lang.String} object.
     * @return a {@link java.util.Map} object.
     */
    public Map<String, Object> getConfigMap(String node) {
        if (config.isConfigurationSection("info." + node)) {
            return config.getConfigurationSection("info." + node).getValues(true);
        }
        return new HashMap<String, Object>();
    }

    // Pattern, Date. Pattern for less pattern creations
    private Map<Pattern, Long> commandCooldowns;

    private Map<Pattern, Long> _getCommandCooldowns() {
        if (!config.isConfigurationSection("timestamps.command-cooldowns")) {
            return null;
        }

        // See saveCommandCooldowns() for deserialization explanation
        List<Map<?, ?>> section = config.getMapList("timestamps.command-cooldowns");
        HashMap<Pattern, Long> result = new HashMap<>();
        for (Map<?, ?> map : section) {
            Pattern pattern = Pattern.compile(map.get("pattern").toString());
            long expiry = ((Number) map.get("expiry")).longValue();
            result.put(pattern, expiry);
        }
        return result;
    }

    /**
     * <p>Getter for the field <code>commandCooldowns</code>.</p>
     *
     * @return a {@link java.util.Map} object.
     */
    public Map<Pattern, Long> getCommandCooldowns() {
        if (this.commandCooldowns == null) {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(this.commandCooldowns);
    }

    /**
     * <p>getCommandCooldownExpiry.</p>
     *
     * @param label a {@link java.lang.String} object.
     * @return a {@link java.util.Date} object.
     */
    public Date getCommandCooldownExpiry(String label) {
        if (commandCooldowns != null) {
            for (Entry<Pattern, Long> entry : this.commandCooldowns.entrySet()) {
                if (entry.getKey().matcher(label).matches()) {
                    return new Date(entry.getValue());
                }
            }
        }
        return null;
    }

    /**
     * <p>addCommandCooldown.</p>
     *
     * @param pattern a {@link java.util.regex.Pattern} object.
     * @param expiresAt a {@link java.util.Date} object.
     * @param save a boolean.
     */
    public void addCommandCooldown(Pattern pattern, Date expiresAt, boolean save) {
        if (this.commandCooldowns == null) {
            this.commandCooldowns = new HashMap<>();
        }
        this.commandCooldowns.put(pattern, expiresAt.getTime());
        if (save) {
            saveCommandCooldowns();
        }
    }

    /**
     * <p>clearCommandCooldown.</p>
     *
     * @param pattern a {@link java.util.regex.Pattern} object.
     * @return a boolean.
     */
    public boolean clearCommandCooldown(Pattern pattern) {
        if (this.commandCooldowns == null) {
            return false; // false for no modification
        }

        if(this.commandCooldowns.remove(pattern) != null) {
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
        List<Object> serialized = new ArrayList<>();
        for (Entry<Pattern, Long> entry : this.commandCooldowns.entrySet()) {
            // Don't save expired cooldowns
            if (entry.getValue() < System.currentTimeMillis()) {
                continue;
            }

            Map<?, ?> map = ImmutableMap.builder()
                .put("pattern", entry.getKey().pattern())
                .put("expiry", entry.getValue())
                .build();
            serialized.add(map);
        }
        config.setProperty("timestamps.command-cooldowns", serialized);
        save();
    }

    private boolean acceptingPay = true; // players accept pay by default

    /**
     * <p>_getAcceptingPay.</p>
     *
     * @return a boolean.
     */
    public boolean _getAcceptingPay() {
        return config.getBoolean("acceptingPay", true);
    }

    /**
     * <p>isAcceptingPay.</p>
     *
     * @return a boolean.
     */
    public boolean isAcceptingPay() {
        return acceptingPay;
    }

    /**
     * <p>Setter for the field <code>acceptingPay</code>.</p>
     *
     * @param acceptingPay a boolean.
     */
    public void setAcceptingPay(boolean acceptingPay) {
        this.acceptingPay = acceptingPay;
        config.setProperty("acceptingPay", acceptingPay);
        save();
    }

    private Boolean confirmPay;

    private Boolean _getConfirmPay() {
        return (Boolean) config.get("confirm-pay");
    }

    /**
     * <p>isPromptingPayConfirm.</p>
     *
     * @return a boolean.
     */
    public boolean isPromptingPayConfirm() {
        return confirmPay != null ? confirmPay : ess.getSettings().isConfirmCommandEnabledByDefault("pay");
    }

    /**
     * <p>setPromptingPayConfirm.</p>
     *
     * @param prompt a boolean.
     */
    public void setPromptingPayConfirm(boolean prompt) {
        this.confirmPay = prompt;
        config.setProperty("confirm-pay", prompt);
        save();
    }

    private Boolean confirmClear;

    private Boolean _getConfirmClear() {
        return (Boolean) config.get("confirm-clear");
    }

    /**
     * <p>isPromptingClearConfirm.</p>
     *
     * @return a boolean.
     */
    public boolean isPromptingClearConfirm() {
        return confirmClear != null ? confirmClear : ess.getSettings().isConfirmCommandEnabledByDefault("clearinventory");
    }

    /**
     * <p>setPromptingClearConfirm.</p>
     *
     * @param prompt a boolean.
     */
    public void setPromptingClearConfirm(boolean prompt) {
        this.confirmClear = prompt;
        config.setProperty("confirm-clear", prompt);
        save();
    }

    private boolean lastMessageReplyRecipient;

    private boolean _getLastMessageReplyRecipient() {
        return config.getBoolean("last-message-reply-recipient", ess.getSettings().isLastMessageReplyRecipient());
    }

    /**
     * <p>isLastMessageReplyRecipient.</p>
     *
     * @return a boolean.
     */
    public boolean isLastMessageReplyRecipient() {
        return this.lastMessageReplyRecipient;
    }

    /**
     * <p>Setter for the field <code>lastMessageReplyRecipient</code>.</p>
     *
     * @param enabled a boolean.
     */
    public void setLastMessageReplyRecipient(boolean enabled) {
        this.lastMessageReplyRecipient = enabled;
        config.setProperty("last-message-reply-recipient", enabled);
        save();
    }

    /**
     * <p>getConfigUUID.</p>
     *
     * @return a {@link java.util.UUID} object.
     */
    public UUID getConfigUUID() {
        return config.uuid;
    }

    /**
     * <p>save.</p>
     */
    public void save() {
        config.save();
    }

    /**
     * <p>startTransaction.</p>
     */
    public void startTransaction() {
        config.startTransaction();
    }

    /**
     * <p>stopTransaction.</p>
     */
    public void stopTransaction() {
        config.stopTransaction();
    }
}
