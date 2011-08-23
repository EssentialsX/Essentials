package com.earth2me.essentials;

import com.earth2me.essentials.commands.IEssentialsCommand;
import java.net.InetSocketAddress;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;


public interface IUser
{
	int getHealth();

	Location getLocation();

	boolean isOnline();

	void sendMessage(String string);

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

	PlayerInventory getInventory();

	void updateInventory();

	String getGroup();

	void setLastLocation();

	Location getHome(String name);

	Location getHome(Location loc);

	String getName();

	InetSocketAddress getAddress();

	String getDisplayName();
}
