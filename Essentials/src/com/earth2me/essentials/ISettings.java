package com.earth2me.essentials;

import com.earth2me.essentials.commands.IEssentialsCommand;
import java.util.List;
import java.util.Map;
import org.bukkit.ChatColor;


public interface ISettings extends IConf
{

	boolean areSignsDisabled();

	String format(String format, IUser user);

	String getAnnounceNewPlayerFormat(IUser user);

	boolean getAnnounceNewPlayers();

	String getBackupCommand();

	long getBackupInterval();

	boolean getBedSetsHome();

	String getChatFormat(String group);

	int getChatRadius();

	double getCommandCost(IEssentialsCommand cmd);

	double getCommandCost(String label);

	String getCurrencySymbol();

	int getDefaultStackSize();

	boolean getGenerateExitPortals();

	double getHealCooldown();

	Object getKit(String name);

	Map<String, Object> getKits();

	String getLocale();

	String getNetherName();

	boolean getNetherPortalsEnabled();

	double getNetherRatio();

	String getNewbieSpawn();

	String getNicknamePrefix();

	ChatColor getOperatorColor() throws Exception;

	boolean getPerWarpPermission();

	boolean getProtectBoolean(final String configName, boolean def);

	int getProtectCreeperMaxHeight();

	List<Integer> getProtectList(final String configName);

	boolean getProtectPreventSpawn(final String creatureName);

	String getProtectString(final String configName);

	boolean getReclaimSetting();

	boolean getRespawnAtHome();

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

	boolean isNetherEnabled();

	boolean isTradeInStacks(int id);

	List<Integer> itemSpawnBlacklist();

	boolean permissionBasedItemSpawn();

	void reloadConfig();

	boolean showNonEssCommandsInHelp();

	boolean spawnIfNoHome();

	boolean use1to1RatioInNether();

	boolean warnOnBuildDisallow();

	boolean warnOnSmite();
	
	double getMaxMoney();

	boolean isEcoLogEnabled();
	
	boolean removeGodOnDisconnect();

	boolean changeDisplayName();
}
