package com.earth2me.essentials.api;

import java.util.Collection;
import org.bukkit.Location;


public interface IWarps extends IReload
{
	Location getWarp(String warp) throws Exception;

	Collection<String> getWarps();

	void removeWarp(String name) throws Exception;

	void setWarp(String name, Location loc) throws Exception;
}
