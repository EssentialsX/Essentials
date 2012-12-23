package com.earth2me.essentials;

import com.earth2me.essentials.commands.IEssentialsCommand;
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

	double getMoney();

	void takeMoney(double value);

	void giveMoney(double value);
	
	boolean canAfford(double value);

	String getGroup();

	void setLastLocation();

	Location getHome(String name) throws Exception;

	Location getHome(Location loc) throws Exception;

	boolean isHidden();

	Teleport getTeleport();

	void setJail(String jail);
	
	boolean isIgnoreExempt();

	boolean isAfk();

	void setAfk();
}
