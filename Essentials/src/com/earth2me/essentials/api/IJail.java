package com.earth2me.essentials.api;

import java.util.Collection;
import org.bukkit.Location;


public interface IJail extends IReload
{
	Location getJail(String jailName) throws Exception;

	Collection<String> getJails() throws Exception;

	void removeJail(String jail) throws Exception;

	void sendToJail(IUser user, String jail) throws Exception;

	void setJail(Location loc, String jailName) throws Exception;
}
