package com.earth2me.essentials.api;

import com.earth2me.essentials.commands.IEssentialsCommand;
import com.earth2me.essentials.storage.IStorageObjectHolder;
import com.earth2me.essentials.user.CooldownException;
import com.earth2me.essentials.user.UserData;
import org.bukkit.Location;
import org.bukkit.entity.Player;


public interface IUser extends Player, IStorageObjectHolder<UserData>, IReload, IReplyTo
{
	boolean isAuthorized(String node);

	boolean isAuthorized(IEssentialsCommand cmd);

	boolean isAuthorized(IEssentialsCommand cmd, String permissionPrefix);

	Player getBase();

	double getMoney();

	void takeMoney(double value);

	void giveMoney(double value);

	String getGroup();

	void setLastLocation();

	Location getHome(String name) throws Exception;

	Location getHome(Location loc) throws Exception;

	boolean isHidden();

	ITeleport getTeleport();

	void checkCooldown(UserData.TimestampType cooldownType, double cooldown, boolean set, String bypassPermission) throws CooldownException;

	boolean toggleAfk();
	
	void updateActivity(boolean broadcast);
	
	void updateDisplayName();
	
	boolean checkJailTimeout(final long currentTime);
	
	boolean checkMuteTimeout(final long currentTime);
	
	boolean checkBanTimeout(final long currentTime);
	
	void setTimestamp(final UserData.TimestampType name, final long value);
}
