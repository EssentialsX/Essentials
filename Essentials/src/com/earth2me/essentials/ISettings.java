package com.earth2me.essentials;

import com.earth2me.essentials.commands.IEssentialsCommand;
import com.earth2me.essentials.signs.EssentialsSign;
import com.earth2me.essentials.textreader.IText;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventPriority;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;


/**
 * <p>ISettings interface.</p>
 *
 * @author LoopyD
 * @version $Id: $Id
 */
public interface ISettings extends IConf {
    /**
     * <p>areSignsDisabled.</p>
     *
     * @return a boolean.
     */
    boolean areSignsDisabled();

    /**
     * <p>getAnnounceNewPlayerFormat.</p>
     *
     * @return a {@link com.earth2me.essentials.textreader.IText} object.
     */
    IText getAnnounceNewPlayerFormat();

    /**
     * <p>getAnnounceNewPlayers.</p>
     *
     * @return a boolean.
     */
    boolean getAnnounceNewPlayers();

    /**
     * <p>getNewPlayerKit.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    String getNewPlayerKit();

    /**
     * <p>getBackupCommand.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    String getBackupCommand();

    /**
     * <p>getBackupInterval.</p>
     *
     * @return a long.
     */
    long getBackupInterval();

    /**
     * <p>getChatFormat.</p>
     *
     * @param group a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    String getChatFormat(String group);

    /**
     * <p>getChatRadius.</p>
     *
     * @return a int.
     */
    int getChatRadius();

    /**
     * <p>getNearRadius.</p>
     *
     * @return a int.
     */
    int getNearRadius();

    /**
     * <p>getChatShout.</p>
     *
     * @return a char.
     */
    char getChatShout();

    /**
     * <p>getChatQuestion.</p>
     *
     * @return a char.
     */
    char getChatQuestion();

    /**
     * <p>getCommandCost.</p>
     *
     * @param cmd a {@link com.earth2me.essentials.commands.IEssentialsCommand} object.
     * @return a {@link java.math.BigDecimal} object.
     */
    BigDecimal getCommandCost(IEssentialsCommand cmd);

    /**
     * <p>getCommandCost.</p>
     *
     * @param label a {@link java.lang.String} object.
     * @return a {@link java.math.BigDecimal} object.
     */
    BigDecimal getCommandCost(String label);

    /**
     * <p>getCurrencySymbol.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    String getCurrencySymbol();

    /**
     * <p>getOversizedStackSize.</p>
     *
     * @return a int.
     */
    int getOversizedStackSize();

    /**
     * <p>getDefaultStackSize.</p>
     *
     * @return a int.
     */
    int getDefaultStackSize();

    /**
     * <p>getHealCooldown.</p>
     *
     * @return a double.
     */
    double getHealCooldown();

    /**
     * <p>getSocialSpyCommands.</p>
     *
     * @return a {@link java.util.Set} object.
     */
    Set<String> getSocialSpyCommands();

    /**
     * <p>getSocialSpyListenMutedPlayers.</p>
     *
     * @return a boolean.
     */
    boolean getSocialSpyListenMutedPlayers();

    /**
     * <p>getMuteCommands.</p>
     *
     * @return a {@link java.util.Set} object.
     */
    Set<String> getMuteCommands();

    /**
     * <p>getKits.</p>
     *
     * @deprecated in favor of {@link com.earth2me.essentials.Kits#getKits()}
     * @return a {@link org.bukkit.configuration.ConfigurationSection} object.
     */
    @Deprecated
    ConfigurationSection getKits();

    /**
     * <p>getKit.</p>
     *
     * @deprecated in favor of {@link com.earth2me.essentials.Kits#getKit(String)}
     * @param kit a {@link java.lang.String} object.
     * @return a {@link java.util.Map} object.
     */
    @Deprecated
    Map<String, Object> getKit(String kit);

    /**
     * <p>addKit.</p>
     *
     * @deprecated in favor of {@link com.earth2me.essentials.Kits#addKit(String, List, long)}}
     * @param name a {@link java.lang.String} object.
     * @param lines a {@link java.util.List} object.
     * @param delay a long.
     */
    @Deprecated
    void addKit(String name, List<String> lines, long delay);

    /**
     * <p>getKitSection.</p>
     *
     * @return a {@link org.bukkit.configuration.ConfigurationSection} object.
     */
    @Deprecated
    ConfigurationSection getKitSection();

    /**
     * <p>isSkippingUsedOneTimeKitsFromKitList.</p>
     *
     * @return a boolean.
     */
    boolean isSkippingUsedOneTimeKitsFromKitList();

    /**
     * <p>getLocale.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    String getLocale();

    /**
     * <p>getNewbieSpawn.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    String getNewbieSpawn();

    /**
     * <p>getNicknamePrefix.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    String getNicknamePrefix();

    /**
     * <p>getOperatorColor.</p>
     *
     * @return a {@link org.bukkit.ChatColor} object.
     * @throws java.lang.Exception if any.
     */
    ChatColor getOperatorColor() throws Exception;

    /**
     * <p>getPerWarpPermission.</p>
     *
     * @return a boolean.
     */
    boolean getPerWarpPermission();

    /**
     * <p>getProtectBoolean.</p>
     *
     * @param configName a {@link java.lang.String} object.
     * @param def a boolean.
     * @return a boolean.
     */
    boolean getProtectBoolean(final String configName, boolean def);

    /**
     * <p>getProtectCreeperMaxHeight.</p>
     *
     * @return a int.
     */
    int getProtectCreeperMaxHeight();

    /**
     * <p>getProtectList.</p>
     *
     * @param configName a {@link java.lang.String} object.
     * @return a {@link java.util.List} object.
     */
    List<Material> getProtectList(final String configName);

    /**
     * <p>getProtectPreventSpawn.</p>
     *
     * @param creatureName a {@link java.lang.String} object.
     * @return a boolean.
     */
    boolean getProtectPreventSpawn(final String creatureName);

    /**
     * <p>getProtectString.</p>
     *
     * @param configName a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    String getProtectString(final String configName);

    /**
     * <p>getRespawnAtHome.</p>
     *
     * @return a boolean.
     */
    boolean getRespawnAtHome();

    /**
     * <p>getMultipleHomes.</p>
     *
     * @return a {@link java.util.Set} object.
     */
    Set getMultipleHomes();

    /**
     * <p>getHomeLimit.</p>
     *
     * @param set a {@link java.lang.String} object.
     * @return a int.
     */
    int getHomeLimit(String set);

    /**
     * <p>getHomeLimit.</p>
     *
     * @param user a {@link com.earth2me.essentials.User} object.
     * @return a int.
     */
    int getHomeLimit(User user);

    /**
     * <p>getSpawnMobLimit.</p>
     *
     * @return a int.
     */
    int getSpawnMobLimit();

    /**
     * <p>getStartingBalance.</p>
     *
     * @return a {@link java.math.BigDecimal} object.
     */
    BigDecimal getStartingBalance();

    /**
     * <p>isTeleportSafetyEnabled.</p>
     *
     * @return a boolean.
     */
    boolean isTeleportSafetyEnabled();

    /**
     * <p>isForceDisableTeleportSafety.</p>
     *
     * @return a boolean.
     */
    boolean isForceDisableTeleportSafety();

    /**
     * <p>getTeleportCooldown.</p>
     *
     * @return a double.
     */
    double getTeleportCooldown();

    /**
     * <p>getTeleportDelay.</p>
     *
     * @return a double.
     */
    double getTeleportDelay();

    /**
     * <p>hidePermissionlessHelp.</p>
     *
     * @return a boolean.
     */
    boolean hidePermissionlessHelp();

    /**
     * <p>isCommandDisabled.</p>
     *
     * @param cmd a {@link com.earth2me.essentials.commands.IEssentialsCommand} object.
     * @return a boolean.
     */
    boolean isCommandDisabled(final IEssentialsCommand cmd);

    /**
     * <p>isCommandDisabled.</p>
     *
     * @param label a {@link java.lang.String} object.
     * @return a boolean.
     */
    boolean isCommandDisabled(String label);

    /**
     * <p>isCommandOverridden.</p>
     *
     * @param name a {@link java.lang.String} object.
     * @return a boolean.
     */
    boolean isCommandOverridden(String name);

    /**
     * <p>isDebug.</p>
     *
     * @return a boolean.
     */
    boolean isDebug();

    /**
     * <p>isEcoDisabled.</p>
     *
     * @return a boolean.
     */
    boolean isEcoDisabled();

    /**
     * <p>isTradeInStacks.</p>
     *
     * @param id a int.
     * @return a boolean.
     */
    @Deprecated
    boolean isTradeInStacks(int id);

    /**
     * <p>isTradeInStacks.</p>
     *
     * @param type a {@link org.bukkit.Material} object.
     * @return a boolean.
     */
    boolean isTradeInStacks(Material type);

    /**
     * <p>itemSpawnBlacklist.</p>
     *
     * @return a {@link java.util.List} object.
     */
    List<Material> itemSpawnBlacklist();

    /**
     * <p>enabledSigns.</p>
     *
     * @return a {@link java.util.List} object.
     */
    List<EssentialsSign> enabledSigns();

    /**
     * <p>permissionBasedItemSpawn.</p>
     *
     * @return a boolean.
     */
    boolean permissionBasedItemSpawn();

    /**
     * <p>showNonEssCommandsInHelp.</p>
     *
     * @return a boolean.
     */
    boolean showNonEssCommandsInHelp();

    /**
     * <p>warnOnBuildDisallow.</p>
     *
     * @return a boolean.
     */
    boolean warnOnBuildDisallow();

    /**
     * <p>warnOnSmite.</p>
     *
     * @return a boolean.
     */
    boolean warnOnSmite();

    /**
     * <p>getMaxMoney.</p>
     *
     * @return a {@link java.math.BigDecimal} object.
     */
    BigDecimal getMaxMoney();

    /**
     * <p>getMinMoney.</p>
     *
     * @return a {@link java.math.BigDecimal} object.
     */
    BigDecimal getMinMoney();

    /**
     * <p>isEcoLogEnabled.</p>
     *
     * @return a boolean.
     */
    boolean isEcoLogEnabled();

    /**
     * <p>isEcoLogUpdateEnabled.</p>
     *
     * @return a boolean.
     */
    boolean isEcoLogUpdateEnabled();

    /**
     * <p>removeGodOnDisconnect.</p>
     *
     * @return a boolean.
     */
    boolean removeGodOnDisconnect();

    /**
     * <p>changeDisplayName.</p>
     *
     * @return a boolean.
     */
    boolean changeDisplayName();

    /**
     * <p>changePlayerListName.</p>
     *
     * @return a boolean.
     */
    boolean changePlayerListName();

    /**
     * <p>isPlayerCommand.</p>
     *
     * @param string a {@link java.lang.String} object.
     * @return a boolean.
     */
    boolean isPlayerCommand(String string);

    /**
     * <p>useBukkitPermissions.</p>
     *
     * @return a boolean.
     */
    boolean useBukkitPermissions();

    /**
     * <p>addPrefixSuffix.</p>
     *
     * @return a boolean.
     */
    boolean addPrefixSuffix();

    /**
     * <p>disablePrefix.</p>
     *
     * @return a boolean.
     */
    boolean disablePrefix();

    /**
     * <p>disableSuffix.</p>
     *
     * @return a boolean.
     */
    boolean disableSuffix();

    /**
     * <p>getAutoAfk.</p>
     *
     * @return a long.
     */
    long getAutoAfk();

    /**
     * <p>getAutoAfkKick.</p>
     *
     * @return a long.
     */
    long getAutoAfkKick();

    /**
     * <p>getFreezeAfkPlayers.</p>
     *
     * @return a boolean.
     */
    boolean getFreezeAfkPlayers();

    /**
     * <p>cancelAfkOnMove.</p>
     *
     * @return a boolean.
     */
    boolean cancelAfkOnMove();

    /**
     * <p>cancelAfkOnInteract.</p>
     *
     * @return a boolean.
     */
    boolean cancelAfkOnInteract();

    /**
     * <p>sleepIgnoresAfkPlayers.</p>
     *
     * @return a boolean.
     */
    boolean sleepIgnoresAfkPlayers();

    /**
     * <p>isAfkListName.</p>
     *
     * @return a boolean.
     */
    boolean isAfkListName();

    /**
     * <p>getAfkListName.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    String getAfkListName();

    /**
     * <p>areDeathMessagesEnabled.</p>
     *
     * @return a boolean.
     */
    boolean areDeathMessagesEnabled();

    /**
     * <p>setDebug.</p>
     *
     * @param debug a boolean.
     */
    void setDebug(boolean debug);

    /**
     * <p>getNoGodWorlds.</p>
     *
     * @return a {@link java.util.Set} object.
     */
    Set<String> getNoGodWorlds();

    /**
     * <p>getUpdateBedAtDaytime.</p>
     *
     * @return a boolean.
     */
    boolean getUpdateBedAtDaytime();

    /**
     * <p>allowUnsafeEnchantments.</p>
     *
     * @return a boolean.
     */
    boolean allowUnsafeEnchantments();

    /**
     * <p>getRepairEnchanted.</p>
     *
     * @return a boolean.
     */
    boolean getRepairEnchanted();

    /**
     * <p>isWorldTeleportPermissions.</p>
     *
     * @return a boolean.
     */
    boolean isWorldTeleportPermissions();

    /**
     * <p>isWorldHomePermissions.</p>
     *
     * @return a boolean.
     */
    boolean isWorldHomePermissions();

    /**
     * <p>registerBackInListener.</p>
     *
     * @return a boolean.
     */
    boolean registerBackInListener();

    /**
     * <p>getDisableItemPickupWhileAfk.</p>
     *
     * @return a boolean.
     */
    boolean getDisableItemPickupWhileAfk();

    /**
     * <p>getRespawnPriority.</p>
     *
     * @return a {@link org.bukkit.event.EventPriority} object.
     */
    EventPriority getRespawnPriority();

    /**
     * <p>getSpawnJoinPriority.</p>
     *
     * @return a {@link org.bukkit.event.EventPriority} object.
     */
    EventPriority getSpawnJoinPriority();

    /**
     * <p>getTpaAcceptCancellation.</p>
     *
     * @return a long.
     */
    long getTpaAcceptCancellation();

    /**
     * <p>getTeleportInvulnerability.</p>
     *
     * @return a long.
     */
    long getTeleportInvulnerability();

    /**
     * <p>isTeleportInvulnerability.</p>
     *
     * @return a boolean.
     */
    boolean isTeleportInvulnerability();

    /**
     * <p>getLoginAttackDelay.</p>
     *
     * @return a long.
     */
    long getLoginAttackDelay();

    /**
     * <p>getSignUsePerSecond.</p>
     *
     * @return a int.
     */
    int getSignUsePerSecond();

    /**
     * <p>getMaxFlySpeed.</p>
     *
     * @return a double.
     */
    double getMaxFlySpeed();

    /**
     * <p>getMaxWalkSpeed.</p>
     *
     * @return a double.
     */
    double getMaxWalkSpeed();

    /**
     * <p>getMailsPerMinute.</p>
     *
     * @return a int.
     */
    int getMailsPerMinute();

    /**
     * <p>getEconomyLagWarning.</p>
     *
     * @return a long.
     */
    long getEconomyLagWarning();

    /**
     * <p>getPermissionsLagWarning.</p>
     *
     * @return a long.
     */
    long getPermissionsLagWarning();

    /**
     * <p>setEssentialsChatActive.</p>
     *
     * @param b a boolean.
     */
    void setEssentialsChatActive(boolean b);

    /**
     * <p>getMaxTempban.</p>
     *
     * @return a long.
     */
    long getMaxTempban();

    /**
     * <p>getListGroupConfig.</p>
     *
     * @return a {@link java.util.Map} object.
     */
    Map<String, Object> getListGroupConfig();

    /**
     * <p>getMaxNickLength.</p>
     *
     * @return a int.
     */
    int getMaxNickLength();

    /**
     * <p>ignoreColorsInMaxLength.</p>
     *
     * @return a boolean.
     */
    boolean ignoreColorsInMaxLength();

    /**
     * <p>hideDisplayNameInVanish.</p>
     *
     * @return a boolean.
     */
    boolean hideDisplayNameInVanish();

    /**
     * <p>getMaxUserCacheCount.</p>
     *
     * @return a int.
     */
    int getMaxUserCacheCount();

    /**
     * <p>allowSilentJoinQuit.</p>
     *
     * @return a boolean.
     */
    boolean allowSilentJoinQuit();

    /**
     * <p>isCustomJoinMessage.</p>
     *
     * @return a boolean.
     */
    boolean isCustomJoinMessage();

    /**
     * <p>getCustomJoinMessage.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    String getCustomJoinMessage();

    /**
     * <p>isCustomQuitMessage.</p>
     *
     * @return a boolean.
     */
    boolean isCustomQuitMessage();

    /**
     * <p>getCustomQuitMessage.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    String getCustomQuitMessage();

    /**
     * <p>isNotifyNoNewMail.</p>
     *
     * @return a boolean.
     */
    boolean isNotifyNoNewMail();

    /**
     * <p>isDropItemsIfFull.</p>
     *
     * @return a boolean.
     */
    boolean isDropItemsIfFull();

    /**
     * <p>isLastMessageReplyRecipient.</p>
     *
     * @return a boolean.
     */
    boolean isLastMessageReplyRecipient();

    /**
     * <p>getMinimumPayAmount.</p>
     *
     * @return a {@link java.math.BigDecimal} object.
     */
    BigDecimal getMinimumPayAmount();

    /**
     * <p>getLastMessageReplyRecipientTimeout.</p>
     *
     * @return a long.
     */
    long getLastMessageReplyRecipientTimeout();

    /**
     * <p>isMilkBucketEasterEggEnabled.</p>
     *
     * @return a boolean.
     */
    boolean isMilkBucketEasterEggEnabled();

    /**
     * <p>isSendFlyEnableOnJoin.</p>
     *
     * @return a boolean.
     */
    boolean isSendFlyEnableOnJoin();

    /**
     * <p>isWorldTimePermissions.</p>
     *
     * @return a boolean.
     */
    boolean isWorldTimePermissions();

    /**
     * <p>isSpawnOnJoin.</p>
     *
     * @return a boolean.
     */
    boolean isSpawnOnJoin();

    /**
     * <p>getSpawnOnJoinGroups.</p>
     *
     * @return a {@link java.util.List} object.
     */
    List<String> getSpawnOnJoinGroups();

    /**
     * <p>isUserInSpawnOnJoinGroup.</p>
     *
     * @param user a {@link com.earth2me.essentials.IUser} object.
     * @return a boolean.
     */
    boolean isUserInSpawnOnJoinGroup(IUser user);

    /**
     * <p>isTeleportToCenterLocation.</p>
     *
     * @return a boolean.
     */
    boolean isTeleportToCenterLocation();

    /**
     * <p>isCommandCooldownsEnabled.</p>
     *
     * @return a boolean.
     */
    boolean isCommandCooldownsEnabled();

    /**
     * <p>getCommandCooldownMs.</p>
     *
     * @param label a {@link java.lang.String} object.
     * @return a long.
     */
    long getCommandCooldownMs(String label);

    /**
     * <p>getCommandCooldownEntry.</p>
     *
     * @param label a {@link java.lang.String} object.
     * @return a {@link java.util.Map.Entry} object.
     */
    Entry<Pattern, Long> getCommandCooldownEntry(String label);

    /**
     * <p>isCommandCooldownPersistent.</p>
     *
     * @param label a {@link java.lang.String} object.
     * @return a boolean.
     */
    boolean isCommandCooldownPersistent(String label);

    /**
     * <p>isNpcsInBalanceRanking.</p>
     *
     * @return a boolean.
     */
    boolean isNpcsInBalanceRanking();

    /**
     * <p>getCurrencyFormat.</p>
     *
     * @return a {@link java.text.NumberFormat} object.
     */
    NumberFormat getCurrencyFormat();

    /**
     * <p>getUnprotectedSignNames.</p>
     *
     * @return a {@link java.util.List} object.
     */
    List<EssentialsSign> getUnprotectedSignNames();

    /**
     * <p>isPastebinCreateKit.</p>
     *
     * @return a boolean.
     */
    boolean isPastebinCreateKit();

    /**
     * <p>isAllowBulkBuySell.</p>
     *
     * @return a boolean.
     */
    boolean isAllowBulkBuySell();

    /**
     * <p>isAddingPrefixInPlayerlist.</p>
     *
     * @return a boolean.
     */
    boolean isAddingPrefixInPlayerlist();

    /**
     * <p>isAddingSuffixInPlayerlist.</p>
     *
     * @return a boolean.
     */
    boolean isAddingSuffixInPlayerlist();

    /**
     * <p>getNotifyPlayerOfMailCooldown.</p>
     *
     * @return a int.
     */
    int getNotifyPlayerOfMailCooldown();

    /**
     * <p>getMotdDelay.</p>
     *
     * @return a int.
     */
    int getMotdDelay();

    /**
     * <p>isDirectHatAllowed.</p>
     *
     * @return a boolean.
     */
    boolean isDirectHatAllowed();

    /**
     * <p>getDefaultEnabledConfirmCommands.</p>
     *
     * @return a {@link java.util.List} object.
     */
    List<String> getDefaultEnabledConfirmCommands();

    /**
     * <p>isConfirmCommandEnabledByDefault.</p>
     *
     * @param commandName a {@link java.lang.String} object.
     * @return a boolean.
     */
    boolean isConfirmCommandEnabledByDefault(String commandName);

    /**
     * <p>isTeleportBackWhenFreedFromJail.</p>
     *
     * @return a boolean.
     */
    boolean isTeleportBackWhenFreedFromJail();

    /**
     * <p>isCompassTowardsHomePerm.</p>
     *
     * @return a boolean.
     */
    boolean isCompassTowardsHomePerm();

    /**
     * <p>isAllowWorldInBroadcastworld.</p>
     *
     * @return a boolean.
     */
    boolean isAllowWorldInBroadcastworld();

    /**
     * <p>getItemDbType.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    String getItemDbType();

    /**
     * <p>isForceEnableRecipe.</p>
     *
     * @return a boolean.
     */
    boolean isForceEnableRecipe();

    /**
     * <p>allowOldIdSigns.</p>
     *
     * @return a boolean.
     */
    boolean allowOldIdSigns();

    /**
     * <p>isWaterSafe.</p>
     *
     * @return a boolean.
     */
    boolean isWaterSafe();
  
    /**
     * <p>isSafeUsermap.</p>
     *
     * @return a boolean.
     */
    boolean isSafeUsermap();
}
