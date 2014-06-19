package com.earth2me.essentials;

import com.earth2me.essentials.commands.IEssentialsCommand;
import com.earth2me.essentials.signs.EssentialsSign;
import com.earth2me.essentials.textreader.IText;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventPriority;


public interface ISettings extends IConf
{
	boolean areSignsDisabled();

	IText getAnnounceNewPlayerFormat();

	boolean getAnnounceNewPlayers();

	String getNewPlayerKit();

	String getBackupCommand();

	long getBackupInterval();

	String getChatFormat(String group);

	int getChatRadius();
	
	char getChatShout();
	
	char getChatQuestion();

	BigDecimal getCommandCost(IEssentialsCommand cmd);

	BigDecimal getCommandCost(String label);

	String getCurrencySymbol();

	int getOversizedStackSize();

	int getDefaultStackSize();

	double getHealCooldown();

	Set<String> getSocialSpyCommands();

	Map<String, Object> getKit(String name);

	ConfigurationSection getKits();

	String getLocale();

	String getNewbieSpawn();

	String getNicknamePrefix();

	ChatColor getOperatorColor() throws Exception;

	boolean getPerWarpPermission();

	boolean getProtectBoolean(final String configName, boolean def);

	int getProtectCreeperMaxHeight();

	List<Integer> getProtectList(final String configName);

	boolean getProtectPreventSpawn(final String creatureName);

	String getProtectString(final String configName);

	boolean getRespawnAtHome();

	Set getMultipleHomes();

	int getHomeLimit(String set);

	int getHomeLimit(User user);

	int getSpawnMobLimit();

	BigDecimal getStartingBalance();

	boolean isTeleportSafetyEnabled();

	double getTeleportCooldown();

	double getTeleportDelay();

	boolean hidePermissionlessHelp();

	boolean isCommandDisabled(final IEssentialsCommand cmd);

	boolean isCommandDisabled(String label);

	boolean isCommandOverridden(String name);

	boolean isDebug();

	boolean isEcoDisabled();

	boolean isTradeInStacks(int id);

	List<Integer> itemSpawnBlacklist();

	List<EssentialsSign> enabledSigns();

	boolean permissionBasedItemSpawn();

	boolean showNonEssCommandsInHelp();

	boolean warnOnBuildDisallow();

	boolean warnOnSmite();

	BigDecimal getMaxMoney();

	BigDecimal getMinMoney();

	boolean isEcoLogEnabled();

	boolean isEcoLogUpdateEnabled();

	boolean removeGodOnDisconnect();

	boolean changeDisplayName();

	boolean changePlayerListName();

	boolean isPlayerCommand(String string);

	boolean useBukkitPermissions();

	boolean addPrefixSuffix();

	boolean disablePrefix();

	boolean disableSuffix();

	long getAutoAfk();

	long getAutoAfkKick();

	boolean getFreezeAfkPlayers();

	boolean cancelAfkOnMove();

	boolean cancelAfkOnInteract();

	boolean areDeathMessagesEnabled();

	void setDebug(boolean debug);

	Set<String> getNoGodWorlds();

	boolean getUpdateBedAtDaytime();

	boolean allowUnsafeEnchantments();

	boolean getRepairEnchanted();

	boolean isWorldTeleportPermissions();

	boolean isWorldHomePermissions();

	boolean registerBackInListener();

	boolean getDisableItemPickupWhileAfk();

	EventPriority getRespawnPriority();

	long getTpaAcceptCancellation();

	boolean isMetricsEnabled();

	void setMetricsEnabled(boolean metricsEnabled);

	long getTeleportInvulnerability();

	boolean isTeleportInvulnerability();

	long getLoginAttackDelay();

	int getSignUsePerSecond();

	double getMaxFlySpeed();

	double getMaxWalkSpeed();

	int getMailsPerMinute();

	long getEconomyLagWarning();
	
	long getPermissionsLagWarning();

	void setEssentialsChatActive(boolean b);

	long getMaxTempban();

	Map<String, Object> getListGroupConfig();

	int getMaxNickLength();

	int getMaxUserCacheCount();

	boolean allowSilentJoinQuit();

	boolean isCustomJoinMessage();

	String getCustomJoinMessage();

	boolean isCustomQuitMessage();

	String getCustomQuitMessage();
}
