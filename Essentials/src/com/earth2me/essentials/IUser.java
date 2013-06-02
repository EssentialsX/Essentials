package com.earth2me.essentials;

import com.earth2me.essentials.commands.IEssentialsCommand;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.entity.Player;


public interface IUser extends Player
{
	long getLastTeleportTimestamp();

	boolean isAuthorized(String node);

	boolean isAuthorized(IEssentialsCommand cmd);

	boolean isAuthorized(IEssentialsCommand cmd, String permissionPrefix);

	void setLastTeleportTimestamp(long time);

	Location getLastLocation();

	Player getBase();

	BigDecimal getMoney();

	void takeMoney(BigDecimal value);

	void giveMoney(BigDecimal value);
	
	boolean canAfford(BigDecimal value);

	String getGroup();

	void setLastLocation();

	Location getHome(String name) throws Exception;

	Location getHome(Location loc) throws Exception;

	/**
	 * 'Hidden' Represents when a player is hidden from others.
	 * This status includes when the player is hidden via other supported plugins.
	 * Use isVanished() if you want to check if a user is vanished by Essentials.
	 * 
	 * @return If the user is hidden or not
	 * @see isVanished
	 */
	
	boolean isHidden();
	
	void setHidden(boolean vanish);
	
	/**
	 * 'Vanished' Represents when a player is hidden from others by Essentials.
	 * This status does NOT include when the player is hidden via other plugins.
	 * Use isHidden() if you want to check if a user is vanished by any supported plugin.
	 * 
	 * @return If the user is vanished or not
	 * @see isHidden
	 */
	
	boolean isVanished();
	
	void setVanished(boolean vanish);

	Teleport getTeleport();

	void setJail(String jail);
	
	boolean isIgnoreExempt();

	boolean isAfk();

	void setAfk(final boolean set);
	
	void setLogoutLocation();
	
	Location getLogoutLocation();
	
	void setConfigProperty(String node, Object object);
	
	Set<String> getConfigKeys();
	
	Map<String, Object> getConfigMap();
	
	Map<String, Object> getConfigMap(String node);
}
