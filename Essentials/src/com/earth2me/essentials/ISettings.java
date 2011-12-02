package com.earth2me.essentials;

import com.earth2me.essentials.commands.IEssentialsCommand;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.ChatColor;


public interface ISettings extends IConf
{
	boolean areSignsDisabled();

	String format(String format, IUser user);

	String getAnnounceNewPlayerFormat(IUser user);

	boolean getAnnounceNewPlayers();

	String getBackupCommand();

	long getBackupInterval();

	String getChatFormat(String group);

	int getChatRadius();

	double getCommandCost(IEssentialsCommand cmd);

	double getCommandCost(String label);

	String getCurrencySymbol();

	int getOversizedStackSize();

	double getHealCooldown();

	Object getKit(String name);

	Map<String, Object> getKits();

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

	List getMultipleHomes();

	int getHomeLimit(String set);

	int getHomeLimit(User user);

	boolean getSortListByGroups();

	int getSpawnMobLimit();

	int getStartingBalance();

	double getTeleportCooldown();

	double getTeleportDelay();

	boolean hidePermissionlessHelp();

	boolean isCommandDisabled(final IEssentialsCommand cmd);

	boolean isCommandDisabled(String label);

	boolean isCommandOverridden(String name);

	boolean isCommandRestricted(IEssentialsCommand cmd);

	boolean isCommandRestricted(String label);

	boolean isDebug();

	boolean isEcoDisabled();

	boolean isTradeInStacks(int id);

	List<Integer> itemSpawnBlacklist();

	boolean permissionBasedItemSpawn();

	boolean showNonEssCommandsInHelp();

	boolean spawnIfNoHome();

	boolean warnOnBuildDisallow();

	boolean warnOnSmite();

	double getMaxMoney();

	boolean isEcoLogEnabled();

	boolean removeGodOnDisconnect();

	boolean changeDisplayName();

	boolean isPlayerCommand(String string);

	boolean useBukkitPermissions();

	boolean addPrefixSuffix();

	boolean disablePrefix();

	boolean disableSuffix();

	long getAutoAfk();

	long getAutoAfkKick();

	boolean getFreezeAfkPlayers();

	boolean areDeathMessagesEnabled();

	public void setDebug(boolean debug);

	Set<String> getNoGodWorlds();
	
	boolean getUpdateBedAtDaytime();
	
	boolean getRepairEnchanted();
}
