package com.earth2me.essentials;

import com.earth2me.essentials.config.ConfigurateUtil;
import com.earth2me.essentials.config.EssentialsUserConfiguration;
import com.earth2me.essentials.config.entities.CommandCooldown;
import com.earth2me.essentials.config.entities.LazyLocation;
import com.earth2me.essentials.config.holders.UserConfigHolder;
import com.earth2me.essentials.utils.NumberUtil;
import com.earth2me.essentials.utils.StringUtil;
import com.google.common.base.Charsets;
import net.ess3.api.IEssentials;
import net.ess3.api.MaxMoneyException;
import net.essentialsx.api.v2.services.mail.MailMessage;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.serialize.SerializationException;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.regex.Pattern;

import static com.earth2me.essentials.I18n.tl;

public abstract class UserData extends PlayerExtension implements IConf {
    protected final transient IEssentials ess;
    private final EssentialsUserConfiguration config;
    private UserConfigHolder holder;
    private BigDecimal money;

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

        config = new EssentialsUserConfiguration(base.getName(), base.getUniqueId(), new File(folder, filename + ".yml"));
        reloadConfig();

        if (config.getUsername() == null) {
            config.setUsername(getLastAccountName());
        }
    }

    public final void reset() {
        config.blockingSave();
        config.getFile().delete();
        if (config.getUsername() != null) {
            ess.getUserMap().removeUser(config.getUsername());
            if (isNPC()) {
                final String uuid = UUID.nameUUIDFromBytes(("NPC:" + config.getUsername()).getBytes(Charsets.UTF_8)).toString();
                ess.getUserMap().removeUserUUID(uuid);
            }
        }
    }

    public final void cleanup() {
        config.blockingSave();
    }

    @Override
    public final void reloadConfig() {
        config.load();
        try {
            holder = config.getRootNode().get(UserConfigHolder.class);
        } catch (SerializationException e) {
            ess.getLogger().log(Level.SEVERE, "Error while reading user config: " + e.getMessage(), e);
            throw new RuntimeException(e);
        }
        config.setSaveHook(() -> {
            try {
                config.getRootNode().set(UserConfigHolder.class, holder);
            } catch (SerializationException e) {
                ess.getLogger().log(Level.SEVERE, "Error while saving user config: " + e.getMessage(), e);
                throw new RuntimeException(e);
            }
        });
        money = _getMoney();
    }

    private BigDecimal _getMoney() {
        BigDecimal result = ess.getSettings().getStartingBalance();
        final BigDecimal maxMoney = ess.getSettings().getMaxMoney();
        final BigDecimal minMoney = ess.getSettings().getMinMoney();

        // NPC banks are not actual player banks, as such they do not have player starting balance.
        if (isNPC()) {
            result = BigDecimal.ZERO;
        }

        if (holder.money() != null) {
            result = holder.money();
        }
        if (result.compareTo(maxMoney) > 0) {
            result = maxMoney;
        }
        if (result.compareTo(minMoney) < 0) {
            result = minMoney;
        }
        holder.money(result);

        return holder.money();
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
        holder.money(money);
        stopTransaction();
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

    public Location getHome(final String name) {
        final String search = getHomeName(name);
        final LazyLocation loc = holder.homes().get(search);
        return loc != null ? loc.location() : null;
    }

    public Location getHome(final Location world) {
        if (getHomes().isEmpty()) {
            return null;
        }
        for (final String home : getHomes()) {
            final Location loc = holder.homes().get(home).location();
            if (loc != null && world.getWorld() == loc.getWorld()) {
                return loc;
            }

        }
        return holder.homes().get(getHomes().get(0)).location();
    }

    public List<String> getHomes() {
        return new ArrayList<>(holder.homes().keySet());
    }

    public void setHome(String name, final Location loc) {
        //Invalid names will corrupt the yaml
        name = StringUtil.safeString(name);
        holder.homes().put(name, LazyLocation.fromLocation(loc));
        config.save();
    }

    public void delHome(final String name) throws Exception {
        String search = getHomeName(name);
        if (!holder.homes().containsKey(search)) {
            search = StringUtil.safeString(search);
        }
        if (holder.homes().containsKey(search)) {
            holder.homes().remove(search);
            config.save();
        } else {
            throw new Exception(tl("invalidHome", search));
        }
    }

    public boolean hasHome() {
        return !holder.homes().isEmpty();
    }

    public boolean hasHome(final String name) {
        return holder.homes().containsKey(name);
    }

    public String getNickname() {
        return holder.nickname();
    }

    public void setNickname(final String nick) {
        holder.nickname(nick);
        config.save();
    }

    public Set<Material> getUnlimited() {
        return holder.unlimited();
    }

    public boolean hasUnlimited(final ItemStack stack) {
        return holder.unlimited().contains(stack.getType());
    }

    public void setUnlimited(final ItemStack stack, final boolean state) {
        final boolean wasUpdated;
        if (state) {
            wasUpdated = holder.unlimited().add(stack.getType());
        } else {
            wasUpdated = holder.unlimited().remove(stack.getType());
        }

        if (wasUpdated) {
            config.save();
        }
    }

    public void clearAllPowertools() {
        holder.powertools().clear();
        config.save();
    }

    public List<String> getPowertool(final ItemStack stack) {
        return getPowertool(stack.getType());
    }

    public List<String> getPowertool(final Material material) {
        return holder.powertools().get(material.name().toLowerCase(Locale.ENGLISH));
    }

    public void setPowertool(final ItemStack stack, final List<String> commandList) {
        if (commandList == null || commandList.isEmpty()) {
            holder.powertools().remove(stack.getType().name().toLowerCase(Locale.ENGLISH));
        } else {
            holder.powertools().put(stack.getType().name().toLowerCase(Locale.ENGLISH), commandList);
        }
        config.save();
    }

    public boolean hasPowerTools() {
        return !holder.powertools().isEmpty();
    }

    public Location getLastLocation() {
        final LazyLocation lastLocation = holder.lastLocation();
        return lastLocation != null ? lastLocation.location() : null;
    }

    public void setLastLocation(final Location loc) {
        if (loc == null || loc.getWorld() == null) {
            return;
        }
        holder.lastLocation(loc);
        config.save();
    }

    public Location getLogoutLocation() {
        return holder.logoutLocation().location();
    }

    public void setLogoutLocation(final Location loc) {
        if (loc == null || loc.getWorld() == null) {
            return;
        }
        holder.logoutLocation(loc);
        config.save();
    }

    public long getLastTeleportTimestamp() {
        return holder.timestamps().lastTeleport();
    }

    public void setLastTeleportTimestamp(final long time) {
        holder.timestamps().lastTeleport(time);
        config.save();
    }

    public long getLastHealTimestamp() {
        return holder.timestamps().lastHeal();
    }

    public void setLastHealTimestamp(final long time) {
        holder.timestamps().lastHeal(time);
        config.save();
    }

    public String getJail() {
        return holder.jail();
    }

    public void setJail(final String jail) {
        holder.jail(jail);
        config.save();
    }

    /**
     * @deprecated Mails are no longer just strings, this method is therefore misleading.
     */
    @Deprecated
    public List<String> getMails() {
        final List<String> list = new ArrayList<>();
        if (getMailAmount() != 0) {
            for (MailMessage mail : getMailMessages()) {
                // I hate this code btw
                list.add(mail.isLegacy() ? mail.getMessage() : ChatColor.GOLD + "[" + ChatColor.RESET + mail.getSenderUsername() + ChatColor.GOLD + "] " + ChatColor.RESET + mail.getMessage());
            }
        }
        return list;
    }

    /**
     * @deprecated This method does not support the new mail system and will fail at runtime.
     */
    @Deprecated
    public void setMails(List<String> mails) {
        throw new UnsupportedOperationException("UserData#setMails(List<String>) is deprecated and can no longer be used. Please tell the plugin author to update this!");
    }

    public int getMailAmount() {
        return holder.mail() == null ? 0 : holder.mail().size();
    }

    public int getUnreadMailAmount() {
        if (holder.mail() == null || holder.mail().isEmpty()) {
            return 0;
        }

        int unread = 0;
        for (MailMessage element : holder.mail()) {
            if (!element.isRead()) {
                unread++;
            }
        }
        return unread;
    }

    /**
     * @deprecated This method does not support the new mail system and should not be used.
     */
    @Deprecated
    abstract void addMail(final String mail);

    public ArrayList<MailMessage> getMailMessages() {
        return new ArrayList<>(holder.mail());
    }

    public void setMailList(ArrayList<MailMessage> messages) {
        holder.mail(messages);
        config.save();
    }

    public boolean isTeleportEnabled() {
        return holder.teleportEnabled();
    }

    public void setTeleportEnabled(final boolean set) {
        holder.teleportEnabled(set);
        config.save();
    }

    public boolean isAutoTeleportEnabled() {
        return holder.teleportAuto();
    }

    public void setAutoTeleportEnabled(final boolean set) {
        holder.teleportAuto(set);
        config.save();
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
        holder.ignore(players);
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
        return holder.ignore().contains(user.getBase().getUniqueId()) && !user.isIgnoreExempt();
    }

    public List<UUID> _getIgnoredPlayers() {
        return holder.ignore();
    }

    public void setIgnoredPlayer(final IUser user, final boolean set) {
        final UUID uuid = user.getBase().getUniqueId();
        if (set) {
            if (!holder.ignore().contains(uuid)) {
                holder.ignore().add(uuid);
            }
        } else {
            holder.ignore().remove(uuid);
        }
        config.save();
    }

    public boolean isGodModeEnabled() {
        return holder.godMode();
    }

    public void setGodModeEnabled(final boolean set) {
        holder.godMode(set);
        config.save();
    }

    public boolean getMuted() {
        return holder.muted();
    }

    public boolean isMuted() {
        return getMuted();
    }

    public void setMuted(final boolean set) {
        holder.muted(set);
        config.save();
    }

    public String getMuteReason() {
        return holder.muteReason();
    }

    public void setMuteReason(final String reason) {
        holder.muteReason(reason);
        config.save();
    }

    public boolean hasMuteReason() {
        return holder.muteReason() != null;
    }

    public long getMuteTimeout() {
        return holder.timestamps().mute();
    }

    public void setMuteTimeout(final long time) {
        holder.timestamps().mute(time);
        config.save();
    }

    public boolean isJailed() {
        return holder.jailed();
    }

    public void setJailed(final boolean set) {
        holder.jailed(set);
        config.save();
    }

    public boolean toggleJailed() {
        final boolean ret = !isJailed();
        setJailed(ret);
        return ret;
    }

    public long getJailTimeout() {
        return holder.timestamps().jail();
    }

    public void setJailTimeout(final long time) {
        holder.timestamps().jail(time);
        config.save();
    }

    public long getOnlineJailedTime() {
        return holder.timestamps().onlineJail();
    }

    public void setOnlineJailedTime(long onlineJailed) {
        holder.timestamps().onlineJail(onlineJailed);
        config.save();
    }

    public long getLastLogin() {
        return holder.timestamps().login();
    }

    public void setLastLogin(final long time) {
        holder.timestamps().login(time);
        if (base.getAddress() != null && base.getAddress().getAddress() != null) {
            holder.ipAddress(base.getAddress().getAddress().getHostAddress());
        }
        config.save();
    }

    public long getLastLogout() {
        return holder.timestamps().logout();
    }

    public void setLastLogout(final long time) {
        holder.timestamps().logout(time);
        config.save();
    }

    public String getLastLoginAddress() {
        return holder.ipAddress();
    }

    public boolean isAfk() {
        return holder.afk();
    }

    public void _setAfk(final boolean set) {
        holder.afk(set);
        config.save();
    }

    public String getGeoLocation() {
        return holder.geolocation();
    }

    public void setGeoLocation(final String geolocation) {
        holder.geolocation(geolocation);
        config.save();
    }

    public boolean isSocialSpyEnabled() {
        return holder.socialSpy();
    }

    public void setSocialSpyEnabled(final boolean status) {
        holder.socialSpy(status);
        config.save();
    }

    public boolean isNPC() {
        return holder.npc();
    }

    public void setNPC(final boolean set) {
        holder.npc(set);
        config.save();
    }

    public String getLastAccountName() {
        return holder.lastAccountName();
    }

    public void setLastAccountName(final String lastAccountName) {
        holder.lastAccountName(lastAccountName);
        config.save();
        ess.getUserMap().trackUUID(getConfigUUID(), lastAccountName, true);
    }

    public boolean arePowerToolsEnabled() {
        return holder.powerToolsEnabled();
    }

    public void setPowerToolsEnabled(final boolean set) {
        holder.powerToolsEnabled(set);
        config.save();
    }

    public boolean togglePowerToolsEnabled() {
        final boolean ret = !arePowerToolsEnabled();
        setPowerToolsEnabled(ret);
        return ret;
    }

    public long getKitTimestamp(String name) {
        name = name.replace('.', '_').replace('/', '_').toLowerCase(Locale.ENGLISH);
        if (holder.timestamps().kits() != null && holder.timestamps().kits().containsKey(name)) {
            return holder.timestamps().kits().get(name);
        }
        return 0L;
    }

    public void setKitTimestamp(String name, final long time) {
        name = name.replace('.', '_').replace('/', '_').toLowerCase(Locale.ENGLISH);
        holder.timestamps().kits().put(name, time);
        config.save();
    }

    public List<CommandCooldown> getCooldownsList() {
        return holder.timestamps().commandCooldowns();
    }

    public Map<Pattern, Long> getCommandCooldowns() {
        final Map<Pattern, Long> map = new HashMap<>();
        for (final CommandCooldown c : getCooldownsList()) {
            if (c == null) {
                // stupid solution to stupid problem
                continue;
            }
            map.put(c.pattern(), c.value());
        }
        return map;
    }

    public Date getCommandCooldownExpiry(final String label) {
        for (CommandCooldown cooldown : getCooldownsList()) {
            if (cooldown == null) {
                // stupid solution to stupid problem
                continue;
            }
            if (cooldown.pattern().matcher(label).matches()) {
                return new Date(cooldown.value());
            }
        }
        return null;
    }

    public void addCommandCooldown(final Pattern pattern, final Date expiresAt, final boolean save) {
        final CommandCooldown cooldown = new CommandCooldown();
        cooldown.pattern(pattern);
        cooldown.value(expiresAt.getTime());
        if (cooldown.isIncomplete()) {
            return;
        }
        holder.timestamps().commandCooldowns().add(cooldown);
        if (save) {
            save();
        }
    }

    public boolean clearCommandCooldown(final Pattern pattern) {
        if (holder.timestamps().commandCooldowns().isEmpty()) {
            return false; // false for no modification
        }

        if (getCooldownsList().removeIf(cooldown -> cooldown.pattern().equals(pattern))) {
            save();
            return true;
        }
        return false;
    }

    public boolean isAcceptingPay() {
        return holder.acceptingPay();
    }

    public void setAcceptingPay(final boolean acceptingPay) {
        holder.acceptingPay(acceptingPay);
        save();
    }

    public boolean isPromptingPayConfirm() {
        return holder.confirmPay() != null ? holder.confirmPay() : ess.getSettings().isConfirmCommandEnabledByDefault("pay");
    }

    public void setPromptingPayConfirm(final boolean prompt) {
        holder.confirmPay(prompt);
        save();
    }

    public boolean isPromptingClearConfirm() {
        return holder.confirmClear() != null ? holder.confirmClear() : ess.getSettings().isConfirmCommandEnabledByDefault("clearinventory");
    }

    public void setPromptingClearConfirm(final boolean prompt) {
        holder.confirmClear(prompt);
        save();
    }

    public boolean isLastMessageReplyRecipient() {
        return holder.lastMessageReplyRecipient() != null ? holder.lastMessageReplyRecipient() : ess.getSettings().isLastMessageReplyRecipient();
    }

    public void setLastMessageReplyRecipient(final boolean enabled) {
        holder.lastMessageReplyRecipient(enabled);
        save();
    }

    public boolean isBaltopExcludeCache() {
        return holder.baltopExempt();
    }

    public void setBaltopExemptCache(boolean baltopExempt) {
        holder.baltopExempt(baltopExempt);
        config.save();
    }

    public UUID getConfigUUID() {
        return config.getUuid();
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

    public void setConfigProperty(String node, Object object) {
        setConfigPropertyRaw("info." + node, object);
    }

    public void setConfigPropertyRaw(String node, Object object) {
        config.setRaw(node, object);
        config.save();
    }

    public Set<String> getConfigKeys() {
        return ConfigurateUtil.getKeys(config.getSection("info"));
    }

    public Map<String, Object> getConfigMap() {
        return ConfigurateUtil.getRawMap(config.getSection("info"));
    }

    public Map<String, Object> getConfigMap(final String node) {
        return ConfigurateUtil.getRawMap(config.getSection("info." + node));
    }
}
