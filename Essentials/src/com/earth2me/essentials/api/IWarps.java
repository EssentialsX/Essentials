package com.earth2me.essentials.api;

import java.io.File;
import java.util.Collection;
import org.bukkit.Location;


public interface IWarps extends IReload
{
	Location getWarp(String warp) throws Exception;

	Collection<String> getList();

	void removeWarp(String name) throws Exception;

	void setWarp(String name, Location loc) throws Exception;

	public boolean isEmpty();

	public File getWarpFile(String name) throws InvalidNameException;
}
